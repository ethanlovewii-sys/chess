package dataaccess;

import chess.ChessGame;
import model.GameData;
import server.ResponseException;

import java.sql.SQLException;
import java.util.List;

public interface GameDAO {
    int createGame(String gameName) throws ResponseException, DataAccessException;

    GameData getGame(int gameID) throws ResponseException;

    void addPlayer(int gameID, String username, ChessGame.TeamColor teamColor) throws ResponseException, DataAccessException;

    List<GameData> listGames() throws DataAccessException, SQLException;

    void deleteAll() throws ResponseException, DataAccessException, SQLException;
}
