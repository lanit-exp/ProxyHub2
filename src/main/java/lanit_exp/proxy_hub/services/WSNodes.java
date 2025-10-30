package lanit_exp.proxy_hub.services;

import lanit_exp.proxy_hub.models.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Component
public class WSNodes {

    private final Map<String, Node> NODES = new ConcurrentHashMap<>();

    @Value("${node.session.idle_timeout}")
    private Integer idleTimeout;

    public void registerNode(String sessionId, String nodeId, Set<String> tags) {
        NODES.put(sessionId, new Node(nodeId, tags));
    }

    public Node getNode(String sessionId){
        return NODES.get(sessionId);
    }

    public void deleteNode(String sessionId) {
        NODES.remove(sessionId);
    }

    public void updateLastActivity(String sessionId) {
        NODES.get(sessionId).updateLastActivity();
    }

    public String getNodeSessionByNodeId(String nodeId) {
        return getNodeSession(stringNodeEntry ->
                stringNodeEntry.getValue().getId().equals(nodeId));
    }

    public String getFreeNodeByTags(Set<String> tags) {
        if (tags == null || tags.isEmpty())
            throw new IllegalArgumentException("Отсутствуют теги для фильтрации нод.");

        return getNodeSession(stringNodeEntry ->
                stringNodeEntry.getValue().isFreeNode(idleTimeout) && stringNodeEntry.getValue().getTags().containsAll(tags));

    }

    public String getNodeByDriverSessionId(String driverSessionId) {
        return getNodeSession(stringNodeEntry ->
                Objects.equals(stringNodeEntry.getValue().getDriverSessionId(), driverSessionId));
    }


    public Integer numberOfConnectedNodes() {
        return NODES.size();
    }

    //------------------------------------------------------------------------------------------------------------------

    private String getNodeSession(Predicate<? super Map.Entry<String, Node>> filter) {
        return NODES.entrySet().stream()
                .filter(filter)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }


}
