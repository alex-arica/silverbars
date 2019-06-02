package silverbars.adapter.api.rest.orders.dto.transformer;

import org.junit.Test;
import silverbars.adapter.api.rest.orders.dto.OrdersSummaryDto;
import silverbars.model.OrderSummaryModel;
import silverbars.model.OrderType;

import static org.junit.Assert.assertEquals;

public class OrderSummaryDtoTransformerTest {

    private OrderSummaryDtoTransformer classToTest = new OrderSummaryDtoTransformer();

    private final int price = 2;
    private final OrderType orderType = OrderType.BUY;
    private final float quantity = 1.5f;

    @Test
    public void GIVEN_order_summary_model_THEN_transform_to_order_summary_dto() {

        final OrderSummaryModel orderSummaryModel = givenModel();

        final OrdersSummaryDto ordersSummaryDtoToTest = whenModelTransformed(orderSummaryModel);

        thenCheckDtoHasExpectedValues(ordersSummaryDtoToTest);
    }

    private OrderSummaryModel givenModel() {
        return new OrderSummaryModel(price, orderType, quantity);
    }

    private OrdersSummaryDto whenModelTransformed(final OrderSummaryModel orderSummaryModel) {
        return classToTest.transform(orderSummaryModel);
    }

    private void thenCheckDtoHasExpectedValues(final OrdersSummaryDto dtoToTest) {
        assertEquals(price, dtoToTest.price);
        assertEquals(orderType, dtoToTest.orderType);
        assertEquals(Float.valueOf(quantity), Float.valueOf(dtoToTest.quantity));
    }
}
