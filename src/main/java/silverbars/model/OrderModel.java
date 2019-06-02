package silverbars.model;

import java.util.Objects;

// Immutable
public final class OrderModel {

    private final String orderId;
    private final String userId;
    private final float quantity;
    private final int price;
    private final OrderType orderType;

    public OrderModel(final String orderId,
                      final String userId,
                      final float quantity,
                      final int price,
                      final OrderType orderType) {
        this.orderId = orderId;
        this.userId = userId;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public float getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderType=" + orderType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderModel orderModel = (OrderModel) o;
        return Objects.equals(orderId, orderModel.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
