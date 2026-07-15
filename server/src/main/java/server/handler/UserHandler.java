package server.handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import request.RegisterRequest;
import result.ErrorResult;
import result.RegisterResult;
import server.Exception.AlreadyTakenException;
import server.Exception.BadRequestException;
import service.UserService;

public class UserHandler {
    public static void register(@NotNull Context context) throws Exception {
            //Convert JSON -> Java object
            RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);

            //Call related service
            UserDAO userDAO = new MemoryUserDAO();
            AuthDAO authDAO = new MemoryAuthDAO();

            UserService service = new UserService(userDAO, authDAO);
            RegisterResult result = service.register(request);

            context.status(200);
            context.result(new Gson().toJson(result));

    }
}
//Body	{ "username":"", "password":"", "email":"" }
//Success response	[200] { "username":"", "authToken":"" }
//Failure response	[400] { "message": "Error: bad request" }
//Failure response	[403] { "message": "Error: already taken" }
//Failure response	[500] { "message": "Error: (description of error)" }