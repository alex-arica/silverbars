package silverbars.service;

import silverbars.adapter.db.MemoryBasedDbAdapter;
import silverbars.model.OrderModel;
import silverbars.model.OrderSummaryModel;
import silverbars.model.OrderType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static silverbars.model.OrderType.BUY;
import static silverbars.model.OrderType.SELL;

public class OrdersSummaryService {

    private final MemoryBasedDbAdapter memoryBasedDbAdapter;

    public OrdersSummaryService(final MemoryBasedDbAdapter memoryBasedDbAdapter) {
        this.memoryBasedDbAdapter = memoryBasedDbAdapter;
    }

    public List<OrderSummaryModel> getSummary() {
        final Set<OrderModel> orderModels = getOrdersFromDb();
        return createOrdersSummary(orderModels);
    }

    private Set<OrderModel> getOrdersFromDb() {
        return memoryBasedDbAdapter.getAll();
    }

    private List<OrderSummaryModel> createOrdersSummary(final Set<OrderModel> orders) {

        return orders.stream()
                .collect(Collectors.groupingBy(OrderModel::getPrice))
                .entrySet().stream()
                    .map(entry -> createSummaryForPrice(entry.getKey(), entry.getValue()))
                    .sorted(this::compareByTypeAndPrice)
                    .collect(Collectors.toList());
    }

    private OrderSummaryModel createSummaryForPrice(final Integer price,
                                                    final List<OrderModel> orders) {
        final double sumOfQuantity =
                orders.stream()
                .mapToDouble(order -> order.getOrderType() == BUY ? order.getQuantity() : -order.getQuantity())
                .sum();

        final OrderType orderType = sumOfQuantity > 0 ? BUY : SELL;
        final float quantity = (float) Math.abs(sumOfQuantity);

        return new OrderSummaryModel(price, orderType, quantity);
    }

    private int compareByTypeAndPrice(final OrderSummaryModel orderSummary1,
                                      final OrderSummaryModel orderSummary2) {

        if (orderSummary1.getOrderType() != orderSummary2.getOrderType()) {
            return orderSummary1.getOrderType() == SELL ? -1 : 1;
        }

        if (orderSummary1.getOrderType() == SELL) {
            return Integer.compare(orderSummary1.getPrice(), orderSummary2.getPrice());
        } else {
            return Integer.compare(orderSummary2.getPrice(), orderSummary1.getPrice());
        }
    }
}
