package silverbars.service;

import silverbars.adapter.db.MemoryBasedDbAdapter;
import silverbars.model.OrderModel;
import silverbars.model.OrderType;
import silverbars.util.http.encoding.EncodeStringTo64BitsUtil;

import java.time.LocalTime;
import java.util.Optional;

public class OrderRegistrationService {

    private final MemoryBasedDbAdapter memoryBasedDbAdapter;
    private final EncodeStringTo64BitsUtil encodeStringTo64BitsUtil;

    public OrderRegistrationService(final MemoryBasedDbAdapter memoryBasedDbAdapter,
                                    final EncodeStringTo64BitsUtil encodeStringTo64BitsUtil) {
        this.memoryBasedDbAdapter = memoryBasedDbAdapter;
        this.encodeStringTo64BitsUtil = encodeStringTo64BitsUtil;
    }

    public OrderModel registerOrder(final LocalTime creationTime,
                                    final String userId,
                                    final float quantity,
                                    final int price,
                                    final OrderType orderType) {

        final String orderId = generateOrderId(creationTime, userId, quantity, price, orderType);
        final OrderModel orderModel = new OrderModel(orderId, userId, quantity, price, orderType);
        memoryBasedDbAdapter.register(orderModel);

        return orderModel;
    }

    public Optional<OrderModel> removeOrderById(final String orderId) {
        return memoryBasedDbAdapter.removeById(orderId);
    }

    private String generateOrderId(final LocalTime creationTime,
                                   final String userId,
                                   final float quantity,
                                   final int price,
                                   final OrderType orderType) {
        final String orderIdStr = creationTime.toString() + userId + quantity + price + orderType;
        return encodeStringTo64BitsUtil.encode(orderIdStr);
    }
}
