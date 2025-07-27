package lanit_exp.proxy_hub.services;

import jakarta.servlet.http.HttpServletRequest;
import lanit_exp.proxy_hub.responses.ValueResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainProxyService {

    private final WSNodes nodes;
    private final NodeCommunicationService nodeCommunicationService;

    public ResponseEntity<?> idRequestHandler(String id, HttpServletRequest request) {

        String sessionId = nodes.getNodeSessionByNodeId(id);

        if (sessionId == null)
            return new ValueResponseEntity("Нода с id: '%s' не найдена. Проверьте подключение ноды или перезапустите её.".formatted(id))
                    .getEntity(HttpStatus.NOT_FOUND);

        return nodeCommunicationService.sendMessage(sessionId, request);
    }

    public ResponseEntity<?> tagRequestHandler(String tag, HttpServletRequest request) {
        return new ValueResponseEntity("Фильтрация по тегам не реализована")
                .getEntity(HttpStatus.NOT_IMPLEMENTED);
    }

}
