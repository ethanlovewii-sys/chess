package server.handler;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;
import request.RegisterRequest;
import result.RegisterResult;
import server.ResponseException;
import service.UserService;

public class UserHandler {

    //Initialize memory
    private static final UserDAO userDAO = new MemoryUserDAO();
    private static final AuthDAO authDAO = new MemoryAuthDAO();

    public static void register(Context context) throws ResponseException, DataAccessException {
        //Convert JSON -> Java object
        RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);

        //Call related service
        UserService service = new UserService(userDAO, authDAO);
        RegisterResult result = service.register(request);

        //If reached, send OK status and the register response
        context.status(200);
        context.result(new Gson().toJson(result));
    }

    public static void clear(Context context) {
        UserService service = new UserService(userDAO, authDAO);
        service.clear();

        context.status(200);
        context.result();
    }
}