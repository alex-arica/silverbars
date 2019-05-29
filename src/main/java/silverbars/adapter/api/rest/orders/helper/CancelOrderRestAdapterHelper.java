package silverbars.adapter.api.rest.orders.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import silverbars.adapter.api.rest.orders.dto.ErrorDto;
import silverbars.adapter.api.rest.orders.dto.OrderDto;
import silverbars.adapter.api.rest.orders.dto.transformer.OrderDtoTransformer;
import silverbars.model.OrderModel;
import silverbars.service.OrderRegistrationService;
import silverbars.util.http.response.HttpResponseHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class CancelOrderRestAdapterHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderRestAdapterHelper.class);

    private final OrderRegistrationService orderRegistrationService;
    private final HttpResponseHelper httpResponseHelper;
    private final OrderDtoTransformer orderDtoTransformer;

    public CancelOrderRestAdapterHelper(final OrderRegistrationService orderRegistrationService,
                                        final HttpResponseHelper httpResponseHelper,
                                        final OrderDtoTransformer orderDtoTransformer) {
        this.orderRegistrationService = orderRegistrationService;
        this.httpResponseHelper = httpResponseHelper;
        this.orderDtoTransformer = orderDtoTransformer;
    }

    public void handleDelete(final HttpServletRequest httpRequest,
                             final HttpServletResponse httpResponse) throws IOException {

        final String orderId = httpRequest.getParameter("orderId");

        LOGGER.info("Received request to cancel the orderId: {}", orderId);

        if (isInvalidOrderId(orderId)) {
            LOGGER.info("The orderId is null or empty.");
            respondWithBadRequestMsg(httpResponse, "The DELETE parameter orderId is expected.");
            return;
        }

        final Optional<OrderModel> cancelledOrderOpt = cancelOrder(orderId);

        if (!cancelledOrderOpt.isPresent()) {
            LOGGER.info("The orderId does not exist in DB.");
            respondWithBadRequestMsg(httpResponse, "The given orderId: " + orderId + " does not match with a registered order. We cannot cancel it.");
            return;
        }

        final OrderDto orderDto = transform(cancelledOrderOpt.get());

        LOGGER.info("Order cancelled: {}", orderDto);

        respondWithCancelledOrderDto(httpResponse, orderDto);
    }

    private Optional<OrderModel> cancelOrder(final String orderId) {
        return orderRegistrationService.removeOrderById(orderId);
    }

    private OrderDto transform(final OrderModel cancelledOrder) {
        return orderDtoTransformer.transform(cancelledOrder);
    }

    private boolean isInvalidOrderId(final String orderId) {
        return orderId == null || orderId.isEmpty();
    }

    private void respondWithBadRequestMsg(final HttpServletResponse httpResponse,
                                          final String message) throws IOException {
        final ErrorDto errorDto = new ErrorDto(message);
        httpResponseHelper.setHttpStatusToBadRequest(httpResponse);
        httpResponseHelper.respondWithJson(errorDto, httpResponse);
    }

    private void respondWithCancelledOrderDto(final HttpServletResponse httpResponse,
                                              final OrderDto orderDto) throws IOException {
        httpResponseHelper.setHttpStatusToSuccess(httpResponse);
        httpResponseHelper.respondWithJson(orderDto, httpResponse);
    }
}
