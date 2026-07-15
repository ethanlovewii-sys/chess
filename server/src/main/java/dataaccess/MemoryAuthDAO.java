package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO{
    final private ArrayList<AuthData> auths = new ArrayList<>();

    public void createAuth(String authToken, String username) {
        auths.add(new AuthData(authToken, username));
    }
}
