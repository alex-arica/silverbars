package acceptance.util.client;

public class HttpClientException extends RuntimeException {

    private final int httpResponseCode;

    public HttpClientException(final int httpResponseCode,
                               final String errorMessage) {
        super(errorMessage);
        this.httpResponseCode = httpResponseCode;
    }

    public HttpClientException(final int httpResponseCode,
                               final String errorMessage,
                               final Exception ex) {
        super(errorMessage, ex);
        this.httpResponseCode = httpResponseCode;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }
}
