package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(String authToken, String username);

    void deleteAll();

    AuthData getAuthData(String authToken);

    void deleteAuth(String authToken);
}
