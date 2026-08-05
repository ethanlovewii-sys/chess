package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public static ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> connections;

    public ConnectionManager() {
        connections = new ConcurrentHashMap<>();
    }

    public void add(int gameId, String username, Session session) {
        connections.putIfAbsent(gameId, new ConcurrentHashMap<>());
        connections.get(gameId).put(username, session);
    }

    public void remove(int gameId, Session session) {
        connections.get(gameId).remove(session);
    }

    public static void broadcast(int gameId, Notification notification) throws IOException {
        String msg = notification.toString();
        var gameConnections = connections.get(gameId);

        if (gameConnections == null) {
            return;
        }

        for (Session session: gameConnections.values()) {
            session.getRemote().sendString(msg);
        }
    }

}
