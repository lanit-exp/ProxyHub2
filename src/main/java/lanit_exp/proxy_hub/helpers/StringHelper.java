package lanit_exp.proxy_hub.helpers;

public class StringHelper {

    public static String trimLargeString(String sourseString, int maxLimit) {
        if(sourseString == null) return null;

        return sourseString.length() > maxLimit
                ? sourseString.substring(0, maxLimit) + "...[ size: %s ]...".formatted(sourseString.length())
                : sourseString;
    }


}
