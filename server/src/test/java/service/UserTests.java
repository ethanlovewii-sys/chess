package service;

import dataaccess.*;
import model.UserData;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;
import passoff.model.TestUser;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import server.Exception.AlreadyTakenException;
import server.ResponseException;

import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTests {

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();

    UserService service = new UserService(userDAO, authDAO);

    @BeforeEach
    public void setup() throws ResponseException, DataAccessException {
        service.clear();
        //one user already logged in
        service.register(new RegisterRequest("ExistingUser", "existingUserPassword", "eu@mail.com"));
    }

    @Test
    void register() throws Exception{
        RegisterRequest newUser = new RegisterRequest("NewUser", "NewUserPassword", "nu@mail.com");
        service.register(newUser);
        UserData userFound = userDAO.getUser("NewUser");
        assertEquals("NewUser", userFound.username());
        assertEquals("NewUserPassword", userFound.password());
        assertEquals("nu@mail.com", userFound.email());
    }

    @Test
    void doubleRegister() throws Exception {
        RegisterRequest newUser = new RegisterRequest("NewUser", "NewUserPassword", "nu@mail.com");
        service.register(newUser);

        assertThrows(ResponseException.class, () -> {
            service.register(newUser);
        });
    }

    @Test
    void normalLogin() throws Exception{
        LoginResult loggedInUser = service.login(new LoginRequest("ExistingUser","existingUserPassword"));
        assertEquals("ExistingUser", loggedInUser.username());
        Assertions.assertNotNull(loggedInUser.authToken());
    }

    @Test
    void badPasswordLogin() {
        assertThrows(ResponseException.class, () -> {
            service.login(new LoginRequest("ExistingUser","BADPASSWORD"));
        });
    }
}
