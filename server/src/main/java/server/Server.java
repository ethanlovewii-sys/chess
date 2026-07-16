package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import result.ErrorResult;
import server.handler.GameHandler;
import server.handler.UserHandler;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        UserHandler userHandler = new UserHandler(userDAO, authDAO);
        GameHandler gameHandler = new GameHandler(userDAO, authDAO, gameDAO);

        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);
        javalin.get("/game", gameHandler::listGames);

        javalin.delete("/db", gameHandler::clear);
        javalin.exception(ResponseException.class, this::exceptionHandler);

    }

    private void exceptionHandler(ResponseException ex, Context context) {
        context.status(ex.statusCode());
        context.result(new Gson().toJson(new ErrorResult(ex.getMessage())));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
