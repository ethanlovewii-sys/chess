package dataaccess;

import model.UserData;
import server.ResponseException;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException, ResponseException;

    void createUser(String username, String password, String email) throws ResponseException, DataAccessException;

    void deleteAll() throws ResponseException, DataAccessException;
}
