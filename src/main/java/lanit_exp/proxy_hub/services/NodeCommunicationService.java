package lanit_exp.proxy_hub.services;

import jakarta.servlet.http.HttpServletRequest;
import lanit_exp.proxy_hub.helpers.ApiConverter;
import lanit_exp.proxy_hub.models.ApiRequest;
import lanit_exp.proxy_hub.responses.ValueResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NodeCommunicationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NodeMessageHolder nodeMessageHolder;

    public ResponseEntity<?> sendMessage(String nodeSession, ApiRequest apiRequest, String driverName) {

        String requestId = UUID.randomUUID().toString();
        Map<String, Object> headers = new HashMap<>();
        headers.put("request_id", requestId);
        headers.put("driver_name", driverName);

        messagingTemplate.convertAndSend("/queue/to/" + nodeSession, apiRequest, headers);

        log.info(">>>>> REQUEST ID '{}': {} - {}", requestId, apiRequest.getMethod(), apiRequest.getUri());

        try {

            String responseString = nodeMessageHolder.awaitMessage(requestId);
            return ApiConverter.responseToEntity(responseString);

        } catch (TimeoutException e) {
            return new ValueResponseEntity("[ NODE RESPONSE TIMEOUT ] Не получен ответ от драйвера (Proxy Node)")
                    .getEntity(HttpStatus.REQUEST_TIMEOUT);
        } catch (Exception e) {
            return new ValueResponseEntity("[ NODE RESPONSE EXCEPTION ] Ошибка получения ответа от драйвера (Proxy Node): " + e.getMessage())
                    .getEntity(HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> sendMessage(String nodeSession, HttpServletRequest request) {
       return sendMessage(nodeSession, ApiConverter.requestToDTO(request), null);
    }

}
