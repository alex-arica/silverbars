package silverbars.adapter.api.rest.health;

import silverbars.util.http.response.HttpResponseHelper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetHealthRestAdapter extends HttpServlet {

    private final HttpResponseHelper httpResponseHelper;

    public GetHealthRestAdapter(final HttpResponseHelper httpResponseHelper) {
        this.httpResponseHelper = httpResponseHelper;
    }

    @Override
    protected void doGet(final HttpServletRequest httpRequest,
                         final HttpServletResponse httpResponse) throws IOException {
        httpResponseHelper.setHttpStatusToSuccess(httpResponse);
        httpResponseHelper.respondWithHtml("ok", httpResponse);
    }

}
