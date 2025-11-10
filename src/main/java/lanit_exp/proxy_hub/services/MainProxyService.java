package lanit_exp.proxy_hub.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lanit_exp.proxy_hub.models.Node;
import lanit_exp.proxy_hub.responses.ValueResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainProxyService {

    private final WSNodes nodes;
    private final NodeCommunicationService nodeCommunicationService;

    public ResponseEntity<?> idRequestHandler(String id, HttpServletRequest request) {

        String nodeSession = nodes.getNodeSessionByNodeId(id);

        if (nodeSession == null)
            return new ValueResponseEntity("Нода с id: '%s' не найдена. Проверьте подключение ноды или перезапустите её.".formatted(id))
                    .getEntity(HttpStatus.NOT_FOUND);

        return nodeCommunicationService.sendMessage(nodeSession, request);
    }

    //------------------------------------------------------------------------------------------------------------------

    public ResponseEntity<?> newSessionRequestHandler(String tag, HttpServletRequest request) {

        Set<String> tags = Arrays.stream(tag.split("&")).collect(Collectors.toSet());

        String nodeSession;
        try {
            nodeSession = nodes.getFreeNodeByTagsAndMarkBusy(tags);
        } catch (Exception e) {
            return new ValueResponseEntity("Не удалось создать сессию: ошибка при поиске свободной ноды - '%s'".formatted(e.getMessage()))
                    .getEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (nodeSession == null)
            return new ValueResponseEntity("Не удалось создать сессию: не найдена свободная нода с тегами - '%s'".formatted(tag))
                    .getEntity(HttpStatus.NOT_FOUND);

        ResponseEntity<?> responseEntity = nodeCommunicationService.sendMessage(nodeSession, request);


        String driverSession = getDriverSession(responseEntity);

        if (driverSession != null) {
            nodes.getNode(nodeSession).setDriverSessionId(driverSession);
            log.info("Driver SESSION '{}' - CREATE", driverSession);
        }

        nodes.getNode(nodeSession).setReceivingASession(false);

        return responseEntity;
    }

    public ResponseEntity<?> quitSessionRequestHandler(String sessionId, HttpServletRequest request) {

        String nodeSession = nodes.getNodeByDriverSessionId(sessionId);

        if (nodeSession == null)
            return new ValueResponseEntity("Не найдена открытая сессия драйвера - '%s'".formatted(sessionId))
                    .getEntity(HttpStatus.NOT_FOUND);

        ResponseEntity<?> responseEntity = nodeCommunicationService.sendMessage(nodeSession, request);

        Node node = nodes.getNode(nodeSession);
        node.setDriverSessionId(null);

        log.info("Driver SESSION '{}' - CLOSE", sessionId);

        return responseEntity;
    }


    public ResponseEntity<?> sessionRequestHandler(String sessionId, HttpServletRequest request) {

        String nodeSession = nodes.getNodeByDriverSessionId(sessionId);

        if (nodeSession == null)
            return new ValueResponseEntity("Не найдена открытая сессия драйвера - '%s'".formatted(sessionId))
                    .getEntity(HttpStatus.NOT_FOUND);

        return nodeCommunicationService.sendMessage(nodeSession, request);
    }

    //------------------------------------------------------------------------------------------------------------------
    public String getDriverSession(ResponseEntity<?> responseEntity) {

        try {
            return new ObjectMapper().readTree((String) responseEntity.getBody())
                    .path("value")
                    .path("sessionId").asText();

        } catch (Exception ignore) {
            return null;
        }
    }

}
