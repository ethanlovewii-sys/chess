package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> connections;

    public ConnectionManager() {
        connections = new ConcurrentHashMap<>();
    }

    public void add(int gameId, String username, Session session) {
        connections.putIfAbsent(gameId, new ConcurrentHashMap<>());
        connections.get(gameId).put(username, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

}
