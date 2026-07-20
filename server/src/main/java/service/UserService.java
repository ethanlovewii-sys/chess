package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
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

        if (user != null) {
            throw new ResponseException("Error: already taken", 403);
        }
        if (username == null || password == null || email == null) {
            throw new ResponseException("Error: bad request", 400);
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        userDAO.createUser(username, hashedPassword, email);
        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, username);

        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException, ResponseException {
        String username = request.username();
        String password = request.password();

        if (username == null || password == null) {
            throw new ResponseException("Error: bad request", 400);
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData user = userDAO.getUser(username);

        if (user == null) {
            throw new ResponseException("Error: unauthorized", 401);
        }
        if (!user.password().equals(hashedPassword)) {
            throw new ResponseException("Error: unauthorized", 401);
        }

        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(authToken, username);

        return new LoginResult(username, authToken);
    }

    public void logout(String authToken) throws ResponseException, DataAccessException {
        AuthData authData = authDAO.getAuthData(authToken);

        if (authData == null) {
            throw new ResponseException("Error: unauthorized", 401);
        }

        authDAO.deleteAuth(authToken);
    }
}
