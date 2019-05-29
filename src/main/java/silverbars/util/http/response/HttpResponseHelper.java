package silverbars.util.http.response;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpResponseHelper {

    private final Gson gson = new Gson();

    public void setHttpStatusToSuccess(final HttpServletResponse httpResponse) {
        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }

    public void setHttpStatusToBadRequest(final HttpServletResponse httpResponse) {
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    public void setHttpStatusToNotFound(final HttpServletResponse httpResponse) {
        httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    public void setHttpStatusToServerError(final HttpServletResponse httpResponse) {
        httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public void respondWithJson(final Object jsonObject,
                                final HttpServletResponse httpResponse) throws IOException {
        final String response = gson.toJson(jsonObject);
        respondWithContent("application/json; charset=utf-8", response, httpResponse);
    }

    public void respondWithHtml(final String htmlContent,
                                final HttpServletResponse httpResponse) throws IOException {
        respondWithContent("text/html; charset=utf-8", htmlContent, httpResponse);
    }

    public void respondWithFile(final String fileContent,
                                final String fileName,
                                final String contentType,
                                final HttpServletResponse httpResponse) throws IOException {
        httpResponse.setHeader(HttpHeaderType.CONTENT_DISPOSITION.getHeaderName(), "attachment;filename=" + fileName);
        respondWithContent(contentType + "; charset=utf-8", fileContent, httpResponse);
    }

    private void respondWithContent(final String contentType,
                                    final String content,
                                    final HttpServletResponse httpResponse) throws IOException {
        httpResponse.setContentType(contentType);
        httpResponse.getWriter().write(content);
    }
}
