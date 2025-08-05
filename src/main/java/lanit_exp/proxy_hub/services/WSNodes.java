package lanit_exp.proxy_hub.services;

import lanit_exp.proxy_hub.models.Node;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WSNodes {

    private final Map<String, Node> NODES = new ConcurrentHashMap<>();

    public void registerNode(String sessionId, String nodeId, List<String> tags) {
        NODES.put(sessionId, new Node(nodeId, tags));
    }

    public void deleteNode(String sessionId) {
        NODES.remove(sessionId);
    }

    public void updateNode(String sessionId) {
        NODES.get(sessionId).updateLastActivity();
    }

    public String getNodeSessionByNodeId(String nodeId) {
        return NODES.entrySet().stream()
                .filter(stringNodeEntry -> stringNodeEntry.getValue().getId().equals(nodeId))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    public Integer numberOfConnectedNodes() {
        return NODES.size();
    }


}
