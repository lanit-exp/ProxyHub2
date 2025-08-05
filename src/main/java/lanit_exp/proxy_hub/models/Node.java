package lanit_exp.proxy_hub.models;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Node {

    private final String id;

    private final List<String> tags;

    private LocalDateTime lastActivity;

    public Node(String id, List<String> tags) {
        this.id = id;
        this.tags = tags;
        updateLastActivity();
    }

    public void updateLastActivity() {
        lastActivity = LocalDateTime.now();
    }


}
