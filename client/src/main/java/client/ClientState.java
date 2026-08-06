package client;

import chess.ChessBoard;
import chess.ChessGame;

public class ClientState {
    private static String authToken;
    private static String username;
    private static int currentGameID;
    private static ChessGame.TeamColor currentGameColor;
    private static ChessGame currentGame;
    private static boolean isObserver;

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

    public static void setGameColor(ChessGame.TeamColor color) {
        currentGameColor = color;
    }

    public static ChessGame.TeamColor getGameColor() {
        return currentGameColor;
    }

    public static void setCurrentGame(ChessGame game) {
        currentGame = game;
    }

    public static ChessGame getCurrentGame() {
        return currentGame;
    }

    public static void setIsObserver(boolean isAnObserver) {
        isObserver = isAnObserver;
    }

    public static boolean isObserver() {
        return isObserver;
    }

    public static void clear() {
        authToken = null;
        username = null;
    }
}