package server.handler;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import server.ResponseException;
import service.UserService;

public class UserHandler {

    private final UserService service;

    public UserHandler(UserDAO userDAO, AuthDAO authDAO) {
        service = new UserService(userDAO, authDAO);
    }

    public void register(Context context) throws ResponseException, DataAccessException {
        //Convert JSON -> Java object
        RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);

        //Call related service
        RegisterResult result = service.register(request);

        //If reached, send OK status and the register response
        context.status(200);
        context.result(new Gson().toJson(result));
    }

    public  void login(Context context) throws DataAccessException, ResponseException {
        //Convert JSON -> Java object
        LoginRequest request = new Gson().fromJson(context.body(), LoginRequest.class);

        //Call related service
        LoginResult result = service.login(request);

        //If reached, send OK status and the register response
        context.status(200);
        context.result(new Gson().toJson(result));
    }

    public  void logout(Context context) throws DataAccessException, ResponseException  {
        String authToken = context.header("Authorization");

        //Call related service
        service.logout(authToken);

        //If reached, send OK status
        context.status(200);
    }
}