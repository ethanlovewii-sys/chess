package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();


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
    public void handleMessage(WsMessageContext ctx) throws ResponseException, IOException, InvalidMoveException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(ctx.session, command.getAuthToken(), command.getGameID());
            case MAKE_MOVE -> {
                MakeMoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                makeMove(ctx.session, moveCommand.getMove(), moveCommand.getAuthToken(), moveCommand.getGameID());
            }
            case LEAVE -> leave(ctx.session);
            case RESIGN -> resign(ctx.session);
        }
    }

    private void resign(Session session) {
    }

    private void leave(Session session) {
    }

    private void makeMove(Session session, ChessMove move, String authToken, int gameID) throws ResponseException, IOException, InvalidMoveException, DataAccessException {
        ChessPosition startPosition = move.getStartPosition();
        GameData gameData = gameDAO.getGame(gameID);
        AuthData authData = authDAO.getAuthData(authToken);
        ChessGame game = gameData.game();
        ChessPiece piece = game.getBoard().getPiece(startPosition);

        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Unrecognized user.");
            String json =  new Gson().toJson(errorMessage);
            session.getRemote().sendString(json);
            return;
        }

        if (!game.validMoves(startPosition).contains(move)) {
            ErrorMessage errorMessage = new ErrorMessage("That Move is not valid.");
            String json =  new Gson().toJson(errorMessage);
            session.getRemote().sendString(json);
            return;
        }

        ChessGame.TeamColor enemyColor = null;
        ChessGame.TeamColor teamColor = null;
        if (authData.username().equals(gameData.whiteUsername())) {
            teamColor = ChessGame.TeamColor.WHITE;
            enemyColor = ChessGame.TeamColor.BLACK;
        } else if (authData.username().equals(gameData.blackUsername())) {
            teamColor = ChessGame.TeamColor.BLACK;
            enemyColor = ChessGame.TeamColor.WHITE;
        }
        
        if (teamColor == null){
            ErrorMessage errorMessage = new ErrorMessage("Observers cannot make moves.");
            String json =  new Gson().toJson(errorMessage);
            session.getRemote().sendString(json);
            return;
        }

        if (piece.getTeamColor() != teamColor) {
            ErrorMessage errorMessage = new ErrorMessage("You can only move your pieces.");
            String json =  new Gson().toJson(errorMessage);
            session.getRemote().sendString(json);
            return;
        }

        try{
            game.makeMove(move);
        } catch  (InvalidMoveException e) {
            ErrorMessage errorMessage = new ErrorMessage("That Move is not valid.");
            String json =  new Gson().toJson(errorMessage);
            session.getRemote().sendString(json);
            return;
        }
        gameDAO.updateGame(game, gameID);

        LoadGameMessage loadGame = new LoadGameMessage(game);
        ConnectionManager.broadcast(gameID, null, loadGame);

        ChessPosition endPosition = move.getEndPosition();
        NotificationMessage notification = new NotificationMessage(authData.username() + " moved " + piece.toString() + " to " + endPosition.toString());
        ConnectionManager.broadcast(gameID, authData.username(), notification);

        if (game.isInCheckmate(enemyColor)){
            NotificationMessage mateNotification = new NotificationMessage(enemyColor + " is in CheckMate! " + teamColor + " Wins!!");
            ConnectionManager.broadcast(gameID, null, mateNotification);
        }
        else if (game.isInCheck(enemyColor)){
            NotificationMessage checkNotification = new NotificationMessage(enemyColor + " is in Check!");
            ConnectionManager.broadcast(gameID, null, checkNotification);
        }
        else if (game.isInStalemate(enemyColor)){
            NotificationMessage staleNotification = new NotificationMessage(enemyColor + " has no viable moves. Stalemate! It's a tie.");
            ConnectionManager.broadcast(gameID, null, staleNotification);
        }
    }

    private void connect(Session session, String authToken, Integer gameId) throws ResponseException, IOException {
        AuthData authData = authDAO.getAuthData(authToken);
        GameData game = gameDAO.getGame(gameId);

        if (game == null) {
            ErrorMessage errorMessage = new ErrorMessage("Game #" + gameId + " does not exist.");
            String json =  new Gson().toJson(errorMessage);
            session.getRemote().sendString(json);
            return;
        }

        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Unrecognized user.");
            String json =  new Gson().toJson(errorMessage);
            session.getRemote().sendString(json);
            return;
        }

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