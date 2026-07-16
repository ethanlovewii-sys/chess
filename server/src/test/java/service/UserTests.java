package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import server.ResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTests {

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();

    UserService userService = new UserService(userDAO, authDAO);
    GameService gameService = new GameService(gameDAO, authDAO, userDAO);

    String authToken;

    @BeforeEach
    public void setup() throws ResponseException, DataAccessException {
        gameService.clear();
        RegisterResult existingUser = userService.register(new RegisterRequest("ExistingUser", "existingUserPassword", "eu@mail.com"));
        authToken = existingUser.authToken();
    }

    @Test
    void register() throws Exception {
        RegisterRequest newUser = new RegisterRequest("NewUser", "NewUserPassword", "nu@mail.com");
        userService.register(newUser);
        UserData userFound = userDAO.getUser("NewUser");
        assertEquals("NewUser", userFound.username());
        assertEquals("NewUserPassword", userFound.password());
        assertEquals("nu@mail.com", userFound.email());
    }

    @Test
    void doubleRegister() throws Exception {
        RegisterRequest newUser = new RegisterRequest("NewUser", "NewUserPassword", "nu@mail.com");
        userService.register(newUser);

        assertThrows(ResponseException.class, () -> {
            userService.register(newUser);
        });
    }

    @Test
    void normalLogin() throws Exception {
        LoginResult loggedInUser = userService.login(new LoginRequest("ExistingUser", "existingUserPassword"));
        assertEquals("ExistingUser", loggedInUser.username());
        Assertions.assertNotNull(loggedInUser.authToken());
        Assertions.assertNotNull(authDAO.getAuthData(loggedInUser.authToken()));
        Assertions.assertNotNull(userDAO.getUser(loggedInUser.username()));
    }

    @Test
    void badPasswordLogin() {
        assertThrows(ResponseException.class, () -> {
            userService.login(new LoginRequest("ExistingUser", "BADPASSWORD"));
        });
    }

    @Test
    void normalLogout() throws ResponseException, DataAccessException {
        userService.logout(authToken);
        Assertions.assertNull(authDAO.getAuthData(authToken));
        Assertions.assertNotNull(userDAO.getUser("ExistingUser"));
    }

    @Test
    void alreadyLoggedOut() throws ResponseException {
        userService.logout(authToken);
        assertThrows(ResponseException.class, () -> {
            userService.logout(authToken);
        });
    }
}
