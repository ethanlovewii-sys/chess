package client;

import result.ClientResult;

import java.util.Arrays;

public class InGameClient {
    public static ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            //Pulls the parameters away from the command
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> new ClientResult("quit", null);
                default -> help();
            };
        } catch (Exception ex) {
            return new ClientResult(ex.getMessage(), null);
        }
    }

    private static ClientResult help() {
        return new ClientResult("help", null);
    }
}
