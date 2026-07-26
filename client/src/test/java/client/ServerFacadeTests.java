package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void beforeEach() throws ResponseException {
        facade.clear();
        facade.register(new RegisterRequest("existingUser", "password", "email"));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void goodRegister() throws ResponseException {
        RegisterResult result = facade.register(new RegisterRequest("username", "password", "email"));
        assertEquals("username", result.username());
    }

    @Test
    public void badRegister() throws ResponseException {
        assertThrows(ResponseException.class, () -> {
            facade.register(new RegisterRequest("existingUser", "password", "email"));
        });
    }

    @Test
    public void goodLogin() throws ResponseException {
        LoginResult result = facade.login(new LoginRequest("existingUser", "password"));
        assertEquals("existingUser", result.username());
    }

    @Test
    public void badLogin() throws ResponseException {
        assertThrows(ResponseException.class, () -> {
            facade.login(new LoginRequest("existingUser", "badPassword"));
        });
        assertThrows(ResponseException.class, () -> {
            facade.login(new LoginRequest("existingUser", null));
        });
    }

    @Test
    public void goodLogout() throws ResponseException {
        facade.listGames();
        facade.logout();
        assertThrows(ResponseException.class, () -> {
            facade.listGames();
        });
    }

    @Test
    public void badLogout() throws ResponseException {
        facade.logout();
        assertThrows(ResponseException.class, () -> {
            facade.logout();
        });
    }

    @Test
    public void goodCreateGame() throws ResponseException {
        ListGamesResult result = facade.listGames();
        assertEquals(0, result.games().size());
        facade.createGame(new CreateGameRequest("newGameName"));
        ListGamesResult newResult = facade.listGames();
        assertEquals(1, newResult.games().size());
    }

    @Test
    public void badCreateGame() throws ResponseException {
        assertThrows(ResponseException.class, () -> {
            facade.createGame(new CreateGameRequest(null));
        });
        facade.logout();
        assertThrows(ResponseException.class, () -> {
            facade.createGame(new CreateGameRequest("newGameName"));
        });
    }

    @Test
    public void goodListGames() throws ResponseException {
        facade.createGame(new CreateGameRequest("game1"));
        facade.createGame(new CreateGameRequest("game2"));
        facade.createGame(new CreateGameRequest("game3"));
        ListGamesResult result = facade.listGames();
        assertEquals(3, result.games().size());
    }

    @Test
    public void badListGames() throws ResponseException {
        facade.logout();
        assertThrows(ResponseException.class, () -> {
            facade.listGames();
        });
    }

    @Test
    public void goodJoinGame() throws ResponseException {
        CreateGameResult gameResult = facade.createGame(new CreateGameRequest("game1"));
        facade.listGames();
        facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameResult.gameID()));
        ListGamesResult result = facade.listGames();
        assertEquals("existingUser", result.games().getFirst().whiteUsername());
        assertNull(result.games().getFirst().blackUsername());
    }

    @Test
    public void badJoinGame() throws ResponseException {
        assertThrows(ResponseException.class, () -> {
            facade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1));
        });
    }

}
