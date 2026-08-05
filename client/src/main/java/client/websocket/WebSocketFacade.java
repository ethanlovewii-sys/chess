package client.websocket;

import chess.*;
import client.ClientState;
import com.google.gson.Gson;
import jakarta.websocket.*;
import model.AuthData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import client.Repl;
import websocket.messages.*;
import static ui.EscapeSequences.*;

import java.io.IOException;
import java.net.URI;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint {
    public Session session;
    public ChessGame.TeamColor colorPerspective;
    private final Gson gson = new Gson();

    public WebSocketFacade() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String json) {
                System.out.println("\n");
                ServerMessage serverMessage = gson.fromJson(json, ServerMessage.class);
                switch (serverMessage.getServerMessageType()){
                    case LOAD_GAME -> {
                        LoadGameMessage gameMessage = gson.fromJson(json, LoadGameMessage.class);
                        System.out.println(parseChessBoard(colorPerspective, gameMessage.getGame().getBoard()));
                    }
                    case NOTIFICATION -> {
                        NotificationMessage notification = gson.fromJson(json, NotificationMessage.class);
                        System.out.println(notification.getMessage());
                    }
                    case ERROR -> {
                        ErrorMessage error = gson.fromJson(json, ErrorMessage.class);
                        System.out.println(error.getMessage() + SET_TEXT_COLOR_RED);
                    }
                }
                System.out.print(Repl.getState() + " >>> ");
            ;}
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
        UserGameCommand command = new UserGameCommand(MAKE_MOVE, ClientState.getAuthToken(), gameID);
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

    private static String parseChessBoard(ChessGame.TeamColor colorPerspective, ChessBoard board) {
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

                if ((row + col) % 2 == 0) {
                    stringBoard.append(SET_BG_COLOR_WHITE);
                } else {
                    stringBoard.append(SET_BG_COLOR_BLACK);
                }

                int boardCol = colorPerspective == ChessGame.TeamColor.BLACK ? 9 - col : col;

                ChessPiece piece = board.getPiece(new ChessPosition(boardRow, boardCol));
                stringBoard.append(symbol(piece));
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
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
        };
    }

    public void setColorPerspective(ChessGame.TeamColor color) {
        colorPerspective = color;
    }

}
