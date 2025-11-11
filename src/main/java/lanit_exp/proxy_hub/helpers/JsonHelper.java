package lanit_exp.proxy_hub.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String getDriverSession(String json, String... path) {

        try {

            JsonNode jsonNode = MAPPER.readTree(json);

            for (String p : path) {
                jsonNode = jsonNode.path(p);

                if (jsonNode.isMissingNode())
                    return null;
            }

            return jsonNode.asText();

        } catch (Exception ignore) {
            return null;
        }
    }

}
