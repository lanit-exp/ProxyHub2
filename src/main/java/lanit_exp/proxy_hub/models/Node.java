package lanit_exp.proxy_hub.models;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Node {

    private final String id;

    private final String nodeSession;

    private final List<String> tags;

    private LocalDateTime lastActivity;

    public Node(String id, List<String> tags, String nodeSession) {
        this.id = id;
        this.tags = tags;
        this.nodeSession = nodeSession;
        updateLastActivity();
    }

    public void updateLastActivity(){
        lastActivity = LocalDateTime.now();
    }


}
