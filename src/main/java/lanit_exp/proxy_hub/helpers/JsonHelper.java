package lanit_exp.proxy_hub.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;

public class JsonHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String getValueByJsonPath(String json, String path1, String path2) {

        try {
            JsonNode rootNode = MAPPER.readTree(json);

            JsonNode node1 = findNodeByName(rootNode, path1);
            if (node1 == null) return null;

            JsonNode node2 = node1.path(path2);

            return node2.isMissingNode() ? null : node2.asText();

        } catch (Exception ignore) {
            return null;
        }
    }


    private static JsonNode findNodeByName(JsonNode node, String targetName) {
        if (node == null || targetName == null) {
            return null;
        }

        if (node.isObject()) {
            if (node.has(targetName)) {
                return node.get(targetName);
            }

            Iterator<String> fieldNames = node.fieldNames();

            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode result = findNodeByName(node.get(fieldName), targetName);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                JsonNode result = findNodeByName(arrayElement, targetName);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }
}
