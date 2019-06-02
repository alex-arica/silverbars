package acceptance;

import acceptance.util.AcceptanceTestService;
import acceptance.util.client.HttpClient;
import acceptance.util.client.HttpClientException;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import silverbars.Main;
import silverbars.adapter.api.rest.orders.dto.ErrorDto;
import silverbars.adapter.api.rest.orders.dto.OrderDto;
import silverbars.adapter.db.MemoryBasedDbAdapter;
import silverbars.model.OrderModel;
import silverbars.model.OrderType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static silverbars.model.OrderType.BUY;

public class RegisterOrderRestAdapterAcceptanceTest {

    private final Gson gson = new Gson();
    private final Set<OrderModel> orders = new HashSet<>();

    @Before
    public void beforeTest() {
        AcceptanceTestService.start();
        insertOrdersInDb();
    }

    @After
    public void afterTest() {
        AcceptanceTestService.stop();
    }

    @Test
    public void GIVEN_order_dto_to_register_WHEN_http_post_request_sent_THEN_order_should_be_saved_in_db_AND_order_dto_should_be_returned_with_and_order_id() {

        final OrderDto orderDtoToRegister = givenOrderDtoToRegister();

        final OrderDto registeredOrderDtoToTest = whenHttpPostRequestSent(orderDtoToRegister);

        thenOrderShouldBeSavedInDb(registeredOrderDtoToTest);
        thenReturnedOrderDtoShouldHaveOrderId(registeredOrderDtoToTest, orderDtoToRegister);
    }

    @Test
    public void GIVEN_order_dto_is_missing_WHEN_http_post_request_sent_THEN_HTTP_400_should_be_returned_with_ErrorDto() {

        final HttpClientException httpClientException = whenHttpPostRequestReceivedErrorResponse(null);

        final String expectedErrorMessage = "The given OrderDto cannot be null.";
        thenResponseShouldBeHttp400WithErrorDto(httpClientException, expectedErrorMessage);
    }

    @Test
    public void GIVEN_order_dto_without_user_id_WHEN_http_post_request_sent_THEN_HTTP_400_should_be_returned_with_ErrorDto() {

        final OrderDto orderDtoWithoutUsedId = givenOrderDtoToRegisterWithoutUserId();
        final HttpClientException httpClientException = whenHttpPostRequestReceivedErrorResponse(orderDtoWithoutUsedId);

        final String expectedErrorMessage = "The given OrderDto does not have a valid userId.";
        thenResponseShouldBeHttp400WithErrorDto(httpClientException, expectedErrorMessage);
    }

    @Test
    public void GIVEN_order_dto_without_order_type_WHEN_http_post_request_sent_THEN_HTTP_400_should_be_returned_with_ErrorDto() {

        final OrderDto orderDtoWithoutOrderType = givenOrderDtoToRegisterWithoutOrderType();
        final HttpClientException httpClientException = whenHttpPostRequestReceivedErrorResponse(orderDtoWithoutOrderType);

        final String expectedErrorMessage = "The given OrderDto does not have a valid orderType. Expected values: " + getValidOrderTypes();
        thenResponseShouldBeHttp400WithErrorDto(httpClientException, expectedErrorMessage);
    }

    private void insertOrdersInDb() {
        final MemoryBasedDbAdapter memoryBasedDbAdapter = Main.beansContext.memoryBasedDbAdapter;
        givenOrders().stream().forEach(order -> memoryBasedDbAdapter.register(order));
    }

    private Set<OrderModel> givenOrders() {
        orders.add(new OrderModel("ord1", "user1", 3.5f, 306, OrderType.SELL));
        orders.add(new OrderModel("ord2", "user2", 1.2f, 310, OrderType.SELL));
        return orders;
    }

    private OrderDto givenOrderDtoToRegister() {
        final OrderDto orderDto = new OrderDto();
        orderDto.userId = "user3";
        orderDto.quantity = 4.2f;
        orderDto.price = 329;
        orderDto.orderType = BUY;
        return orderDto;
    }

    private OrderDto givenOrderDtoToRegisterWithoutUserId() {
        final OrderDto orderDto = givenOrderDtoToRegister();
        orderDto.userId = null;
        return orderDto;
    }

    private OrderDto givenOrderDtoToRegisterWithoutOrderType() {
        final OrderDto orderDto = givenOrderDtoToRegister();
        orderDto.orderType = null;
        return orderDto;
    }

    private OrderDto whenHttpPostRequestSent(final OrderDto orderDtoToRegister) {

        final String orderJson = gson.toJson(orderDtoToRegister);

        final HttpClient httpClient = new HttpClient();
        final String registeredOrderJson = httpClient.postRequest("http://localhost:26034/order", orderJson);

        return gson.fromJson(registeredOrderJson, OrderDto.class);
    }

    private HttpClientException whenHttpPostRequestReceivedErrorResponse(final OrderDto orderDtoToRegister) {

        final HttpClient httpClient = new HttpClient();

        try {
            final String orderJson = gson.toJson(orderDtoToRegister);
            httpClient.postRequest("http://localhost:26034/order", orderJson);

        } catch (HttpClientException ex) {
            return ex;
        }

        return null;
    }

    private void thenOrderShouldBeSavedInDb(final OrderDto registeredOrderDtoToTest) {

        final OrderModel expectedRegisteredOrder = convertOrderDtoToModel(registeredOrderDtoToTest);

        final Set<OrderModel> registeredOrders = Main.beansContext.memoryBasedDbAdapter.getAll();
        assertEquals(orders.size() + 1, registeredOrders.size());
        assertTrue(registeredOrders.contains(expectedRegisteredOrder));
    }

    private OrderModel convertOrderDtoToModel(final OrderDto orderDto) {
        return new OrderModel(orderDto.orderId,
                              orderDto.userId,
                              orderDto.quantity,
                              orderDto.price,
                              orderDto.orderType);
    }

    private void thenReturnedOrderDtoShouldHaveOrderId(final OrderDto registeredOrderDtoToTest,
                                                       final OrderDto expectedOrderDto) {

        assertNotNull(registeredOrderDtoToTest.orderId);

        assertEquals(expectedOrderDto.userId, registeredOrderDtoToTest.userId);
        assertEquals(expectedOrderDto.price, registeredOrderDtoToTest.price);
        assertEquals(Float.valueOf(expectedOrderDto.quantity), Float.valueOf(registeredOrderDtoToTest.quantity));
        assertEquals(expectedOrderDto.orderType, registeredOrderDtoToTest.orderType);
    }

    private void thenResponseShouldBeHttp400WithErrorDto(final HttpClientException httpClientException,
                                                         final String expectedErrorMessage) {
        assertNotNull(httpClientException);
        assertEquals(400, httpClientException.getHttpResponseCode());
        final ErrorDto errorDto = gson.fromJson(httpClientException.getResponse(), ErrorDto.class);
        assertEquals(expectedErrorMessage, errorDto.message);
    }

    private String getValidOrderTypes() {
        return Arrays.stream(OrderType.values())
                .map(orderType -> orderType.name())
                .collect(Collectors.joining(", "));
    }
}
