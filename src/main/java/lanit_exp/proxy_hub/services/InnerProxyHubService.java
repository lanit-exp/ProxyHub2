package lanit_exp.proxy_hub.services;

import lanit_exp.proxy_hub.configurations.ProxyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InnerProxyHubService {

    private final WSNodes nodes;

    public ResponseEntity<?> getProxyHubStatus() {

        Map<String, Object> result = new HashMap<>();
        result.put("activeNode", nodes.numberOfConnectedNodes());
        result.put("nodes", nodes.getNodeStatuses());
        result.put("version", ProxyConfig.getProxyConfig().getVersion());
        result.put("nodeIdleTimeout", ProxyConfig.getProxyConfig().getIdleTimeout());

        return ResponseEntity.status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);

    }

    public ResponseEntity<?> setIdleTimeout(String idleTimeout) {

        try {
            int timeout = Integer.parseInt(idleTimeout);

            if (timeout > 0 && timeout < 1800) {
                ProxyConfig.getProxyConfig().setIdleTimeout(timeout);
            } else {
                throw new RuntimeException();
            }

        } catch (Exception ignore) {
            return ResponseEntity.status(400)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Некорректное значение параметра idleTimeout. Таймаут должен быть в диапазоне 0 - 1800 сек."));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("nodeIdleTimeout", ProxyConfig.getProxyConfig().getIdleTimeout());

        return ResponseEntity.status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);

    }

}
