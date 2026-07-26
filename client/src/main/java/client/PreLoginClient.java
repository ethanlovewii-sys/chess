package client;

import request.*;
import result.*;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class PreLoginClient {
    private static ServerFacade server = null;

    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

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

    private static String register(String[] params) throws ResponseException {
        if (params.length < 3) {
            System.err.println("Must include Username, Password, and Email");
        }
        RegisterResult result = server.register(new RegisterRequest(params[0], params[1], params[2]));
        return "Registered new user:" + result.username();
    }

    private static String login(String[] params) throws ResponseException {
        if (params.length < 2) {
            System.err.println("Must include Username, and Password");
        }
        LoginResult result = server.login(new LoginRequest(params[0], params[1]));
        return "Logged in user:" + result.username();
    }
}
