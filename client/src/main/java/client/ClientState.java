package client;

public class ClientState {
    private static String authToken;
    private static String username;
    private static int currentGameID;

    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void setUsername(String name) {
        username = name;
    }

    public static String getUsername() {
        return username;
    }

    public static void setGameID(int gameId) {
        currentGameID = gameId;
    }

    public static int getGameID() {
        return currentGameID;
    }

    public static void clear() {
        authToken = null;
        username = null;
    }
}