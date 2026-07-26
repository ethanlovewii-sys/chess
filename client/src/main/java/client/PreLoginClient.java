package client;

import request.*;
import result.*;
import server.ServerFacade;

import java.util.Arrays;

public class PreLoginClient {

    private static ServerFacade server = null;

    public PreLoginClient(ServerFacade server) {
        PreLoginClient.server = server;
    }

    public static ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            //Pulls the parameters away from the command
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> new ClientResult("quit", null);
                default -> help();
            };
        } catch (Exception ex) {
            return new ClientResult(ex.getMessage(), null);
        }
    }

    private static ClientResult help() {
        return new ClientResult("""
                register <USERNAME> <PASSWORD> <EMAIL>
                login <USERNAME> <PASSWORD>
                quit
                help - display possible commands
                """, null);
    }

    private static ClientResult register(String[] params) throws server.ResponseException {
        if (params.length < 3) {
            System.err.println("Must include Username, Password, and Email");
        }
        RegisterResult result = server.register(new RegisterRequest(params[0], params[1], params[2]));
        return new ClientResult("Registered new user:" + result.username(), "LoggedIn");
    }

    private static ClientResult login(String[] params) throws server.ResponseException {
        if (params.length < 2) {
            System.err.println("Must include Username, and Password");
        }
        LoginResult result = server.login(new LoginRequest(params[0], params[1]));
        return new ClientResult("Logged in user:" + result.username(), "LoggedIn");
    }
}
