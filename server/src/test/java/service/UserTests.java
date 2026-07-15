package service;

import dataaccess.*;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;
import passoff.model.TestUser;
import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import result.RegisterResult;
import server.Exception.AlreadyTakenException;
import server.ResponseException;

import java.net.HttpURLConnection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTests {

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();

    UserService userService = new UserService(userDAO, authDAO);
    GameService gameService = new GameService(gameDAO, authDAO);

    RegisterResult existingUser;
    String authToken;

    @BeforeEach
    public void setup() throws ResponseException, DataAccessException {
        userService.clear();
        //one user already logged in
        RegisterResult existingUser = userService.register(new RegisterRequest("ExistingUser", "existingUserPassword", "eu@mail.com"));
        authToken = existingUser.authToken();
    }

    @Test
    void register() throws Exception{
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
    void normalLogin() throws Exception{
        LoginResult loggedInUser = userService.login(new LoginRequest("ExistingUser","existingUserPassword"));
        assertEquals("ExistingUser", loggedInUser.username());
        Assertions.assertNotNull(loggedInUser.authToken());
        Assertions.assertNotNull(authDAO.getAuthData(loggedInUser.authToken()));
        Assertions.assertNotNull(userDAO.getUser(loggedInUser.username()));
    }

    @Test
    void badPasswordLogin() {
        assertThrows(ResponseException.class, () -> {
            userService.login(new LoginRequest("ExistingUser","BADPASSWORD"));
        });
    }

    @Test
    void normalLogout() throws ResponseException, DataAccessException {
        userService.logout(authToken);
        Assertions.assertNull(authDAO.getAuthData(authToken));
        Assertions.assertNotNull(userDAO.getUser("ExistingUser"));
    }

    @Test
    void alreadyLoggedOut() throws ResponseException, DataAccessException {
        userService.logout(authToken);
        assertThrows(ResponseException.class, () -> {
            userService.logout(authToken);
        });
    }

    @Test
    void normalCreateGame() throws ResponseException {
        CreateGameResult result = gameService.createGame(new CreateGameRequest("newGameName"), authToken);
        Assertions.assertNotNull(result.gameID());
        GameData gameData = (GameData) gameDAO.getGame(result.gameID());
        assertEquals(gameData.gameName(), "newGameName");
        Assertions.assertNull(gameData.whiteUsername());
    }

    @Test
    void unauthorizedCreate() {
        assertThrows(ResponseException.class, () -> {
            gameService.createGame(new CreateGameRequest("newGameName"), "5235234534");
        });
    }
}
