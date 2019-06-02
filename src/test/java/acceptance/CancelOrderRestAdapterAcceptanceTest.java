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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class CancelOrderRestAdapterAcceptanceTest {

    private final Gson gson = new Gson();
    private final List<OrderModel> orders = new ArrayList<>();

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
    public void GIVEN_order_id_to_cancel_WHEN_http_delete_request_sent_THEN_order_should_be_deleted_from_db_AND_deleted_order_dto_should_be_returned() {

        final OrderModel orderToCancel = givenOrderToCancel();

        final OrderDto canceledOrderDtoToTest = whenHttpDeleteRequestSent(orderToCancel.getOrderId());

        thenOrderShouldBeRemovedFromDb();
        checkCancelledOrderDto(canceledOrderDtoToTest, orderToCancel);
    }

    @Test
    public void GIVEN_order_id_is_missing_WHEN_http_delete_request_sent_THEN_HTTP_400_should_be_returned_with_ErrorDto() {

        final HttpClientException httpClientException = whenHttpDeleteRequestReceivedErrorResponse(null);

        final String expectedErrorMessage = "The DELETE parameter orderId is expected.";
        thenResponseShouldBeHttp400WithErrorDto(httpClientException, expectedErrorMessage);
    }

    @Test
    public void GIVEN_order_id_does_not_exist_in_DB_WHEN_http_delete_request_sent_THEN__HTTP_400_should_be_returned_with_ErrorDto() {

        final String orderIdToCancel = givenOrderIdDoesNotExistInDb();

        final HttpClientException httpClientException = whenHttpDeleteRequestReceivedErrorResponse(orderIdToCancel);

        final String expectedErrorMessage = "The given orderId: " + orderIdToCancel + " does not match with a registered order. We cannot cancel it.";
        thenResponseShouldBeHttp400WithErrorDto(httpClientException, expectedErrorMessage);
    }

    private void insertOrdersInDb() {
        final MemoryBasedDbAdapter memoryBasedDbAdapter = Main.beansContext.memoryBasedDbAdapter;
        givenOrders().stream().forEach(order -> memoryBasedDbAdapter.register(order));
    }

    private List<OrderModel> givenOrders() {
        orders.add(new OrderModel("ord1", "user1", 3.5f, 306, OrderType.SELL));
        orders.add(new OrderModel("ord2", "user2", 1.2f, 310, OrderType.SELL));
        return orders;
    }

    private String givenOrderIdDoesNotExistInDb() {
        return "12345678";
    }

    private OrderModel givenOrderToCancel() {
        return orders.get(0);
    }

    private OrderDto whenHttpDeleteRequestSent(final String orderIdToCancel) {
        final HttpClient httpClient = new HttpClient();
        final String canceledOrderJson = httpClient.deleteRequest("http://localhost:26034/order?orderId=" + orderIdToCancel);
        return gson.fromJson(canceledOrderJson, OrderDto.class);
    }

    private HttpClientException whenHttpDeleteRequestReceivedErrorResponse(final String orderIdToCancel) {

        final HttpClient httpClient = new HttpClient();

        try {
            final String orderIdParam = (orderIdToCancel != null) ? "?orderId=" + orderIdToCancel : "";
            httpClient.deleteRequest("http://localhost:26034/order" + orderIdParam);
        } catch (HttpClientException ex) {
            return ex;
        }

        return null;
    }

    private void checkCancelledOrderDto(final OrderDto canceledOrderDtoToTest,
                                        final OrderModel expectedOrder) {

        assertEquals(expectedOrder.getOrderId(), canceledOrderDtoToTest.orderId);
        assertEquals(expectedOrder.getUserId(), canceledOrderDtoToTest.userId);
        assertEquals(expectedOrder.getPrice(), canceledOrderDtoToTest.price);
        assertEquals(Float.valueOf(expectedOrder.getQuantity()), Float.valueOf(canceledOrderDtoToTest.quantity));
        assertEquals(expectedOrder.getOrderType(), canceledOrderDtoToTest.orderType);
    }

    private void thenOrderShouldBeRemovedFromDb() {

        final OrderModel expectedOrderToCancel = givenOrderToCancel();

        final Set<OrderModel> registeredOrders = Main.beansContext.memoryBasedDbAdapter.getAll();
        assertEquals(1, registeredOrders.size());
        assertFalse(registeredOrders.contains(expectedOrderToCancel));
    }

    private void thenResponseShouldBeHttp400WithErrorDto(final HttpClientException httpClientException,
                                                         final String expectedErrorMessage) {
        assertNotNull(httpClientException);
        assertEquals(400, httpClientException.getHttpResponseCode());
        final ErrorDto errorDto = gson.fromJson(httpClientException.getResponse(), ErrorDto.class);
        assertEquals(expectedErrorMessage, errorDto.message);
    }
}