package dataaccess.MySql;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;

import java.util.List;

public class MySqlGameDAO extends MySqlParent implements GameDAO {
    public int createGame(String gameName) {
        return 0;
    }

    public GameData getGame(int gameID) {
        return null;
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
        return new String[0];
    }
}
