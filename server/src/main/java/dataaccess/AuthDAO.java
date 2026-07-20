package dataaccess;

import model.AuthData;
import server.ResponseException;

public interface AuthDAO {
    void createAuth(String authToken, String username) throws ResponseException, DataAccessException;

    void deleteAll() throws ResponseException, DataAccessException;

    AuthData getAuthData(String authToken) throws ResponseException;

    void deleteAuth(String authToken) throws ResponseException, DataAccessException;


}
