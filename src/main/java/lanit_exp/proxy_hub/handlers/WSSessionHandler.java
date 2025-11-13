package lanit_exp.proxy_hub.handlers;

import lanit_exp.proxy_hub.services.WSSessions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

@Slf4j
public class WSSessionHandler extends WebSocketHandlerDecorator {

    private final WSSessions wsSessions;

    public WSSessionHandler(WebSocketHandler delegate, WSSessions wsSessions) {
        super(delegate);
        this.wsSessions = wsSessions;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        wsSessions.registerSession(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        super.afterConnectionClosed(session, closeStatus);
        wsSessions.deleteSession(session.getId());
        log.info("[SESSION CLOSE] {} - {}", session.getId(), closeStatus.getReason());
    }
}
