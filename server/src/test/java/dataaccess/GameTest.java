package dataaccess;

import chess.ChessGame;
import dataaccess.mysql.MySqlGame;
import dataaccess.mysql.MySqlUser;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ResponseException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    UserDAO userDAO = new MySqlUser();
    GameDAO gameDAO = new MySqlGame();

    String username = "username";
    String password = "password";
    String email = "email@mail.com";
    String gameName = "gameName";


    public GameTest() throws ResponseException, DataAccessException {
    }

    @BeforeEach
    void setUp() throws ResponseException, DataAccessException, SQLException {
        gameDAO.deleteAll();
    }

    @Test
    public void normalCreateGame() throws ResponseException, DataAccessException {
        int gameID = gameDAO.createGame(gameName);
        GameData gameData = gameDAO.getGame(gameID);
        Assertions.assertNotNull(gameData);
        assertEquals(gameName, gameData.gameName());
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
    public void badGetGame() throws ResponseException {
        assertNull(gameDAO.getGame(123));
    }

    @Test
    public void normalAddPlayer() throws ResponseException, DataAccessException {
        int gameID = gameDAO.createGame(gameName);
        gameDAO.addPlayer(gameID, username, ChessGame.TeamColor.WHITE);
        GameData gameData = gameDAO.getGame(gameID);
        assertEquals(username, gameData.whiteUsername());
        assertNull(gameData.blackUsername());

        gameDAO.addPlayer(gameID, "blackUser", ChessGame.TeamColor.BLACK);
        gameData = gameDAO.getGame(gameID);
        assertEquals("blackUser", gameData.blackUsername());
        assertEquals(username, gameData.whiteUsername());
    }

    @Test
    public void badAddPlayer(){
        assertThrows(ResponseException.class, () -> {
            gameDAO.addPlayer(123, username, ChessGame.TeamColor.WHITE);
        });

    }

    @Test
    public void normalListGames() throws ResponseException, DataAccessException, SQLException {
        List<GameData> gameList = gameDAO.listGames();
        assertEquals(0, gameList.size());
        gameDAO.createGame("game1");
        gameDAO.createGame("game1");
        gameDAO.createGame("game1");
        gameList = gameDAO.listGames();
        assertEquals(3, gameList.size());
        assertEquals("game1", gameList.get(0).gameName());
    }

    @Test
    public void badListGames() throws ResponseException, DataAccessException {
        assertThrows(ResponseException.class, () -> {
            gameDAO.createGame(null);
        });
    }

    @Test
    public void deleteAll() throws ResponseException, DataAccessException, SQLException {
        gameDAO.createGame(gameName);
        gameDAO.createGame("game2");
        gameDAO.createGame("game3");
        gameDAO.deleteAll();
        List<GameData> gameList = gameDAO.listGames();
        assertEquals(0, gameList.size());
    }

}
