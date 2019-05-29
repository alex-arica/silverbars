package acceptance.util.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    public HttpClient() {
        //new HttpClientSslPolicy().configureSsl();
    }

    public String getRequest(final String url) throws HttpClientException {
        return sendRequest(HttpMethodType.GET,
                           url,
                           Optional.empty(),
                           new ArrayList<>(),
                           true,
                           0);
    }

    public String deleteRequest(final String url) throws HttpClientException {
        return sendRequest(HttpMethodType.DELETE,
                url,
                Optional.empty(),
                new ArrayList<>(),
                true,
                0);
    }

    public String postRequest(final String url,
                              final String body) throws HttpClientException {
        return sendRequest(HttpMethodType.POST,
                url,
                Optional.of(body),
                new ArrayList<>(),
                true,
                0);
    }


    private String sendRequest(final HttpMethodType methodType,
                               final String urlAsString,
                               final Optional<String> bodyOptional,
                               final List<HttpHeader> httpHeaders,
                               final boolean isLoggingEnabled,
                               final int numberOfRetryCallsMade) throws HttpClientException {

        if (isLoggingEnabled) LOGGER.info("Sending request to URL : {}", urlAsString);

        int responseCode = 0;

        try {
            final HttpURLConnection httpURLConnection = createHttpUrlConnection(urlAsString);
            setRequestMethod(methodType, httpURLConnection);
            addHeaders(httpHeaders, httpURLConnection);
            sendBody(bodyOptional, httpURLConnection);

            responseCode = getResponseCode(httpURLConnection);
            final InputStream inputStream = getHttpResponse(httpURLConnection, responseCode);
            if (responseCode == 200) return generateResponse(inputStream);

        } catch (Exception ex) {

            if (shouldWeRetryHttpCall(methodType, numberOfRetryCallsMade)) {
                return retryHttpCall(methodType, urlAsString, bodyOptional, httpHeaders, isLoggingEnabled, numberOfRetryCallsMade);
            }

            final String errorMsg = "An exception happened after a " + methodType + " request "
                                    + "to url: " + urlAsString
                                    + " - Given error: " + ex.getClass() + "  : " + ex.getMessage();
            throw new HttpClientException(responseCode, errorMsg, ex);
        }

        if (shouldWeRetryHttpCall(methodType, numberOfRetryCallsMade)) {
            return retryHttpCall(methodType, urlAsString, bodyOptional, httpHeaders, isLoggingEnabled, numberOfRetryCallsMade);
        }

        final String errorMsg = "The HTTP response is " + responseCode + " for a "
                                + methodType + " request to url: " + urlAsString
                                + ". Given response: " + responseCode;
        throw new HttpClientException(responseCode, errorMsg);
    }

    private String retryHttpCall(final HttpMethodType methodType,
                                 final String urlAsString,
                                 final Optional<String> bodyOptional,
                                 final List<HttpHeader> httpHeaders,
                                 final boolean isLoggingEnabled,
                                 final int numberOfRetryCallsMade) {
        sleep200ms();
        LOGGER.info("Retrying ({} / 5) HTTP GET call: {}", numberOfRetryCallsMade, urlAsString);
        return sendRequest(methodType,
                           urlAsString,
                           bodyOptional,
                           httpHeaders,
                           isLoggingEnabled,
                           numberOfRetryCallsMade);
    }

    private void sleep200ms() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
    }

    private boolean shouldWeRetryHttpCall(final HttpMethodType httpMethodType,
                                          final int numberOfRetryCallsMade) {
        return httpMethodType == HttpMethodType.GET && numberOfRetryCallsMade < 5;
    }

    private void addHeaders(final List<HttpHeader> httpHeaders,
                            final HttpURLConnection httpURLConnection) {
        httpHeaders
           .stream()
           .forEach(httpHeader -> httpURLConnection.setRequestProperty(httpHeader.getType(), httpHeader.getValue()));
    }

    private void setRequestMethod(final HttpMethodType httpMethodType,
                                  final HttpURLConnection httpURLConnection) throws ProtocolException {
        httpURLConnection.setRequestMethod(httpMethodType.name());
    }

    private void sendBody(final Optional<String> bodyOptional,
                          final HttpURLConnection httpURLConnection) throws IOException {
        if (!bodyOptional.isPresent()) return;

        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);

        final OutputStream postOutputStream = httpURLConnection.getOutputStream();
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(postOutputStream, "UTF-8"));
        writer.write(bodyOptional.get());
        writer.flush();
        writer.close();
        postOutputStream.close();

        httpURLConnection.connect();
    }

    private InputStream getHttpResponse(final HttpURLConnection httpURLConnection,
                                        final int responseCode) throws IOException {
        if (responseCode == 200) {
            return httpURLConnection.getInputStream();
        }
        return httpURLConnection.getErrorStream();
    }

    private String generateResponse(final InputStream inputStream) throws IOException {
        if (inputStream == null) return "";

        final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private HttpURLConnection createHttpUrlConnection(final String urlAsString) throws IOException {
        final URL url = new URL(urlAsString);
        return (HttpURLConnection) url.openConnection();
    }

    private int getResponseCode(final HttpURLConnection httpURLConnection) throws IOException {
        return httpURLConnection.getResponseCode();
    }

}
