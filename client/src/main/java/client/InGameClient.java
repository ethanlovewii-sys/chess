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
        String inputStart = params[0].toLowerCase();
        String inputEnd = params[1].toLowerCase();

        char startCol = inputStart.charAt(0);
        int startColInt = startCol - 'a' + 1;
        int startRow = Character.getNumericValue(inputStart.charAt(1));

        char endCol = inputEnd.charAt(0);
        int endColInt = endCol - 'a' + 1;
        int endRow = Character.getNumericValue(inputEnd.charAt(1));

        ChessPosition start = new ChessPosition(startRow, startColInt);
        ChessPosition end = new ChessPosition(endRow, endColInt);

        ChessMove move = new ChessMove(start, end, null);

        webSocket.makeMove(ClientState.getGameID(), move);

        return new ClientResult("", null);
    }

    private static ClientResult help() {
        return new ClientResult("move start end. ex: A1 B4", null);
    }
}
