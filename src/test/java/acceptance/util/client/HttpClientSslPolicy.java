package acceptance.util.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpClientSslPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientSslPolicy.class);

    public void configureSsl() {

        final TrustManager[] trustManagers = new TrustManager[]{createX509TrustManager()};

        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        } catch (Exception ex) {
            final String errorMsg = "Unable to configure SSL policy of HTTP client";
            LOGGER.error(errorMsg);
            throw new RuntimeException(errorMsg, ex);
        }

    }

    // Create a trust manager that does not validate certificate chains
    private X509TrustManager createX509TrustManager() {
        return new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
    }

}
