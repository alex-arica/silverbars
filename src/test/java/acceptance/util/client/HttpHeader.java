package acceptance.util.client;

import silverbars.util.http.response.HttpHeaderType;

import java.util.Objects;

public class HttpHeader {

    private final HttpHeaderType headerType;
    private final String headerValue;

    public HttpHeader(final HttpHeaderType headerType,
                      final String headerValue) {
        this.headerType = headerType;
        this.headerValue = headerValue;
    }

    public String getType() {
        return headerType.getHeaderName();
    }

    public String getValue() {
        return headerValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpHeader that = (HttpHeader) o;
        return headerType == that.headerType &&
                Objects.equals(headerValue, that.headerValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerType, headerValue);
    }
}
