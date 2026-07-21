package dataaccess;

import dataaccess.MySql.MySqlGameDAO;
import dataaccess.MySql.MySqlUserDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ResponseException;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    UserDAO userDAO = new MySqlUserDAO();
    GameDAO gameDAO = new MySqlGameDAO();

    String username = "username";
    String password = "password";
    String email = "email@mail.com";
    String gameName = "gameName";


    public GameTest() throws ResponseException, DataAccessException {
    }

    @BeforeEach
    void setUp() throws ResponseException, DataAccessException {
        gameDAO.deleteAll();
    }

    @Test
    public void normalCreateGame() throws ResponseException, DataAccessException {
        int gameID = gameDAO.createGame(gameName);
        GameData gameData = gameDAO.getGame(gameID);
        Assertions.assertNotNull(gameData);
        assertEquals(gameName, gameData.gameName());
        assertEquals(gameID, gameData.gameID());
        assertNull(gameData.whiteUsername());
        assertNull(gameData.blackUsername());
    }

    @Test
    public void badCreateGame() {
        assertThrows(ResponseException.class, () -> {
            gameDAO.createGame(null);
        });
    }

    @Test
    public void normalGetGame() throws ResponseException, DataAccessException {
        int gameID = gameDAO.createGame(gameName);
        //Add changing the game here and seeing if it gets it right
        GameData gameData = gameDAO.getGame(gameID);
        Assertions.assertNotNull(gameData);
        assertEquals(gameName, gameData.gameName());
        assertEquals(gameID, gameData.gameID());
        assertNull(gameData.whiteUsername());
        assertNull(gameData.blackUsername());
    }

    @Test
    public void badGetGame() {
        assertThrows(ResponseException.class, () -> {
            gameDAO.getGame(123);
        });
    }
//
//    @Test
//    public void normalDeleteAll() throws ResponseException, DataAccessException {
//        userDAO.createUser(username, password, email);
//        userDAO.createUser("username2", password, email);
//        userDAO.createUser("username3", password, email);
//        userDAO.deleteAll();
//        assertThrows(ResponseException.class, () -> {
//            userDAO.getUser(username);
//        });
//        assertThrows(ResponseException.class, () -> {
//            userDAO.getUser("username2");
//        });
//        assertThrows(ResponseException.class, () -> {
//            userDAO.getUser("username3");
//        });
//    }
}
