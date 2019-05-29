package silverbars.util.http.response;

public enum HttpHeaderType {

    CONTENT_DISPOSITION("Content-Disposition"),
    BASIC_AUTH("Authorization"),
    ACCEPT("Accept");

    private final String headerName;

    HttpHeaderType(final String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}
