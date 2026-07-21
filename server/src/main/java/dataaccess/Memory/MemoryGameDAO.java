package dataaccess.Memory;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
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

    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    public void deleteAll() {
        games.clear();
    }
}
