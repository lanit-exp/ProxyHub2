package lanit_exp.proxy_hub.controllers;

import lanit_exp.proxy_hub.helpers.StringHelper;
import lanit_exp.proxy_hub.services.NodeMessageHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WSController {

    private final NodeMessageHolder messageHolder;

    @MessageMapping("/from")
    public void messageHandler(@Header(name = "request_id") String requestId, String message) {

        log.info("<<<<< RESPONSE ID '{}': {}", requestId, StringHelper.trimLargeString(message, 500));

        messageHolder.addMessage(requestId, message);
    }

}
