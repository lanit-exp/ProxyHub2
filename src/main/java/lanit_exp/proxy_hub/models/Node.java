package lanit_exp.proxy_hub.models;

import lanit_exp.proxy_hub.configurations.ProxyConfig;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
public class Node {

    private final String id;
    private final Set<String> tags;


    @Setter
    private String driverSessionId;
    private LocalDateTime lastActivity;
    private boolean receivingASession;


    public Node(String id, Set<String> tags) {
        this.id = id;
        this.tags = tags;
        updateLastActivity();
    }

    public void updateLastActivity() {
        lastActivity = LocalDateTime.now();
    }

    public synchronized boolean isFreeNode() {
        return !receivingASession &&
                (driverSessionId == null || ChronoUnit.SECONDS.between(lastActivity, LocalDateTime.now()) > ProxyConfig.getProxyConfig().getIdleTimeout());
    }

    public synchronized void setReceivingASession(boolean busy) {
        this.receivingASession = busy;
    }

    public Map<String, Object> getStatus() {

        Map<String, Object> result = new HashMap<>();
        result.put("id", "*".repeat((id.length() + 1) / 2) + id.substring((id.length() + 1) / 2));
        result.put("tags", tags);
        result.put("driverSessionId", driverSessionId);
        result.put("isFree", isFreeNode());
        result.put("lastActivity", lastActivity.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return result;
    }

}
