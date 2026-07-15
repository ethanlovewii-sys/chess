package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();
    int gameID = 1;

    public int createGame(String gameName) {
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, gameData);
        return gameID++;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public int getID() {
        return gameID;
    }

    public void addPlayer(int oldGameID, String username, ChessGame.TeamColor teamColor) {
        GameData oldGame = getGame(oldGameID);
        GameData newgameData = null;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            newgameData = new GameData(oldGameID, username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        }
        if (teamColor == ChessGame.TeamColor.BLACK) {
            newgameData = new GameData(oldGameID, oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());
        }
        games.put(oldGameID, newgameData);
    }
}
