package silverbars.configuration.context;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HttpServerConfig {

    public int getPort() {
        return 26034;
    }

    public String getThisServiceUrl() {
        return "http://" + getHost() + ":" + getPort();
    }

    public static String getHost() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException ex) {
            return "";
        }
    }

}
