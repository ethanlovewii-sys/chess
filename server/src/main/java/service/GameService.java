package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;
import server.ResponseException;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws ResponseException {
        System.out.println(authToken);
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
}
