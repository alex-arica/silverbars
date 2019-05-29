package silverbars.adapter.api.rest.orders.dto.transformer;

import silverbars.adapter.api.rest.orders.dto.OrdersSummaryDto;
import silverbars.model.OrderSummaryModel;

public class OrderSummaryDtoTransformer {

    public OrdersSummaryDto transform(final OrderSummaryModel orderSummary) {
        return new OrdersSummaryDto(orderSummary.getOrderType(),
                                    orderSummary.getPrice(),
                                    orderSummary.getQuantity());
    }
}
