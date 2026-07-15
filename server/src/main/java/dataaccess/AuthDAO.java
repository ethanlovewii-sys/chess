package dataaccess;

public interface AuthDAO {
    void createAuth(String authToken, String username);

    void deleteAll();
}
