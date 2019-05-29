package silverbars.adapter.db;

import org.junit.Test;
import silverbars.model.OrderModel;
import silverbars.model.OrderType;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MemoryBasedDbAdapterTest {

    private MemoryBasedDbAdapter classToTest = new MemoryBasedDbAdapter();

    private final OrderModel order1 = new OrderModel("ord1", "user1", 3.5f, 306, OrderType.SELL);
    private final OrderModel order2 = new OrderModel("ord2", "user2", 1.2f, 310, OrderType.SELL);

    @Test
    public void test_register_and_getAll() {

        classToTest.register(order1);
        classToTest.register(order2);

        final Set<OrderModel> ordersFromDb = classToTest.getAll();

        assertEquals(2, ordersFromDb.size());
        assertTrue(ordersFromDb.contains(order1));
        assertTrue(ordersFromDb.contains(order2));
    }

    @Test
    public void test_register_and_remove_and_getAll() {

        classToTest.register(order1);
        classToTest.register(order2);

        classToTest.removeById(order1.getOrderId());

        final Set<OrderModel> ordersFromDb = classToTest.getAll();

        assertEquals(1, ordersFromDb.size());
        assertTrue(ordersFromDb.contains(order2));
    }
}
