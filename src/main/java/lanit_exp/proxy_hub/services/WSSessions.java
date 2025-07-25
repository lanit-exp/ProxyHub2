package lanit_exp.proxy_hub.services;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WSSessions {

    private final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    public void registerSession(String sessionId, WebSocketSession session) {
        SESSIONS.put(sessionId, session);
    }

    public void deleteSession(String sessionId) {
        SESSIONS.remove(sessionId);
    }

    public WebSocketSession getSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }

}
