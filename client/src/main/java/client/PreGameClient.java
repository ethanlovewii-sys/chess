package client;

import java.util.Arrays;

public class PreGameClient {
    public static String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            //Pulls the parameters away from the command
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame();
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private static String logout(String[] params) {
        return null;
    }

    private static String createGame(String[] params) {
        return null;
    }

    private static String listGames() {
        return null;
    }

    private static String joinGame() {
        return null;
    }

    private static String observeGame(String[] params) {
        return null;
    }

    private static String help() {
        return """
                logout
                create <NAME> - creates a game
                list - lists all games
                join <ID> [WHITE|BLACK] - joins a game
                observe <ID> - watch a game
                quit
                help - displays all possible commands
                """;
    }
}
