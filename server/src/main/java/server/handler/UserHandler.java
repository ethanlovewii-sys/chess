package server.handler;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import request.RegisterRequest;
import result.ErrorResult;
import result.RegisterResult;
import server.Exception.AlreadyTakenException;
import server.Exception.BadRequestException;
import service.UserService;

public class UserHandler {
    public static void register(@NotNull Context context) {
        try{
            //Convert JSON -> Java object
            RegisterRequest request = context.bodyAsClass(RegisterRequest.class);

            //Call related service
            UserService service = new UserService();
            RegisterResult result = service.register(request);

            context.status(200);
            context.json(result);

        } catch (BadRequestException e){
            context.status(400);
            context.json(new ErrorResult(e.getMessage()));

        }catch (AlreadyTakenException e){
            context.status(403);
            context.json(new ErrorResult(e.getMessage()));

        }catch (Exception e) {

            context.status(500);
            context.json(new ErrorResult("Error: " + e.getMessage()));
        }

    }
}
//Body	{ "username":"", "password":"", "email":"" }
//Success response	[200] { "username":"", "authToken":"" }
//Failure response	[400] { "message": "Error: bad request" }
//Failure response	[403] { "message": "Error: already taken" }
//Failure response	[500] { "message": "Error: (description of error)" }