package client;

import result.ClientResult;

import java.util.Scanner;

import static ui.EscapeSequences.*;


public class Repl {

    String state = "LoggedOut";
    private final PreLoginClient preLoginClient;
    private final PreGameClient preGameClient;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        preGameClient = new PreGameClient(serverUrl);
    }

    public void run() {
        System.out.println(" Welcome to Chess. Sign in to start.");

        Scanner scanner = new Scanner(System.in);
        ClientResult result = null;
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
                System.out.print(result.message());
                if (!result.nextState().equals(null)) {
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
