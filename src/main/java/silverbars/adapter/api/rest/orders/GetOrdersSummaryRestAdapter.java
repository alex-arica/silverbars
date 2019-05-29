package silverbars.adapter.api.rest.orders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import silverbars.adapter.api.rest.orders.dto.OrdersSummaryDto;
import silverbars.adapter.api.rest.orders.dto.transformer.OrderSummaryDtoTransformer;
import silverbars.model.OrderSummaryModel;
import silverbars.service.OrdersSummaryService;
import silverbars.util.http.response.HttpResponseHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GetOrdersSummaryRestAdapter extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOrdersSummaryRestAdapter.class);

    private final OrdersSummaryService ordersSummaryService;
    private final HttpResponseHelper httpResponseHelper;
    private final OrderSummaryDtoTransformer orderSummaryDtoTransformer;

    public GetOrdersSummaryRestAdapter(final OrdersSummaryService ordersSummaryService,
                                       final HttpResponseHelper httpResponseHelper,
                                       final OrderSummaryDtoTransformer orderSummaryDtoTransformer) {
        this.ordersSummaryService = ordersSummaryService;
        this.httpResponseHelper = httpResponseHelper;
        this.orderSummaryDtoTransformer = orderSummaryDtoTransformer;
    }

    @Override
    protected void doGet(final HttpServletRequest httpRequest,
                         final HttpServletResponse httpResponse) throws IOException {

        LOGGER.info("Received request to get orders summary");

        final List<OrderSummaryModel> ordersSummary = getSummary();
        final List<OrdersSummaryDto> ordersSummaryDto = transform(ordersSummary);

        LOGGER.info("Returning orders summary");
        LOGGER.debug("Orders summary: {}", ordersSummaryDto);

        httpResponseHelper.setHttpStatusToSuccess(httpResponse);
        httpResponseHelper.respondWithJson(ordersSummaryDto, httpResponse);
    }

    private List<OrderSummaryModel> getSummary() {
        return ordersSummaryService.getSummary();
    }

    private List<OrdersSummaryDto> transform(final List<OrderSummaryModel> ordersSummary) {
        return ordersSummary.stream()
                .map(orderSummary -> orderSummaryDtoTransformer.transform(orderSummary))
                .collect(Collectors.toList());
    }
}
