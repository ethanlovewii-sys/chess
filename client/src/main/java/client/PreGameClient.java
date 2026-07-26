package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import request.*;
import result.*;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PreGameClient {

    private static ServerFacade server = null;
    private static Map<Integer, GameData> gameNumbering = new HashMap<>();

    public PreGameClient(ServerFacade server) {
        PreGameClient.server = server;
    }

    public static ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            //Pulls the parameters away from the command
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> new ClientResult("quit", null);
                default -> help();
            };
        } catch (Exception ex) {
            return new ClientResult(ex.getMessage(), null);
        }
    }

    private static ClientResult logout() throws ResponseException {
        server.logout();
        return new ClientResult("Logout successful", "LoggedOut");
    }

    private static ClientResult createGame(String[] params) throws ResponseException {
        if (params.length < 1) {
            System.err.println("Must include the Name for your Game.");
        }
        CreateGameResult result = server.createGame(new CreateGameRequest(params[0]));
        return new ClientResult("Game: " + result.gameID() + " created", null);
    }

    private static ClientResult listGames() throws ResponseException {
        ListGamesResult result = server.listGames();
        if (result.games().isEmpty()) {
            return new ClientResult("No Games have been created. Use create <Game_Name> to create one", null);
        }
        String gameList = "";
        int counter = 1;
        gameNumbering = new HashMap<>();
        for (GameData game : result.games()){
            String whitePlayer = game.whiteUsername();
            String blackPlayer = game.blackUsername();
            if (game.whiteUsername() == null){
                whitePlayer = "awaiting player";
            }
            if (game.blackUsername() == null){
                blackPlayer = "awaiting player";
            }
            gameList += counter + " - " + game.gameName() + " - White: " + whitePlayer + " - Black: " + blackPlayer + "\n";

            gameNumbering.put(counter, game);

            counter++;
        }
        return new ClientResult(gameList, null);
    }

    private static ClientResult joinGame(String[] params) throws ResponseException {
        if (params.length < 2) {
            System.err.println("Must include the game number and what color you'd like to be.");
        }
        int gameNumber = Integer.parseInt(params[0]);
        int gameID = gameNumbering.get(gameNumber).gameID();

        ChessGame.TeamColor colorToJoin = null;
        if  (params[1].equals("white")) {
            colorToJoin = ChessGame.TeamColor.WHITE;
        } else if (params[1].equals("black")) {
            colorToJoin = ChessGame.TeamColor.BLACK;
        } else {
            System.err.println("Invalid game color. Must choose White or Black.");
        }

        server.joinGame(new JoinGameRequest(colorToJoin, gameID));
        return new ClientResult("Joined game number " + params[0], "InGame");
    }

    private static ClientResult observeGame(String[] params) {
        return null;
    }

    private static ClientResult help() {
        return new ClientResult("""
                logout
                create <NAME> - creates a game
                list - lists all games
                join <ID> [WHITE|BLACK] - joins a game
                observe <ID> - watch a game
                quit
                help - displays all possible commands
                """, null);
    }
}
