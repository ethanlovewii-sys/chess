package client;

import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import result.ClientResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

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
                case "resign" -> resign();
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "highlight" -> highlightMoves(params);
                default -> help();
            };
        } catch (Exception ex) {
            return new ClientResult(ex.getMessage(), null);
        }
    }

    private static ClientResult highlightMoves(String[] params) {
        if (params.length == 0) {
            return new ClientResult("Must include the position of the piece. EX: A3", null);
        }

        if (params[0].length() != 2) {
            return new ClientResult("Positions should be two characters long. Ex: A2", null);
        }

        String position = params[0];
        char col = position.charAt(0);
        char row = position.charAt(1);
        if (col < 'a' || col > 'h' ||
                !Character.isDigit(row) ||
                row < '1' || row > '8') {
            return new ClientResult("Invalid position. Use A1 through H8.", null);
        }

        int colInt = col - 'a' + 1;
        int rowInt = Character.getNumericValue(row);

        ChessPosition chessposition;
        try {
            chessposition = new ChessPosition(rowInt, colInt);
        } catch (Exception e) {
            return new ClientResult("Must use a positions found on the board. Reference the coordinates found on the border.", null);
        }

        webSocket.highlightMoves(chessposition);
        return new ClientResult("", null);
    }

    private static ClientResult redraw() {
        webSocket.redraw();
        return new ClientResult("", null);
    }

    private static ClientResult leave() throws IOException {
        webSocket.leave(ClientState.getGameID());
        return new ClientResult("You have left the game.", "LoggedIn");
    }

    private static ClientResult resign() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nType 'yes' to confirm you want to resign.\n>>> ");
        String line = scanner.nextLine();
        if (!line.equalsIgnoreCase("yes")) {
            return new ClientResult("Continue playing, good luck!", null);
        }
        webSocket.resign(ClientState.getGameID());
        return new ClientResult("You reigned and have thus lost! Use the command 'leave' to leave the game.", null);
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
        if (endCol < 'a' || endCol > 'h' ||
                !Character.isDigit(startRowChar) ||
                endRowChar < '1' || endRowChar > '8') {
            return new ClientResult("Invalid end position. Use A1 through H8.", null);
        }

        //Convert to int
        int startColInt = startCol - 'a' + 1;
        int endColInt = endCol - 'a' + 1;

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
        return new ClientResult("""
                move {start} {end} - Make a move using the coordinate system. Ex: A2 D4
                redraw - Reload the current board.
                leave - Leave the game. Another user will be able to take your spot.
                resign - Forfeit the game.
                highlight {positon} - Shows legal moves for the piece at the given positon.
                help - For a list of commands.
                """, null);
    }
}
