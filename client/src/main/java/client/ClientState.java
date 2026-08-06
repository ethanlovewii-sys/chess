package client;

import chess.ChessBoard;
import chess.ChessGame;

public class ClientState {
    private static String authToken;
    private static String username;
    private static int currentGameID;
    private static ChessGame.TeamColor currentGameColor;
    private static ChessBoard currentBoard;

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

    public static void setCurrentBoard(ChessBoard board) {
        currentBoard = board;
    }
    public static ChessBoard getCurrentBoard() {
        return currentBoard;
    }

    public static void clear() {
        authToken = null;
        username = null;
    }
}