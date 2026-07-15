package server;

import com.google.gson.Gson;
import io.javalin.*;
import result.ErrorResult;
import server.handler.UserHandler;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.post("/user", UserHandler::register);
        javalin.delete("/db", UserHandler::clear);
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
