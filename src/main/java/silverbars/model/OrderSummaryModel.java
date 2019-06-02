package silverbars.model;

import java.util.Objects;

// Immutable
public final class OrderSummaryModel {

    private final int price;
    private final OrderType orderType;
    private final float quantity;

    public OrderSummaryModel(final int price,
                             final OrderType orderType,
                             final float quantity) {
        this.price = price;
        this.orderType = orderType;
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public float getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "OrderSummaryModel{" +
                "price=" + price +
                ", orderType=" + orderType +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderSummaryModel that = (OrderSummaryModel) o;
        return price == that.price &&
                Float.compare(that.quantity, quantity) == 0 &&
                orderType == that.orderType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, orderType, quantity);
    }
}
