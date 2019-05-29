package silverbars.adapter.api.rest.orders.dto.transformer;

import silverbars.adapter.api.rest.orders.dto.OrderDto;
import silverbars.model.OrderModel;

public class OrderDtoTransformer {

    public OrderDto transform(final OrderModel order) {

        final OrderDto orderDto = new OrderDto();
        orderDto.orderId = order.getOrderId();
        orderDto.orderType = order.getOrderType();
        orderDto.price = order.getPrice();
        orderDto.quantity = order.getQuantity();
        orderDto.userId = order.getUserId();
        return orderDto;
    }

}
