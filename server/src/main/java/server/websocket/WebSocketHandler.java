package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;

    public WebSocketHandler(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws ResponseException {
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

    private void connect(Session session, String authToken, Integer gameId) throws ResponseException {
        AuthData authData = authDAO.getAuthData(authToken);
        connections.add(gameId, authData.username(), session);
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