package client;

import client.websocket.WebSocketFacade;
import result.ClientResult;
import sharedserver.ServerFacade;

import java.util.Scanner;


public class Repl {

    public static String state = "LoggedOut";
    private final PreLoginClient preLoginClient;
    private final PreGameClient preGameClient;
    private final InGameClient inGameClient;
    private ServerFacade server;
    private WebSocketFacade webSocket;

    public Repl(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        webSocket = new WebSocketFacade();
        preLoginClient = new PreLoginClient(server);
        preGameClient = new PreGameClient(server);
        inGameClient = new InGameClient(webSocket);
    }

    public void run() {
        System.out.println("Welcome to Chess. use 'help' for a list of commands.");
        Scanner scanner = new Scanner(System.in);
        ClientResult result = new ClientResult("", null);
        while (!result.message().equals("quit")) {
            //Print prompt
            System.out.print("\n\n" + state + " >>> ");
            String line = scanner.nextLine();
            try {
                switch (state) {
                    case "LoggedOut" -> result = preLoginClient.eval(line);
                    case "LoggedIn" -> result = preGameClient.eval(line);
                    case "InGame" -> result = inGameClient.eval(line);
                }
                System.out.print(result.message());
                if (!(result.nextState() == null)) {
                    state = result.nextState();
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public static String getState() {
        return state;
    }

    public void printPrompt() {
        System.out.print("\n" + state + " >>> ");
    }
}
