package lanit_exp.proxy_hub.services;

import lanit_exp.proxy_hub.models.Node;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Component
public class WSNodes {

    private final Map<String, Node> NODES = new ConcurrentHashMap<>();


    //------------------------------------------------------------------------------------------------------------------
    public void registerNode(String sessionId, String nodeId, Set<String> tags) {
        NODES.put(sessionId, new Node(nodeId, tags));
    }

    public Node getNode(String sessionId) {
        return NODES.get(sessionId);
    }

    public void deleteNode(String sessionId) {
        NODES.remove(sessionId);
    }

    public void updateLastActivity(String sessionId) {
        NODES.get(sessionId).updateLastActivity();
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<String> getNodeSessionsByNodeId(String nodeId) {
        return NODES.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(nodeId))
                .map(Map.Entry::getKey)
                .toList();
    }

    public String getFreeNodeByTagsAndMarkBusy(Set<String> tags) {
        if (tags == null || tags.isEmpty())
            throw new IllegalArgumentException("Отсутствуют теги для фильтрации нод.");

        synchronized (NODES) {
            String nodeSession = getNodeSession(entry -> entry.getValue().isFreeNode()
                    && entry.getValue().getTags().containsAll(tags));

            if (nodeSession != null)
                NODES.get(nodeSession).setReceivingASession(true);

            return nodeSession;
        }
    }

    public String getNodeByDriverSessionId(String driverSessionId) {
        return getNodeSession(stringNodeEntry ->
                Objects.equals(stringNodeEntry.getValue().getDriverSessionId(), driverSessionId));
    }


    //------------------------------------------------------------------------------------------------------------------

    public Integer numberOfConnectedNodes() {
        return NODES.size();
    }

    public List<Map<String, Object>> getNodeStatuses() {
        return NODES.values().stream().map(Node::getStatus).toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    private String getNodeSession(Predicate<? super Map.Entry<String, Node>> filter) {
        return NODES.entrySet().stream()
                .filter(filter)
                .sorted(Comparator.comparing(o -> o.getValue().getLastActivity()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }


}
