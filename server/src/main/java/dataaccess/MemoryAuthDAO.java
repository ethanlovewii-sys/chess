package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> auths = new HashMap<>();

    public void createAuth(String authToken, String username) {
        auths.put(authToken, new AuthData(authToken, username));
    }

    public void deleteAll() {
        auths.clear();
    }

    public AuthData getAuthData(String authToken) {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }
}
