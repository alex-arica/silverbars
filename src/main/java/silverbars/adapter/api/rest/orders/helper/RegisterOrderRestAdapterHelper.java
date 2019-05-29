package silverbars.adapter.api.rest.orders.helper;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import silverbars.adapter.api.rest.orders.dto.ErrorDto;
import silverbars.adapter.api.rest.orders.dto.OrderDto;
import silverbars.adapter.api.rest.orders.dto.transformer.OrderDtoTransformer;
import silverbars.model.OrderModel;
import silverbars.model.OrderType;
import silverbars.service.OrderRegistrationService;
import silverbars.util.http.response.HttpResponseHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RegisterOrderRestAdapterHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterOrderRestAdapterHelper.class);

    private final OrderRegistrationService orderRegistrationService;
    private final HttpResponseHelper httpResponseHelper;
    private final OrderDtoTransformer orderDtoTransformer;
    private final Gson gson = new Gson();

    public RegisterOrderRestAdapterHelper(final OrderRegistrationService orderRegistrationService,
                                          final HttpResponseHelper httpResponseHelper,
                                          final OrderDtoTransformer orderDtoTransformer) {
        this.orderRegistrationService = orderRegistrationService;
        this.httpResponseHelper = httpResponseHelper;
        this.orderDtoTransformer = orderDtoTransformer;
    }

    public void handlePost(final HttpServletRequest httpRequest,
                           final HttpServletResponse httpResponse) throws IOException {

        LOGGER.info("Received request to register an order");

        final String requestBody = getRequestBody(httpRequest);
        final OrderDto orderDto;

        try {
            orderDto = extractOrderDtoFromBody(requestBody);

            LOGGER.info("Order to register: {}", orderDto);

        } catch (IllegalArgumentException ex) {
            LOGGER.info("Validation error. Given message: {}", ex.getMessage());
            respondWithBadRequestMsg(httpResponse, ex);
            return;
        }

        final OrderModel registeredOrder = registerOrder(orderDto);

        final OrderDto registeredOrderDto = transform(registeredOrder);

        LOGGER.info("Order registered with orderId: {}", registeredOrderDto.orderId);

        respondWithRegisteredOrderDto(httpResponse, registeredOrderDto);
    }

    private String getRequestBody(final HttpServletRequest httpRequest) throws IOException {
        return httpRequest.getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private OrderDto extractOrderDtoFromBody(final String requestBody) {

        checkBodyIsNotEmpty(requestBody);

        try {
            final OrderDto orderDto = gson.fromJson(requestBody, OrderDto.class);
            validateOrderDtoFields(orderDto);
            return orderDto;

        } catch (Exception ex) {
            throw new IllegalArgumentException("The given JSON is not in the expected format " +
                                                "and cannot be transformed into an OrderDto. " +
                                                "Given error: " + ex.getMessage());
        }
    }

    private void checkBodyIsNotEmpty(final String requestBody) {
        if (requestBody == null || requestBody.isEmpty()) {
            throw new IllegalArgumentException("A JSON with an OrderDto is expected in the body of the POST request.");
        }
    }

    private void validateOrderDtoFields(final OrderDto orderDto) {

        if (orderDto == null) {
            throw new IllegalArgumentException("The given OrderDto cannot be null.");
        }

        if (orderDto.userId == null || orderDto.userId.isEmpty()) {
            throw new IllegalArgumentException("The given OrderDto does not have a valid userId.");
        }

        if (orderDto.quantity == 0) {
            throw new IllegalArgumentException("The given OrderDto does not have a valid quantity.");
        }

        if (orderDto.price == 0) {
            throw new IllegalArgumentException("The given OrderDto does not have a valid price.");
        }

        if (orderDto.orderType == null) {
            throw new IllegalArgumentException("The given OrderDto does not have a valid orderType. Expected: " + getValidOrderTypes());
        }
    }

    private String getValidOrderTypes() {
        return Arrays.stream(OrderType.values())
                .map(orderType -> orderType.name())
                .collect(Collectors.joining(", "));
    }

    private OrderModel registerOrder(final OrderDto orderDto) {
        final LocalTime creationTime = LocalTime.now();
        return orderRegistrationService.registerOrder(creationTime,
                                                      orderDto.userId,
                                                      orderDto.quantity,
                                                      orderDto.price,
                                                      orderDto.orderType);
    }

    private OrderDto transform(final OrderModel registeredOrder) {
        return orderDtoTransformer.transform(registeredOrder);
    }

    private void respondWithBadRequestMsg(final HttpServletResponse httpResponse,
                                          final IllegalArgumentException ex) throws IOException {
        final ErrorDto errorDto = new ErrorDto(ex.getMessage());
        httpResponseHelper.setHttpStatusToBadRequest(httpResponse);
        httpResponseHelper.respondWithJson(errorDto, httpResponse);
    }

    private void respondWithRegisteredOrderDto(final HttpServletResponse httpResponse,
                                               final OrderDto orderDto) throws IOException {
        httpResponseHelper.setHttpStatusToSuccess(httpResponse);
        httpResponseHelper.respondWithJson(orderDto, httpResponse);
    }
}
