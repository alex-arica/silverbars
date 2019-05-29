package silverbars.adapter.api.rest.orders;

import silverbars.adapter.api.rest.orders.helper.CancelOrderRestAdapterHelper;
import silverbars.adapter.api.rest.orders.helper.RegisterOrderRestAdapterHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderOperationsRestAdapter extends HttpServlet {

    private final RegisterOrderRestAdapterHelper registerOrderRestAdapterHelper;
    private final CancelOrderRestAdapterHelper cancelOrderRestAdapterHelper;

    public OrderOperationsRestAdapter(final RegisterOrderRestAdapterHelper registerOrderRestAdapterHelper,
                                      final CancelOrderRestAdapterHelper cancelOrderRestAdapterHelper) {
        this.registerOrderRestAdapterHelper = registerOrderRestAdapterHelper;
        this.cancelOrderRestAdapterHelper = cancelOrderRestAdapterHelper;
    }

    @Override
    protected void doPost(final HttpServletRequest httpRequest,
                          final HttpServletResponse httpResponse) throws IOException {
        registerOrderRestAdapterHelper.handlePost(httpRequest, httpResponse);
    }

    @Override
    protected void doDelete(final HttpServletRequest httpRequest,
                            final HttpServletResponse httpResponse) throws IOException {
        cancelOrderRestAdapterHelper.handleDelete(httpRequest, httpResponse);
    }

}
