package dataaccess;

import model.AuthData;
import server.ResponseException;

import java.sql.SQLException;

public interface AuthDAO {
    void createAuth(String authToken, String username) throws ResponseException, DataAccessException;

    void deleteAll() throws ResponseException, DataAccessException, SQLException;

    AuthData getAuthData(String authToken) throws ResponseException;

    void deleteAuth(String authToken) throws ResponseException, DataAccessException;


}
