package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;
import server.Exception.AlreadyTakenException;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        String username = request.username();
        String password = request.password();
        String email = request.email();
        UserData user = userDAO.getUser(username);
        if (user != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        userDAO.createUser(username, password, email);
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, username);

        return new RegisterResult(username, authToken, null);
    }
}
