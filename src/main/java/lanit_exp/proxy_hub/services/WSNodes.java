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
    public void registerNode(String sessionId, Node node) {
        NODES.put(sessionId, node);
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
                .filter(entry -> entry.getValue().getNodeId().equals(nodeId))
                .map(Map.Entry::getKey)
                .toList();
    }

    public String getFreeNodeByTagsAndMarkBusy(Set<String> tags, String runId, String driverName, Integer awaitTimeout) {
        if (tags == null || tags.isEmpty())
            throw new IllegalArgumentException("Отсутствуют теги для фильтрации нод.");

        long end = System.currentTimeMillis() + awaitTimeout * 1000L;

        do {
            synchronized (NODES) {
                String nodeSession = getSessionIdByNodeParams(tags, runId, driverName);

                if (nodeSession != null) {
                    NODES.get(nodeSession).setReceivingASession(true);
                    return nodeSession;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (System.currentTimeMillis() < end);

        return null;
    }

    public String getNodeByDriverSessionId(String driverSessionId) {
        return getNodeSession(entry ->
                entry.getValue().containsDriverSessionId(driverSessionId));
    }


    //------------------------------------------------------------------------------------------------------------------

    public Integer numberOfConnectedNodes() {
        return NODES.size();
    }

    public List<Map<String, Object>> getNodeInfo() {
        return NODES.values().stream().map(Node::getInfo).toList();
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

    private String getSessionIdByNodeParams(Set<String> tags, String runId, String driverName) {

        if (runId != null && !runId.isEmpty() && driverName != null && !driverName.isEmpty()) {
            String nodeSession = getNodeSession(entry ->
                    (Objects.equals(entry.getValue().getRunId(), runId) || entry.getValue().getDriverSessionIds().contains(runId))
                            && !entry.getValue().isFreeNode());
            if (nodeSession != null) return nodeSession;
        }

        return getNodeSession(entry -> entry.getValue().isFreeNode() && entry.getValue().getTags().containsAll(tags));
    }

}
