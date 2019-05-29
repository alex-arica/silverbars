package silverbars.adapter.api.rest.orders.dto;

import silverbars.model.OrderType;

public class OrderDto {

    public String orderId;
    public String userId;
    public float quantity;
    public int price;
    public OrderType orderType;

    @Override
    public String toString() {
        return "OrderDto{" +
                "orderId='" + orderId + '\'' +
                ", userId=" + userId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderType=" + orderType +
                '}';
    }
}
