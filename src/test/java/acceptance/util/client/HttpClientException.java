package acceptance.util.client;

public class HttpClientException extends RuntimeException {

    private final int httpResponseCode;
    private final String response;

    public HttpClientException(final int httpResponseCode,
                               final String errorMessage,
                               final String response) {
        super(errorMessage);
        this.httpResponseCode = httpResponseCode;
        this.response = response;
    }

    public HttpClientException(final int httpResponseCode,
                               final String errorMessage,
                               final String response,
                               final Exception ex) {
        super(errorMessage, ex);
        this.httpResponseCode = httpResponseCode;
        this.response = response;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public String getResponse() {
        return response;
    }
}
