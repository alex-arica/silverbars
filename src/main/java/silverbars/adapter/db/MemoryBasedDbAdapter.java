package silverbars.adapter.db;

import silverbars.model.OrderModel;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryBasedDbAdapter {

    private final Map<String, OrderModel> orders = new ConcurrentHashMap(1000);

    public Set<OrderModel> getAll() {
        return new HashSet<>(orders.values());
    }

    public void register(final OrderModel orderModel) {
        if (orderModel.getOrderId() == null) throw new IllegalArgumentException("A non-null orderId is expected to register an order.");

        orders.put(orderModel.getOrderId(), orderModel);
    }

    public Optional<OrderModel> removeById(final String orderId) {
        if (orderId == null) throw new IllegalArgumentException("A non-null orderId is expected to remove an order.");

        final OrderModel removedOrder = orders.remove(orderId);
        return Optional.ofNullable(removedOrder);
    }
}
