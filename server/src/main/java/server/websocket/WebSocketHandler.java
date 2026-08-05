package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws ResponseException, IOException {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(ctx.session, command.getAuthToken(), command.getGameID());
            case MAKE_MOVE -> makeMove(ctx.session);
            case LEAVE -> leave(ctx.session);
            case RESIGN -> resign(ctx.session);
        }
    }

    private void resign(Session session) {
    }

    private void leave(Session session) {
    }

    private void makeMove(Session session) {

    }

    private void connect(Session session, String authToken, Integer gameId) throws ResponseException, IOException {
        AuthData authData = authDAO.getAuthData(authToken);
        GameData game = gameDAO.getGame(gameId);

        connections.add(gameId, authData.username(), session);

        //Send game to new client
        LoadGameMessage loadGame = new LoadGameMessage(game.game());
        session.getRemote().sendString(new Gson().toJson(loadGame));

        //Find role and notify everyone.
        String role;
        if (authData.username().equals(game.whiteUsername())) {
            role = "White";
        } else {
            role = "Black";
        }
        NotificationMessage notification = new NotificationMessage(authData.username() + " has Joined as " + role);
        ConnectionManager.broadcast(gameId, authData.username(), notification);
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(session, notification);
//    }
//
//    private void exit(String visitorName, Session session) throws IOException {
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
//    }
}