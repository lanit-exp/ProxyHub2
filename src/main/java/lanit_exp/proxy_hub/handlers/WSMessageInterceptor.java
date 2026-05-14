package lanit_exp.proxy_hub.handlers;

import lanit_exp.proxy_hub.exceptions.IncorrectDestinationException;
import lanit_exp.proxy_hub.exceptions.IncorrectNodeIdException;
import lanit_exp.proxy_hub.models.Node;
import lanit_exp.proxy_hub.services.WSNodes;
import lanit_exp.proxy_hub.services.WSSessions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.*;
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
        } else if(SimpMessageType.CONNECT_ACK.equals(accessor.getHeader("simpMessageType"))) {
            StompHeaderAccessor newAccessor = StompHeaderAccessor.create(StompCommand.CONNECTED);
            newAccessor.copyHeaders(accessor.toMap());
            newAccessor.addNativeHeader("node_session", accessor.getSessionId());
            return MessageBuilder.createMessage(message.getPayload(), newAccessor.getMessageHeaders());
        }

        return message;
    }


    private void registerWSNode(StompHeaderAccessor accessor) {

        String sessionId = accessor.getSessionId();
        String nodeId = getHeaderValue(accessor, "node_id");
        String nodeName = getHeaderValue(accessor, "node_name");;
        String nodeVersion = getHeaderValue(accessor, "node_version");;
        String nodeDescription = getHeaderValue(accessor, "node_description");;
        Set<String> tags = getHeaderValues(accessor, "node_tags");
        Set<String> driverNames = getHeaderValues(accessor, "driver_names");

        Node node = new Node(nodeId, nodeName, nodeVersion, nodeDescription, tags, driverNames);

        wsNodes.registerNode(sessionId, node);

        log.info("Нода подключена: {}. Активных соединений: {}", accessor.getSessionId(), wsNodes.numberOfConnectedNodes());
    }

    private void checkSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        String expectedDestination = "/queue/to/" + accessor.getSessionId();
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
        wsNodes.updateLastActivity(accessor.getSessionId());
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

    private String getHeaderValue(StompHeaderAccessor accessor, String headerName) {
        try {
            List<?> values = (List) ((MultiValueMap) accessor.getHeader("nativeHeaders"))
                    .get(headerName);

            return (String) values.get(0);

        } catch (Exception e) {
            throw new IncorrectNodeIdException();
        }
    }

    private Set<String> getHeaderValues(StompHeaderAccessor accessor, String headerName) {

        List<?> tagsList = (List) ((MultiValueMap) accessor.getHeader("nativeHeaders"))
                .get(headerName);

        if (tagsList == null || tagsList.isEmpty()) return new HashSet<>();

        return Arrays.stream(((String) tagsList.get(0)).split(","))
                .map(String::trim)
                .filter(string -> !string.isEmpty())
                .collect(Collectors.toSet());
    }


}
