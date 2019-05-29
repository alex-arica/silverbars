package silverbars.adapter.api.rest.orders.dto;

import silverbars.model.OrderType;

public class OrdersSummaryDto {

    public OrderType orderType;
    public int price;
    public float quantity;

    public OrdersSummaryDto(final OrderType orderType,
                            final int price,
                            final float quantity) {
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrdersSummaryDto{" +
                "orderType=" + orderType +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
