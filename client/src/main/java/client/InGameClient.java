package client;

import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import result.ClientResult;

import java.io.IOException;
import java.util.Arrays;

public class InGameClient {

    private static WebSocketFacade webSocket = null;

    public InGameClient(WebSocketFacade webSocket) {
        this.webSocket = webSocket;
    }

    public static ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            //Pulls the parameters away from the command
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> new ClientResult("quit", null);
                case "move" -> makeMove(params);
                default -> help();
            };
        } catch (Exception ex) {
            return new ClientResult(ex.getMessage(), null);
        }
    }

    private static ClientResult makeMove(String[] params) throws IOException {
        if (params.length != 2) {
            return new ClientResult("Must only include a start and end position for your move. Ex: A5 D8", null);
        }

        if (params[0].length() != 2 || params[1].length() != 2) {
            return new ClientResult("Start and End positions should be two characters long. Ex: A2 D2", null);
        }

        String inputStart = params[0].toLowerCase();
        String inputEnd = params[1].toLowerCase();

        char startCol = inputStart.charAt(0);
        char endCol = inputEnd.charAt(0);

        char startRowChar = inputStart.charAt(1);
        char endRowChar = inputEnd.charAt(1);

        if (startCol < 'a' || startCol > 'h' ||
                !Character.isDigit(startRowChar) ||
                startRowChar < '1' || startRowChar > '8') {
            return new ClientResult("Invalid start position. Use A1 through H8.", null);
        }

        //Convert to int
        int startColInt = startCol - 'a' + 1;
        int endColInt = endCol - 'a' + 1;

        if (!Character.isDigit(startRowChar) || !Character.isDigit(endRowChar)) {
            return new ClientResult("Start and End positions must end in a number. Ex: A2 A3", null);
        }

        int startRow = Character.getNumericValue(startRowChar);
        int endRow = Character.getNumericValue(endRowChar);

        ChessPosition start;
        ChessPosition end;
        try {
            start = new ChessPosition(startRow, startColInt);
            end = new ChessPosition(endRow, endColInt);
        } catch (Exception e) {
            return new ClientResult("Must use a positions found on the board. Reference the coordinates found on the border.", null);
        }

        ChessMove move = new ChessMove(start, end, null);

        try{
            webSocket.makeMove(ClientState.getGameID(), move);
        } catch (Exception e){
            return new ClientResult(e.getMessage(), null);
        }

        return new ClientResult("", null);
    }

    private static ClientResult help() {
        return new ClientResult("move start end. ex: A1 B4", null);
    }
}
