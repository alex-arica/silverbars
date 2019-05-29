package acceptance;

import acceptance.util.AcceptanceTestService;
import acceptance.util.client.HttpClient;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Test;
import silverbars.Main;
import silverbars.adapter.api.rest.orders.dto.OrderDto;
import silverbars.adapter.db.MemoryBasedDbAdapter;
import silverbars.model.OrderModel;
import silverbars.model.OrderType;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static silverbars.model.OrderType.BUY;

public class RegisterOrderRestAdapterAcceptanceTest {

    private final Gson gson = new Gson();
    private final Set<OrderModel> orders = new HashSet<>();

    @After
    public void afterTest() {
        AcceptanceTestService.stop();
    }

    @Test
    public void test() {

        AcceptanceTestService.start();

        insertOrdersInDb();

        final OrderDto orderDto = new OrderDto();
        orderDto.userId = "user3";
        orderDto.quantity = 4.2f;
        orderDto.price = 329;
        orderDto.orderType = BUY;

        final String orderJson = gson.toJson(orderDto);
        final HttpClient httpClient = new HttpClient();
        final String registeredOrderJson = httpClient.postRequest("http://localhost:26034/order", orderJson);

        final OrderDto registeredOrderDtoToTest = gson.fromJson(registeredOrderJson, OrderDto.class);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        checkRegisteredOrderDto(registeredOrderDtoToTest, orderDto);
        verifyOrderWasAddedInDb(registeredOrderDtoToTest);
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

    private void checkRegisteredOrderDto(final OrderDto registeredOrderDtoToTest,
                                         final OrderDto expectedOrderDto) {

        assertNotNull(registeredOrderDtoToTest.orderId);
        assertEquals(expectedOrderDto.userId, registeredOrderDtoToTest.userId);
        assertEquals(expectedOrderDto.price, registeredOrderDtoToTest.price);
        assertEquals(Float.valueOf(expectedOrderDto.quantity), Float.valueOf(registeredOrderDtoToTest.quantity));
        assertEquals(expectedOrderDto.orderType, registeredOrderDtoToTest.orderType);
    }

    private void verifyOrderWasAddedInDb(final OrderDto registeredOrderDtoToTest) {

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
}
