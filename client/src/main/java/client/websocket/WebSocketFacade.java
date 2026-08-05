package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;

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
        session.addMessageHandler(String.class, message -> {

        });
    }
    public void connect(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(CONNECT, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        UserGameCommand command = new UserGameCommand(MAKE_MOVE, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void resign(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(RESIGN, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void leave(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(LEAVE, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }
}
