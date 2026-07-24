package client;

import java.util.Scanner;
import static ui.EscapeSequences.*;


public class Repl {
    private State state = State.LOGGED_OUT;
    private String displayStatus = "[LOGGED_OUT]";
    public Repl(String serverUrl) {

    }

    public void run() {
        System.out.println(" Welcome to the pet store. Sign in to start.");
//        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            //Print prompt
            System.out.print("\n" + displayStatus + " >>> ");
            String line = scanner.nextLine();
            try {
                switch (state){
                    case LOGGED_OUT -> result = PreLoginClient.eval(line);
                    case LOGGED_IN -> result = PreGameClient.eval(line);
                    case IN_GAME -> result = InGameClient.eval(line);
                }
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
}
