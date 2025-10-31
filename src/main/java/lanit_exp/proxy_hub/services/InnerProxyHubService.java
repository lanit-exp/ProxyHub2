package lanit_exp.proxy_hub.services;

import jakarta.servlet.http.HttpServletRequest;
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

    public ResponseEntity<?> getProxyHubStatus(HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        result.put("activeNode", nodes.numberOfConnectedNodes());
        result.put("nodes", nodes.getNodeStatuses());

        return ResponseEntity.status(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);

    }


}
