package lanit_exp.proxy_hub.services;

import lanit_exp.proxy_hub.models.NodeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
@Slf4j
public class NodeMessageHolder {

    private final ConcurrentHashMap<String, NodeMessage> MESSAGES = new ConcurrentHashMap<>();

    @Value("${node.message.live_timeout}")
    private Integer messageLiveTimeout;

    public void addMessage(String requestId, String message) {
        MESSAGES.put(requestId, new NodeMessage(message));
    }

    public NodeMessage getMessage(String requestId) {
        return MESSAGES.contains(requestId) ? MESSAGES.remove(requestId) : null;
    }


    @Scheduled(fixedDelay = 300_000)
    private void clearOldMessages(){
        log.info("Очистка очереди сообщений");

        try {
            Stream<String> toDelete = MESSAGES.entrySet().stream()
                    .filter(stringNodeMessageEntry -> stringNodeMessageEntry.getValue().isOutdated(messageLiveTimeout))
                    .map(Map.Entry::getKey);

            toDelete.forEach(MESSAGES::remove);
        } catch (Exception e) {
            log.error("Ошибка очистки старых сообщений", e);
        }
    }

}
