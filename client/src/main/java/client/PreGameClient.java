package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import request.*;
import result.*;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.*;

public class PreGameClient {

    private static ServerFacade server = null;
    private static Map<Integer, GameData> gameNumbering = new HashMap<>();

    public PreGameClient(ServerFacade server) {
        PreGameClient.server = server;
    }

    public static ClientResult eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            //Pulls the parameters away from the command
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> new ClientResult("quit", null);
                default -> help();
            };
        } catch (Exception ex) {
            return new ClientResult(ex.getMessage(), null);
        }
    }

    private static ClientResult logout() throws ResponseException {
        server.logout();
        return new ClientResult("Logout successful", "LoggedOut");
    }

    private static ClientResult createGame(String[] params) throws ResponseException {
        if (params.length < 1) {
            System.err.println("Must include the Name for your Game.");
        }
        CreateGameResult result = server.createGame(new CreateGameRequest(params[0]));
        return new ClientResult("Game: " + result.gameID() + " created", null);
    }

    private static ClientResult listGames() throws ResponseException {
        ListGamesResult result = server.listGames();
        if (result.games().isEmpty()) {
            return new ClientResult("No Games have been created. Use create <Game_Name> to create one", null);
        }
        String gameList = "";
        int counter = 1;
        gameNumbering = new HashMap<>();
        for (GameData game : result.games()) {
            String whitePlayer = game.whiteUsername();
            String blackPlayer = game.blackUsername();
            if (game.whiteUsername() == null) {
                whitePlayer = "awaiting player";
            }
            if (game.blackUsername() == null) {
                blackPlayer = "awaiting player";
            }
            gameList += counter + " - " + game.gameName() + " - White: " + whitePlayer + " - Black: " + blackPlayer + "\n";

            gameNumbering.put(counter, game);

            counter++;
        }
        return new ClientResult(gameList, null);
    }

    private static ClientResult joinGame(String[] params) throws ResponseException {
        if (params.length < 2) {
            System.err.println("Must include the game number and what color you'd like to be.");
        }
        int gameNumber = Integer.parseInt(params[0]);
        int gameID = gameNumbering.get(gameNumber).gameID();

        ChessGame.TeamColor colorToJoin = null;
        if (params[1].equals("white")) {
            colorToJoin = ChessGame.TeamColor.WHITE;
        } else if (params[1].equals("black")) {
            colorToJoin = ChessGame.TeamColor.BLACK;
        } else {
            System.err.println("Invalid game color. Must choose White or Black.");
        }

        server.joinGame(new JoinGameRequest(colorToJoin, gameID));

        System.out.print(assembleInitialBoard(colorToJoin));

        return new ClientResult("Joined game number " + params[0], null);
    }

    private static String assembleInitialBoard(ChessGame.TeamColor colorToJoin) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        StringBuilder stringBoard = new StringBuilder();

        stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY);

        if (colorToJoin == ChessGame.TeamColor.WHITE) {
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

            int boardRow = colorToJoin == ChessGame.TeamColor.BLACK ? row : 9 - row;
            stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append("\u2003").append(boardRow).append(" ");

            for (int col = 1; col <= 8; col++) {

                if ((row + col) % 2 == 0) {
                    stringBoard.append(SET_BG_COLOR_WHITE);
                } else {
                    stringBoard.append(SET_BG_COLOR_BLACK);
                }

                int boardCol = colorToJoin == ChessGame.TeamColor.BLACK ? 9 - col : col;

                ChessPiece piece = board.getPiece(new ChessPosition(boardRow, boardCol));
                stringBoard.append(symbol(piece));
                if (col == 8) {
                    stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(boardRow).append("\u2003");
                    stringBoard.append(RESET_BG_COLOR).append("\n");
                }
            }
        }

        stringBoard.append(SET_BG_COLOR_LIGHT_GREY).append(EMPTY);
        if (colorToJoin == ChessGame.TeamColor.WHITE) {
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

    private static ClientResult observeGame(String[] params) {
        if (params.length < 1) {
            System.err.println("Must include the game number you want to observe.");
        }
        int gameNumber = Integer.parseInt(params[0]);
        int gameID = gameNumbering.get(gameNumber).gameID();
        System.out.print(assembleInitialBoard(ChessGame.TeamColor.WHITE));
        return new ClientResult("Observing game " + params[0] + ", With the GameID of " + gameID, null);
    }

    private static ClientResult help() {
        return new ClientResult("""
                logout
                create <NAME> - creates a game
                list - lists all games
                join <ID> [WHITE|BLACK] - joins a game
                observe <ID> - watch a game
                quit
                help - displays all possible commands
                """, null);
    }
}


