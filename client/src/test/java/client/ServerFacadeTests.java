package client;

import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

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

}
