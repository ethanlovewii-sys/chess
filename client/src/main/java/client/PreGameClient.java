package client;

import model.GameData;
import request.*;
import result.*;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class PreGameClient {

    private static ServerFacade server = null;

    public PreGameClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
                case "join" -> joinGame();
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
        String gameList = "";
        int counter = 1;
        for (GameData game : result.games()){
            gameList += counter + " - " + game.gameName() + " - White: " + game.whiteUsername() + " - Black: " + game.blackUsername() + "\n";
            counter++;
        }
        return new ClientResult(gameList, null);
    }

    private static ClientResult joinGame() {
        return null;
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
