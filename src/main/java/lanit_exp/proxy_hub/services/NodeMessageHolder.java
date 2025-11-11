package lanit_exp.proxy_hub.services;

import lanit_exp.proxy_hub.configurations.ProxyConfig;
import lanit_exp.proxy_hub.models.NodeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class NodeMessageHolder {

    private static final ConcurrentHashMap<String, NodeMessage> MESSAGES = new ConcurrentHashMap<>();

    public synchronized void addMessage(String requestId, String message) {
        MESSAGES.put(requestId, new NodeMessage(message));
        notifyAll();
    }

    public synchronized String awaitMessage(String requestId) throws TimeoutException, InterruptedException {

        long timeout = ProxyConfig.getProxyConfig().getMessageAwaitTimeout() * 1000L;
        long end = System.currentTimeMillis() + timeout;
        long remaining = timeout;

        while (remaining > 0) {
            NodeMessage message = MESSAGES.remove(requestId);
            if (message != null) {
                return message.getMessage();
            }

            wait(remaining);
            remaining = end - System.currentTimeMillis();
        }

        throw new TimeoutException();
    }


    @Scheduled(fixedDelay = 300_000)
    private void clearOldMessages() {
        log.info("Очистка очереди сообщений");

        try {

            Integer timeout = ProxyConfig.getProxyConfig().getMessageAwaitTimeout();
            MESSAGES.entrySet().removeIf(entry -> entry.getValue().isOutdated(timeout));

        } catch (Exception e) {
            log.error("Ошибка очистки старых сообщений", e);
        }
    }

}
