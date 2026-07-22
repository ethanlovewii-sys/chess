package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlGame extends MySqlParent implements GameDAO {

    public MySqlGame() {
    }

    public int createGame(String gameName) throws ResponseException, DataAccessException {
        if (gameName == null || gameName.isEmpty()) {
            throw new ResponseException("Must provide a game name", 500);
        }
        var statement = """
                INSERT INTO games
                (whiteUsername, blackUsername, gameName, gameState)
                 VALUES (?, ?, ?, ?)
                """;
        return executeUpdate(statement, null, null, gameName, new Gson().toJson(new ChessGame()));
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
            throw new ResponseException(String.format("Error: Unable to read data: %s", e.getMessage()), 500);
        }
        return null;
    }

    //Turns the result set to Game Data Object
    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt(1);
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String jsonGameState = rs.getString("gameState");
        ChessGame gameState = new Gson().fromJson(jsonGameState, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameState);
    }


    public void addPlayer(int gameID, String username, ChessGame.TeamColor teamColor) throws ResponseException, DataAccessException {
        String statement = null;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            statement = """
                    UPDATE games
                    SET whiteUsername = ?
                    WHERE gameID = ?
                    """;
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            statement = """
                    UPDATE games
                    SET blackUsername = ?
                    WHERE gameID = ?
                    """;
        }
        executeUpdate(statement, username, gameID);
    }

    public List<GameData> listGames() throws DataAccessException, SQLException {
        List<GameData> gameList = new ArrayList<>();
        String statement = """
                SELECT gameID, whiteUsername, blackUsername, gameName, gameState
                FROM games
                """;
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement);
             var rs = ps.executeQuery()) {

            //Loop through the game table data and pull out the game data
            while (rs.next()) {
                int gameID = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                String gameState = rs.getString("gameState");

                //Convert ChessGame JSON back to an object
                ChessGame gameStateObject = new Gson().fromJson(gameState, ChessGame.class);

                gameList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, gameStateObject));
            }
            return gameList;
        }
    }

    public void deleteAll() throws ResponseException, DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM games")) {

            ps.executeUpdate();

        }
    }

}
