package lanit_exp.proxy_hub.models;

import lanit_exp.proxy_hub.configurations.ProxyConfig;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Getter
public class Node {

    private final String nodeId;

    private final String nodeName;
    private final String nodeVersion;
    private final String nodeDescription;

    private final Set<String> tags;
    private final Set<String> driverNames;


    private final Set<String> driverSessionIds = new HashSet<>();

    private String runId;
    private boolean receivingASession;

    private LocalDateTime lastActivity;


    public Node(String nodeId,
                String nodeName,
                String nodeVersion,
                String nodeDescription,
                Set<String> tags,
                Set<String> driverNames) {

        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeVersion = nodeVersion;
        this.nodeDescription = nodeDescription;
        this.tags = tags;
        this.driverNames = driverNames;

        updateLastActivity();
    }

    public void updateLastActivity() {
        lastActivity = LocalDateTime.now();
    }

    public synchronized boolean isFreeNode() {
        clearOldDriverSession();
        return !receivingASession && driverSessionIds.isEmpty();
    }

    private void clearOldDriverSession(){
        if(!driverSessionIds.isEmpty() && ChronoUnit.SECONDS.between(lastActivity, LocalDateTime.now()) > ProxyConfig.getProxyConfig().getIdleTimeout()){
            driverSessionIds.clear();
        }
    }

    public synchronized void setReceivingASession(boolean busy) {
        this.receivingASession = busy;
    }

    //------------------------------------------------------------------------------------------------------------------

    public Node setRunId(String runId) {
        this.runId = runId;
        return this;
    }

    public Node addDriverSessionId(String driverSessionId) {
        driverSessionIds.add(driverSessionId);
        return this;
    }

    public boolean containsDriverSessionId(String driverSessionId) {
        return driverSessionIds.contains(driverSessionId);
    }

    public boolean deleteDriverSessionId(String driverSessionId) {
        return driverSessionIds.remove(driverSessionId);
    }

    //------------------------------------------------------------------------------------------------------------------

    public Map<String, Object> getInfo() {

        Map<String, Object> result = new HashMap<>();
        result.put("nodeId", "*".repeat((nodeId.length() + 1) / 2) + nodeId.substring((nodeId.length() + 1) / 2));
        result.put("nodeName", nodeName);
        result.put("nodeVersion", nodeVersion);
        result.put("nodeDescription", nodeDescription);
        result.put("tags", tags);
        result.put("driverNames", driverNames);
        result.put("runId", runId);
        result.put("driverSessionIds", driverSessionIds);
        result.put("isFree", isFreeNode());
        result.put("lastActivity", lastActivity.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return result;
    }

}
