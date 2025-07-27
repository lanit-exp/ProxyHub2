package lanit_exp.proxy_hub.services;

import jakarta.servlet.http.HttpServletRequest;
import lanit_exp.proxy_hub.responses.ValueResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NodeCommunicationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NodeMessageHolder nodeMessageHolder;

    public ResponseEntity<?> sendMessage(String sessionId, HttpServletRequest request) {

        String requestId = UUID.randomUUID().toString();
        Map<String, Object> headers = new HashMap<>();
        headers.put("request_id", requestId);

        messagingTemplate.convertAndSend("/queue/to/" + sessionId, request.getRequestURI(), headers);

        try {
            String response = nodeMessageHolder.awaitMessage(requestId);
            return new ValueResponseEntity(response).getEntity(HttpStatus.OK);

        } catch (Exception e) {
            return new ValueResponseEntity("[ NODE RESPONSE TIMEOUT ] Не получен ответ от драйвера (Proxy Node)")
                    .getEntity(HttpStatus.REQUEST_TIMEOUT);
        }

    }


}
