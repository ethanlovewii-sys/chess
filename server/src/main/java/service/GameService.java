package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import server.ResponseException;

import java.sql.SQLException;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws ResponseException, DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new ResponseException("Error: unauthorized", 401);
        }
        if (request.gameName() == null) {
            throw new ResponseException("Error: bad request", 400);
        }

        int gameID = gameDAO.createGame(request.gameName());

        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws ResponseException, DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new ResponseException("Error: unauthorized", 401);
        }
        if (request.gameID() == null || request.playerColor() == null) {
            throw new ResponseException("Error: bad request", 400);
        }

        GameData gameData = gameDAO.getGame(request.gameID());

        if (gameData == null) {
            throw new ResponseException("Error: bad request", 400);
        }

        if (request.playerColor().equals(ChessGame.TeamColor.WHITE)) {
            if (gameData.whiteUsername() != null) {
                throw new ResponseException("Error: already taken", 403);
            }
        }
        if (request.playerColor().equals(ChessGame.TeamColor.BLACK)) {
            if (gameData.blackUsername() != null) {
                throw new ResponseException("Error: already taken", 403);
            }
        }
        gameDAO.addPlayer(gameData.gameID(), authData.username(), request.playerColor());
    }

    public ListGamesResult listGames(String authToken) throws ResponseException, SQLException, DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new ResponseException("Error: unauthorized", 401);
        }

        return (new ListGamesResult(gameDAO.listGames()));
    }

    public void clear() throws ResponseException, DataAccessException, SQLException {
        userDAO.deleteAll();
        authDAO.deleteAll();
        gameDAO.deleteAll();
    }
}
