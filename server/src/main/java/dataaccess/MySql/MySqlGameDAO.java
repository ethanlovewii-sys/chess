package dataaccess.MySql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MySqlGameDAO extends MySqlParent implements GameDAO {
    public int createGame(String gameName) throws ResponseException, DataAccessException {
        var statement = """
        INSERT INTO games
        (gameID, whiteUsername, blackUsername, gameName, gameState)
         VALUES (?, ?, ?, ?, ?)
        """;
        return executeUpdate(statement,  null, null, gameName, new Gson().toJson( new ChessGame()));
    }

    public GameData getGame(int gameID) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameState FROM games WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, String.valueOf(gameID));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(String.format("Unable to read data: %s", e.getMessage()), 400);
        }
        throw new ResponseException("Error: unauthorized", 401);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt(1);
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var jsonGameState = rs.getString("gameState");
        ChessGame gameState = new Gson().fromJson(jsonGameState, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameState);
    }

    public int getID() {
        return 0;
    }

    public void addPlayer(int gameID, String username, ChessGame.TeamColor teamColor) {

    }

    public List<GameData> listGames() {
        return List.of();
    }

    public void deleteAll() {

    }

    @Override
    protected String[] getCreateStatements() {
        return new String[]{
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(255),
              `blackUsername` VARCHAR(255),
              `gameName` VARCHAR(255),
              gameState JSON,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
    }
}
