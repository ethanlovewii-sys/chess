package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    int createGame(String gameName);

    GameData getGame(int gameID);

    int getID();

    void addPlayer(int gameID, String username, ChessGame.TeamColor teamColor);
}
