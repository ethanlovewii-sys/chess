package dataaccess;

import model.GameData;

public interface GameDAO {
    int createGame(String gameName);

    Object getGame(int gameID);

    int getID();
}
