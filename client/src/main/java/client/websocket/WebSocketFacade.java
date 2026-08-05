package client.websocket;

import chess.ChessMove;
import client.ClientState;
import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint {
    public Session session;
    private final Gson gson = new Gson();

    public WebSocketFacade() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);
    }

    // This method must be overridden, but we don't have to do anything with it
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        session.addMessageHandler(String.class, json -> {
            ServerMessage message = gson.fromJson(json, ServerMessage.class);
        });
    }
    public void connect(int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(CONNECT, ClientState.getAuthToken(), gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void makeMove(int gameID, ChessMove move) throws IOException {
        UserGameCommand command = new UserGameCommand(MAKE_MOVE, ClientState.getAuthToken(), gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void resign(int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(RESIGN, ClientState.getAuthToken(), gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void leave(int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(LEAVE, ClientState.getAuthToken(), gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public interface ServerMessageObserver {
        void notify(ServerMessage message);
    }
}
