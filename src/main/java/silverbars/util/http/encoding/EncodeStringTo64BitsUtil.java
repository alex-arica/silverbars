package silverbars.util.http.encoding;

import java.util.Base64;

public class EncodeStringTo64BitsUtil {

    public String encode(final String toEncode) {
        byte[] credentualsIn64Bits = Base64.getEncoder().encode(toEncode.getBytes());
        return new String(credentualsIn64Bits);
    }
}
