package lanit_exp.proxy_hub.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WSController {

    @MessageMapping("/nodeMes")
    @SendTo("/topic/mes")
    public String messageHandler(String message) {
        return "ws mess: " + message;
    }
}
