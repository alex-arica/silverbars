package silverbars.adapter.api.rest.orders.dto.transformer;

import org.junit.Test;
import silverbars.adapter.api.rest.orders.dto.OrderDto;
import silverbars.model.OrderModel;
import silverbars.model.OrderType;

import static org.junit.Assert.assertEquals;

public class OrderDtoTransformerTest {

    private OrderDtoTransformer classToTest = new OrderDtoTransformer();

    private final String orderId = "orderId";
    private final String userId = "userId";
    private final float quantity = 1.5f;
    private final int price = 2;
    private final OrderType orderType = OrderType.BUY;

    @Test
    public void GIVEN_order_model_THEN_transform_to_order_dto() {

        final OrderModel orderModel = givenModel();

        final OrderDto orderDtoToTest = whenModelTransformed(orderModel);

        thenCheckDtoHasExpectedValues(orderDtoToTest);
    }

    private OrderModel givenModel() {
        return new OrderModel(orderId, userId, quantity, price, orderType);
    }

    private OrderDto whenModelTransformed(final OrderModel orderModel) {
        return classToTest.transform(orderModel);
    }

    private void thenCheckDtoHasExpectedValues(final OrderDto dtoToTest) {
        assertEquals(orderId, dtoToTest.orderId);
        assertEquals(userId, dtoToTest.userId);
        assertEquals(Float.valueOf(quantity), Float.valueOf(dtoToTest.quantity));
        assertEquals(price, dtoToTest.price);
        assertEquals(orderType, dtoToTest.orderType);
    }
}
