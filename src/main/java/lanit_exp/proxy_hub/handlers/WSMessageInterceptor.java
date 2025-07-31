package lanit_exp.proxy_hub.handlers;

import lanit_exp.proxy_hub.exceptions.IncorrectDestinationException;
import lanit_exp.proxy_hub.exceptions.IncorrectNodeIdException;
import lanit_exp.proxy_hub.exceptions.IncorrectNodeSessionException;
import lanit_exp.proxy_hub.services.WSNodes;
import lanit_exp.proxy_hub.services.WSSessions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class WSMessageInterceptor implements ChannelInterceptor {

    private final WSNodes wsNodes;
    private final WSSessions wsSessions;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            try {
                registerWSNode(accessor);
            } catch (Exception e) {
                log.error("Ошибка регистрации ноды: {}", e.getMessage());
                closeCurrentWSSession(accessor, e);
            }

        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            try {
                checkSubscribe(accessor);
            } catch (Exception e) {
                closeCurrentWSSession(accessor, e);
            }
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            deleteWSNode(accessor);
        } else if (StompCommand.SEND.equals(accessor.getCommand())) {
            updateWSNodeActivity(accessor);
        }

        return message;
    }


    private void registerWSNode(StompHeaderAccessor accessor) {
        wsNodes.registerNode(accessor.getSessionId(), getNodeId(accessor), getNodeTags(accessor), getNodeSession(accessor));
        log.info("Нода подключена: {}. Активных соединений: {}", accessor.getSessionId(), wsNodes.numberOfConnectedNodes());
    }

    private void checkSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        String expectedDestination = "/queue/to/" + wsNodes.getNodeSessionBySessionId(accessor.getSessionId());
        if (!Objects.equals(destination, expectedDestination)) {
            String error = "Некорректный параметр destination(имя топика): '%s' для подписки.".formatted(destination);
            log.error(error);
            throw new IncorrectDestinationException(error);
        }

    }

    private void deleteWSNode(StompHeaderAccessor accessor) {
        wsNodes.deleteNode(accessor.getSessionId());
        log.info("Нода отключена: {}. Активных соединений: {}", accessor.getSessionId(), wsNodes.numberOfConnectedNodes());
    }

    private void updateWSNodeActivity(StompHeaderAccessor accessor) {
        log.info("Обновлено время активности ноды: {}", accessor.getSessionId());
        wsNodes.updateNode(accessor.getSessionId());
    }


    private void closeCurrentWSSession(StompHeaderAccessor accessor, Exception e){
        try {
            wsSessions.getSession(accessor.getSessionId())
                    .close(CloseStatus.SERVER_ERROR.withReason("Session closed by server: " + e.getMessage()));
        } catch (IOException ex) {
            log.error("Ошибка закрытия сессии: {}", ex.getMessage());
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private String getNodeId(StompHeaderAccessor accessor) {
        try {
            List<?> nodeIds = (List) ((MultiValueMap) accessor.getHeader("nativeHeaders"))
                    .get("node_id");

            if (nodeIds == null || nodeIds.isEmpty())
                throw new IncorrectNodeIdException();

            return (String) nodeIds.get(0);

        } catch (Exception e) {
            throw new IncorrectNodeIdException();
        }
    }

    private List<String> getNodeTags(StompHeaderAccessor accessor) {

        List<?> tagsList = (List) ((MultiValueMap) accessor.getHeader("nativeHeaders"))
                .get("node_tags");

        if (tagsList == null || tagsList.isEmpty()) return new ArrayList<>();

        return Arrays.stream(((String) tagsList.get(0)).split(","))
                .map(String::trim)
                .filter(string -> !string.isEmpty())
                .collect(Collectors.toList());
    }


    private String getNodeSession(StompHeaderAccessor accessor) {
        try {
            List<?> nodeSessions = (List) ((MultiValueMap) accessor.getHeader("nativeHeaders"))
                    .get("node_session");

            if (nodeSessions == null || nodeSessions.isEmpty())
                throw new IncorrectNodeSessionException();

            return (String) nodeSessions.get(0);

        } catch (Exception e) {
            throw new IncorrectNodeSessionException();
        }
    }

}
