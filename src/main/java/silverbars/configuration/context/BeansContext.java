package silverbars.configuration.context;

import silverbars.adapter.api.rest.health.GetHealthRestAdapter;
import silverbars.adapter.api.rest.orders.GetOrdersSummaryRestAdapter;
import silverbars.adapter.api.rest.orders.OrderOperationsRestAdapter;
import silverbars.adapter.api.rest.orders.helper.CancelOrderRestAdapterHelper;
import silverbars.adapter.api.rest.orders.helper.RegisterOrderRestAdapterHelper;
import silverbars.adapter.api.rest.orders.dto.transformer.OrderDtoTransformer;
import silverbars.adapter.api.rest.orders.dto.transformer.OrderSummaryDtoTransformer;
import silverbars.adapter.db.MemoryBasedDbAdapter;
import silverbars.service.OrderRegistrationService;
import silverbars.service.OrdersSummaryService;
import silverbars.util.http.encoding.EncodeStringTo64BitsUtil;
import silverbars.util.http.response.HttpResponseHelper;
import silverbars.util.http.server.HttpServer;

public class BeansContext {

    public HttpServerConfig httpServerConfig;

    public HttpServer httpServer;
    public HttpResponseHelper httpResponseHelper;
    public EncodeStringTo64BitsUtil encodeStringTo64BitsUtil;
    public GetHealthRestAdapter getHealthRestAdapter;

    public OrderDtoTransformer orderDtoTransformer;
    public OrderSummaryDtoTransformer orderSummaryDtoTransformer;

    public MemoryBasedDbAdapter memoryBasedDbAdapter;
    public OrderRegistrationService orderRegistrationService;
    public OrdersSummaryService ordersSummaryService;

    public RegisterOrderRestAdapterHelper registerOrderRestAdapterHelper;
    public CancelOrderRestAdapterHelper cancelOrderRestAdapterHelper;
    public OrderOperationsRestAdapter orderOperationsRestAdapter;
    public GetOrdersSummaryRestAdapter getOrdersSummaryRestAdapter;

    public void createBeans() {

        httpServerConfig = new HttpServerConfig();

        httpServer = new HttpServer(httpServerConfig);
        httpResponseHelper = new HttpResponseHelper();
        encodeStringTo64BitsUtil = new EncodeStringTo64BitsUtil();
        getHealthRestAdapter = new GetHealthRestAdapter(httpResponseHelper);

        orderDtoTransformer = new OrderDtoTransformer();
        orderSummaryDtoTransformer = new OrderSummaryDtoTransformer();

        memoryBasedDbAdapter = new MemoryBasedDbAdapter();
        orderRegistrationService = new OrderRegistrationService(memoryBasedDbAdapter, encodeStringTo64BitsUtil);
        ordersSummaryService = new OrdersSummaryService(memoryBasedDbAdapter);

        registerOrderRestAdapterHelper = new RegisterOrderRestAdapterHelper(orderRegistrationService, httpResponseHelper, orderDtoTransformer);
        cancelOrderRestAdapterHelper = new CancelOrderRestAdapterHelper(orderRegistrationService, httpResponseHelper, orderDtoTransformer);
        orderOperationsRestAdapter = new OrderOperationsRestAdapter(registerOrderRestAdapterHelper, cancelOrderRestAdapterHelper);
        getOrdersSummaryRestAdapter = new GetOrdersSummaryRestAdapter(ordersSummaryService, httpResponseHelper, orderSummaryDtoTransformer);
    }
}
