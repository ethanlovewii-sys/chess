package dataaccess;

import dataaccess.mysql.MySqlAuth;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ResponseException;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    AuthDAO authDAO = new MySqlAuth();

    String authToken =  UUID.randomUUID().toString();
    String username = "username";

    public AuthTest() throws ResponseException, DataAccessException {
    }

    @BeforeEach
    void setUp() throws ResponseException, DataAccessException, SQLException {
        authDAO.deleteAll();
    }

    @Test
    public void normalCreateAuth() throws ResponseException, DataAccessException {
        authDAO.createAuth(authToken, username);
        AuthData authData = authDAO.getAuthData(authToken);
        Assertions.assertNotNull(authData);
        assertEquals(username, authData.username());
    }

    @Test
    public void badCreateAuth() {
        assertThrows(ResponseException.class, () -> {
            authDAO.createAuth(authToken, null);
        });
    }

    @Test
    public void normalGetAuth() throws ResponseException, DataAccessException {
        authDAO.createAuth(authToken, username);
        AuthData authData = authDAO.getAuthData(authToken);
        Assertions.assertNotNull(authData);
        assertEquals(username, authData.username());
        assertEquals(authToken, authData.authToken());
    }

    @Test
    public void badGetAuth() throws ResponseException {
        assertNull(authDAO.getAuthData("12423-dfsd-24234"));

    }

    @Test
    public void normalDeleteAll() throws ResponseException, DataAccessException, SQLException {
        authDAO.createAuth(authToken, username);
        authDAO.createAuth("dfsd-wrwe-24242", "username2");
        authDAO.createAuth("dfsd-234234-dfsdf", "username3");
        authDAO.deleteAll();
        assertNull(authDAO.getAuthData(authToken));
        assertNull(authDAO.getAuthData("dfsd-wrwe-24242"));
        assertNull(authDAO.getAuthData("dfsd-234234-dfsdf"));
    }

    @Test
    public void normalDelete() throws ResponseException, DataAccessException {
        authDAO.createAuth(authToken, username);
        authDAO.deleteAuth(authToken);
        assertNull(authDAO.getAuthData(authToken));
    }

    @Test
    public void badDelete() throws ResponseException, DataAccessException {
        assertThrows(ResponseException.class, () -> {
            authDAO.deleteAuth("dfsd-wrwe-24242");
        });
    }
}
