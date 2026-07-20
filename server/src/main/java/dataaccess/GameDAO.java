package dataaccess;

import chess.ChessGame;
import model.GameData;
import server.ResponseException;

import java.util.List;

public interface GameDAO {
    int createGame(String gameName) throws ResponseException, DataAccessException;

    GameData getGame(int gameID) throws ResponseException;

    int getID();

    void addPlayer(int gameID, String username, ChessGame.TeamColor teamColor);

    List<GameData> listGames();

    void deleteAll();
}
