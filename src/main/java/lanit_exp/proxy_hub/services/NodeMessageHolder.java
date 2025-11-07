package lanit_exp.proxy_hub.services;

import lanit_exp.proxy_hub.configurations.ProxyConfig;
import lanit_exp.proxy_hub.models.NodeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

@Service
@Slf4j
public class NodeMessageHolder {

    private static final ConcurrentHashMap<String, NodeMessage> MESSAGES = new ConcurrentHashMap<>();

    public void addMessage(String requestId, String message) {
        MESSAGES.put(requestId, new NodeMessage(message));
    }

    public String awaitMessage(String requestId) throws TimeoutException, InterruptedException {
        long end = new Date().getTime() + ProxyConfig.getProxyConfig().getMessageAwaitTimeout() * 1000;

        while (new Date().getTime() < end) {
            if (MESSAGES.containsKey(requestId))
                return MESSAGES.remove(requestId).getMessage();

            Thread.sleep(100);
        }

        throw new TimeoutException();
    }


    @Scheduled(fixedDelay = 300_000)
    private void clearOldMessages() {
        log.info("Очистка очереди сообщений");

        try {
            Stream<String> toDelete = MESSAGES.entrySet().stream()
                    .filter(stringNodeMessageEntry -> stringNodeMessageEntry.getValue().isOutdated(ProxyConfig.getProxyConfig().getMessageAwaitTimeout()))
                    .map(Map.Entry::getKey);

            toDelete.forEach(MESSAGES::remove);
        } catch (Exception e) {
            log.error("Ошибка очистки старых сообщений", e);
        }
    }

}
