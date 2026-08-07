package client.websocket;

import chess.*;
import client.ClientState;
import com.google.gson.Gson;
import jakarta.websocket.*;
import model.AuthData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import client.Repl;
import websocket.messages.*;

import static ui.EscapeSequences.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint {
    public Session session;
    private final Gson gson = new Gson();
    private Timer promptTimer = new Timer(true);

    public WebSocketFacade() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String json) {
                System.out.println("\n");
                ServerMessage serverMessage = gson.fromJson(json, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> {
                        LoadGameMessage gameMessage = gson.fromJson(json, LoadGameMessage.class);
                        System.out.println(parseChessBoard(ClientState.getGameColor(), gameMessage.getGame().getBoard(), null, null));
                        ClientState.setCurrentGame(gameMessage.getGame());
                    }
                    case NOTIFICATION -> {
                        NotificationMessage notification = gson.fromJson(json, NotificationMessage.class);
                        System.out.println(notification.getMessage());
                    }
                    case ERROR -> {
                        ErrorMessage error = gson.fromJson(json, ErrorMessage.class);
                        System.out.println(SET_TEXT_COLOR_RED + error.getMessage() + RESET_TEXT_COLOR);
                    }
                }
                schedulePrompt();
                ;
            }
        });
    }

    // This method must be overridden, but we don't have to do anything with it
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(CONNECT, ClientState.getAuthToken(), gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void makeMove(int gameID, ChessMove move) throws IOException {
        MakeMoveCommand command = new MakeMoveCommand(ClientState.getAuthToken(), gameID, move);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void resign(int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(RESIGN, ClientState.getAuthToken(), gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void leave(int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(LEAVE, ClientState.getAuthToken(), gameID);
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    private static String parseChessBoard(
            ChessGame.TeamColor colorPerspective, ChessBoard board, Collection<String> highlights, String highlightPiece
    ) {
        StringBuilder stringBoard = new StringBuilder();

        stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY);

        if (colorPerspective == ChessGame.TeamColor.WHITE) {
            for (char letter = 'a'; letter <= 'h'; letter++) {
                stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append("\u2003").append(letter).append(" ");
            }
        } else {
            for (char letter = 'h'; letter >= 'a'; letter--) {
                stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append("\u2003").append(letter).append(" ");
            }
        }

        stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY);
        stringBoard.append(RESET_BG_COLOR).append("\n");

        for (int row = 1; row <= 8; row++) {

            int boardRow = colorPerspective == ChessGame.TeamColor.BLACK ? row : 9 - row;
            stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append("\u2003").append(boardRow).append(" ");

            for (int col = 1; col <= 8; col++) {
                int boardCol = colorPerspective == ChessGame.TeamColor.BLACK ? 9 - col : col;

                String coordinate = "" + boardRow + boardCol;
                if (coordinate.equals(highlightPiece)) {
                    stringBoard.append(SET_BG_COLOR_SOFT_YELLOW);
                } else if ((row + col) % 2 == 0) {
                    if (highlights != null && highlights.contains(coordinate)) {
                        stringBoard.append(SET_BG_COLOR_SOFT_GREEN);
                    } else {
                        stringBoard.append(SET_BG_COLOR_WHITE);
                    }
                } else {
                    if (highlights != null && highlights.contains(coordinate)) {
                        stringBoard.append(SET_BG_COLOR_DARK_GREEN);
                    } else {
                        stringBoard.append(SET_BG_COLOR_BLACK);
                    }
                }

                ChessPiece piece = board.getPiece(new ChessPosition(boardRow, boardCol));
                stringBoard.append(symbol(piece)).append(RESET_TEXT_COLOR);
                if (col == 8) {
                    stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(boardRow).append("\u2003");
                    stringBoard.append(RESET_BG_COLOR).append("\n");
                }
            }
        }

        stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY);
        if (colorPerspective == ChessGame.TeamColor.WHITE) {
            for (char letter = 'a'; letter <= 'h'; letter++) {
                stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append("\u2003").append(letter).append(" ");
            }
        } else {
            for (char letter = 'h'; letter >= 'a'; letter--) {
                stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append("\u2003").append(letter).append(" ");
            }
        }
        stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY);
        stringBoard.append(RESET_BG_COLOR).append("\n");

        return stringBoard.toString();
    }

    private static String symbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        return switch (piece.getPieceType()) {
            case PAWN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_BLUE + BLACK_PAWN : SET_TEXT_COLOR_RED + BLACK_PAWN;
            case KNIGHT ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_BLUE + BLACK_KNIGHT : SET_TEXT_COLOR_RED + BLACK_KNIGHT;
            case BISHOP ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_BLUE + BLACK_BISHOP : SET_TEXT_COLOR_RED + BLACK_BISHOP;
            case ROOK ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_BLUE + BLACK_ROOK : SET_TEXT_COLOR_RED + BLACK_ROOK;
            case QUEEN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_BLUE + BLACK_QUEEN : SET_TEXT_COLOR_RED + BLACK_QUEEN;
            case KING ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_BLUE + BLACK_KING : SET_TEXT_COLOR_RED + BLACK_KING;
        };
    }

    private void schedulePrompt() {
        promptTimer.cancel();
        promptTimer = new Timer(true);

        promptTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.print(Repl.getState() + " >>> ");
            }
        }, 250);
    }

    public void redraw() {
        System.out.println(parseChessBoard(ClientState.getGameColor(), ClientState.getCurrentGame().getBoard(), null, null));
    }

    public void highlightMoves(ChessPosition chessposition) {
        String pieceCoordinate = "" + chessposition.getRow() + chessposition.getColumn();
        Collection<String> highlightCoordinates = new ArrayList<>(List.of());
        Collection<ChessMove> moves = ClientState.getCurrentGame().validMoves(chessposition);
        for (ChessMove move : moves) {
            int row = move.getEndPosition().getRow();
            int col = move.getEndPosition().getColumn();
            highlightCoordinates.add("" + row + col);
        }
        System.out.println(
                parseChessBoard(ClientState.getGameColor(), ClientState.getCurrentGame().getBoard(), highlightCoordinates, pieceCoordinate)
        );
    }
}
