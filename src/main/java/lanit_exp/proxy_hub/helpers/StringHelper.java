package lanit_exp.proxy_hub.helpers;

public class StringHelper {

    public static String trimLargeString(String sourseString, int maxLimit) {
        if(sourseString == null) return null;

        return sourseString.length() > maxLimit
                ? sourseString.substring(0, maxLimit) + " ... [ Content-Length: %s ] ".formatted(sourseString.length())
                : sourseString;
    }


}
