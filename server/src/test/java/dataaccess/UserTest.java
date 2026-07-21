package dataaccess;

import dataaccess.MySql.MySqlUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ResponseException;


import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    UserDAO userDAO = new MySqlUserDAO();

    String username = "username";
    String password = "password";
    String email = "email@mail.com";

    public UserTest() throws ResponseException, DataAccessException {
    }

    @BeforeEach
    void setUp() throws ResponseException, DataAccessException, SQLException {
        userDAO.deleteAll();
    }

    @Test
    public void normalCreateUser() throws ResponseException, DataAccessException {
        userDAO.createUser(username, password, email);
        UserData userData = userDAO.getUser(username);
        Assertions.assertNotNull(userData);
        assertEquals(username, userData.username());
    }

    @Test
    public void badCreateUser() {
        assertThrows(ResponseException.class, () -> {
            userDAO.createUser(username, null, email);
        });
    }

    @Test
    public void normalGetUser() throws ResponseException, DataAccessException {
        userDAO.createUser(username, password, email);
        UserData userData = userDAO.getUser(username);
        Assertions.assertNotNull(userData);
        assertEquals(username, userData.username());
        assertEquals(password, userData.password());
        assertEquals(email, userData.email());
    }

    @Test
    public void badGetUser() {
        assertThrows(ResponseException.class, () -> {
            userDAO.getUser("badUsername");
        });
    }

    @Test
    public void normalDeleteAll() throws ResponseException, DataAccessException, SQLException {
        userDAO.createUser(username, password, email);
        userDAO.createUser("username2", password, email);
        userDAO.createUser("username3", password, email);
        userDAO.deleteAll();
        assertThrows(ResponseException.class, () -> {
            userDAO.getUser(username);
        });
        assertThrows(ResponseException.class, () -> {
            userDAO.getUser("username2");
        });
        assertThrows(ResponseException.class, () -> {
            userDAO.getUser("username3");
        });
    }
}