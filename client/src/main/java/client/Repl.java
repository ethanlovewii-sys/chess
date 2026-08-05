package client;

import result.ClientResult;
import sharedserver.ServerFacade;

import java.util.Scanner;


public class Repl {

    public String state = "LoggedOut";
    private final PreLoginClient preLoginClient;
    private final PreGameClient preGameClient;
    private ServerFacade server;

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
        preLoginClient = new PreLoginClient(server);
        preGameClient = new PreGameClient(server);
    }

    public void run() {
        System.out.println(" Welcome to Chess. use 'help' for a list of commands.");
        Scanner scanner = new Scanner(System.in);
        ClientResult result = new ClientResult("", null);
        while (!result.message().equals("quit")) {
            //Print prompt
            System.out.print("\n" + state + " >>> ");
            String line = scanner.nextLine();
            try {
                switch (state) {
                    case "LoggedOut" -> result = preLoginClient.eval(line);
                    case "LoggedIn" -> result = preGameClient.eval(line);
                    case "InGame" -> result = InGameClient.eval(line);
                }
                System.out.print("\n" + result.message());
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
}
