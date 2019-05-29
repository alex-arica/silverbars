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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CancelOrderRestAdapterAcceptanceTest {

    private final Gson gson = new Gson();
    final Set<OrderModel> orders = new HashSet<>();

    @After
    public void afterTest() {
        AcceptanceTestService.stop();
    }

    @Test
    public void test() {

        AcceptanceTestService.start();

        insertOrdersInDb();

        final OrderModel expectedOrderToRemove = orders.iterator().next();

        final HttpClient httpClient = new HttpClient();
        final String removedOrderJson = httpClient.deleteRequest("http://localhost:26034/order?orderId=" + expectedOrderToRemove.getOrderId());

        final OrderDto removedOrderDtoToTest = gson.fromJson(removedOrderJson, OrderDto.class);

        checkRemovedOrderDto(removedOrderDtoToTest, expectedOrderToRemove);
        verifyOrderWasRemovedFromDb(expectedOrderToRemove);
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

    private void checkRemovedOrderDto(final OrderDto removedOrderDto,
                                      final OrderModel expectedRemovedOrder) {

        assertEquals(expectedRemovedOrder.getOrderId(), removedOrderDto.orderId);
        assertEquals(expectedRemovedOrder.getUserId(), removedOrderDto.userId);
        assertEquals(expectedRemovedOrder.getPrice(), removedOrderDto.price);
        assertEquals(Float.valueOf(expectedRemovedOrder.getQuantity()), Float.valueOf(removedOrderDto.quantity));
        assertEquals(expectedRemovedOrder.getOrderType(), removedOrderDto.orderType);
    }

    private void verifyOrderWasRemovedFromDb(final OrderModel expectedOrderToRemove) {
        final Set<OrderModel> registeredOrders = Main.beansContext.memoryBasedDbAdapter.getAll();
        assertEquals(1, registeredOrders.size());
        assertFalse(registeredOrders.contains(expectedOrderToRemove));
    }
}
