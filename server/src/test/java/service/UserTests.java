package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;
import passoff.model.TestUser;
import request.RegisterRequest;
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

    @Test
    void register() throws Exception{
        RegisterRequest newUser = new RegisterRequest("NewUser", "NewUserPassword", "nu@mail.com");
        service.register(newUser);
        UserData userFound = userDAO.getUser("NewUser");
        assertEquals(userFound.username(), "NewUser");
        assertEquals(userFound.password(), "NewUserPassword");
        assertEquals(userFound.email(), "nu@mail.com");
    }

    @Test
    void doubleRegister() throws Exception {
        RegisterRequest newUser = new RegisterRequest("NewUser", "NewUserPassword", "nu@mail.com");
        service.register(newUser);

        assertThrows(ResponseException.class, () -> {
            service.register(newUser);
        });
    }
}
