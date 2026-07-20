package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.Memory.MemoryAuthDAO;
import dataaccess.Memory.MemoryGameDAO;
import dataaccess.Memory.MemoryUserDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.RegisterResult;
import server.ResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTests {

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();

    UserService userService = new UserService(userDAO, authDAO);
    GameService gameService = new GameService(gameDAO, authDAO, userDAO);

    String authToken;
    int existingGameID;

    @BeforeEach
    public void setup() throws ResponseException, DataAccessException {
        //Clear all data, create existing user, and game.
        gameService.clear();
        RegisterResult existingUser = userService.register(new RegisterRequest("ExistingUser", "existingUserPassword", "eu@mail.com"));
        authToken = existingUser.authToken();
        CreateGameResult existingGame = gameService.createGame(new CreateGameRequest("ExistingGame"), authToken);
        existingGameID = existingGame.gameID();
    }

    @Test
    void normalCreateGame() throws ResponseException, DataAccessException {
        CreateGameResult result = gameService.createGame(new CreateGameRequest("newGameName"), authToken);
        GameData gameData = gameDAO.getGame(result.gameID());
        assertEquals("newGameName", gameData.gameName());
        Assertions.assertNull(gameData.whiteUsername());
    }

    @Test
    void unauthorizedCreate() {
        assertThrows(ResponseException.class, () -> {
            gameService.createGame(new CreateGameRequest("newGameName"), "5235234534");
        });
    }

    @Test
    void normalJoinGame() throws ResponseException, DataAccessException {
        RegisterResult newUser = userService.register(new RegisterRequest("NewUser", "NewPassword", "new@mail.com"));
        gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, existingGameID), newUser.authToken());

        GameData gameData = gameDAO.getGame(existingGameID);
        assertEquals("NewUser", gameData.whiteUsername());
        Assertions.assertNull(gameData.blackUsername());
    }

    @Test
    void joinNonexistentGame() {
        assertThrows(ResponseException.class, () -> {
            gameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 5481), authToken);
        });
    }

    @Test
    void normalListGames() throws ResponseException, DataAccessException {
        gameService.createGame(new CreateGameRequest("Game2"), authToken);
        gameService.createGame(new CreateGameRequest("Game3"), authToken);
        gameService.createGame(new CreateGameRequest("Game4"), authToken);
        ListGamesResult games = gameService.listGames(authToken);
        assertEquals(4, games.games().size());
        assertEquals("Game2", games.games().get(1).gameName());
    }

    @Test
    void unauthorizedListGames() throws ResponseException, DataAccessException {
        gameService.createGame(new CreateGameRequest("Game2"), authToken);
        gameService.createGame(new CreateGameRequest("Game3"), authToken);
        gameService.createGame(new CreateGameRequest("Game4"), authToken);
        assertThrows(ResponseException.class, () -> {
            gameService.listGames("5498198");
        });
    }
}