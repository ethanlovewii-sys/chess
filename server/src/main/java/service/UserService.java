package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;
import server.ResponseException;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException, DataAccessException {
        String username = request.username();
        String password = request.password();
        String email = request.email();
        UserData user = userDAO.getUser(username);
        if (user != null){
            System.out.println("Exception reached");
            throw new ResponseException("Error: already taken", 403);
        }
        if (username == null || password == null || email == null){
            throw new ResponseException("Error: bad request", 400);
        }
        userDAO.createUser(username, password, email);
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, username);

        return new RegisterResult(username, authToken, null);
    }

    public void clear(){
        userDAO.deleteAll();
        authDAO.deleteAll();
    }
}
