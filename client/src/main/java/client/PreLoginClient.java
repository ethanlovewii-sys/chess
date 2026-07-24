package client;

import java.util.Arrays;

public class PreLoginClient {
    public static String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            //Pulls the parameters away from the command
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private static String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL>
                login <USERNAME> <PASSWORD>
                quit
                help - display possible commands
                """;
    }

    private static String register(String[] params) {
        return null;
    }

    private static String login(String[] params) {
        return null;
    }
}
