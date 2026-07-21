package server.handler;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import server.ResponseException;
import service.GameService;

public class GameHandler {

    private final GameService service;

    public GameHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        service = new GameService(gameDAO, authDAO, userDAO);
    }

    public void createGame(Context context) throws ResponseException, DataAccessException {
        //Convert JSON -> Java object
        CreateGameRequest request = new Gson().fromJson(context.body(), CreateGameRequest.class);
        String authToken = context.header("Authorization");

        //Call related service
        CreateGameResult result = service.createGame(request, authToken);

        //If reached, send OK status and the register response
        context.status(200);
        context.result(new Gson().toJson(result));
    }

    public void joinGame(Context context) throws ResponseException, DataAccessException {
        //Convert JSON -> Java object
        JoinGameRequest request = new Gson().fromJson(context.body(), JoinGameRequest.class);
        String authToken = context.header("Authorization");

        //Call related service
        service.joinGame(request, authToken);

        //If reached, send OK status and the register response
        context.status(200);
    }

    public void listGames(Context context) throws ResponseException {
        //Convert JSON -> Java object
        String authToken = context.header("Authorization");

        //Call related service
        ListGamesResult result = service.listGames(authToken);

        //If reached, send OK status and the register response
        context.status(200);
        context.result(new Gson().toJson(result));
    }

    public void clear(Context context) throws ResponseException, DataAccessException {
        service.clear();
        context.status(200);
    }
}
