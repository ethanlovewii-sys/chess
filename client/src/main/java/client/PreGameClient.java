package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import model.GameData;
import request.*;
import result.*;
import exception.ResponseException;
import sharedserver.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.*;

public class PreGameClient {

    private static ServerFacade server = null;
    private static WebSocketFacade webSocket = null;
    private static Map<Integer, GameData> gameNumbering = new HashMap<>();
    private static String authToken;

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
                case "help" -> help();
                default ->
                        new ClientResult("Unrecognized Command: " + cmd + ". Type \"help\" for a list of available commands.", null);
            };
        } catch (Exception ex) {
            return new ClientResult(ex.getMessage(), null);
        }
    }

    private static ClientResult logout() throws ResponseException {
        server.logout();
        ClientState.clear();
        return new ClientResult("Logout successful", "LoggedOut");
    }

    private static ClientResult createGame(String[] params) throws ResponseException {
        if (params.length < 1) {
            return new ClientResult("Must include the Name for your Game.", null);
        }
        server.createGame(new CreateGameRequest(params[0]));
        return new ClientResult("Game: " + params[0] + " created", null);
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
            gameList += "\n" + counter + " - " + game.gameName() + " - White: " + whitePlayer + " - Black: " + blackPlayer;

            if (game.isGameOver()) {
                gameList += " - Game Over";
            }

            gameNumbering.put(counter, game);

            counter++;
        }
        return new ClientResult(gameList, null);
    }

    private static ClientResult joinGame(String[] params) throws Exception {
        if (params.length < 2) {
            return new ClientResult("Must include the game number and what color you'd like to be.", null);
        }
        int gameNumber = 0;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (Exception ex) {
            return new ClientResult("Use the numeric version to join a game, ex: 1 3 5 ect.", null);
        }
        if (!gameNumbering.containsKey(gameNumber)) {
            return new ClientResult("Invalid game number. Use 'list' to see available games.", null);
        }

        int gameID = gameNumbering.get(gameNumber).gameID();

        ChessGame.TeamColor colorToJoin;
        if (params[1].equals("white")) {
            colorToJoin = ChessGame.TeamColor.WHITE;
        } else if (params[1].equals("black")) {
            colorToJoin = ChessGame.TeamColor.BLACK;
        } else {
            return new ClientResult("Invalid game color. Must choose White or Black.", null);
        }

        server.joinGame(new JoinGameRequest(colorToJoin, gameID));

        webSocket = new WebSocketFacade();
        ClientState.setGameColor(colorToJoin);
        webSocket.connect(gameID);
        ClientState.setGameID(gameID);

        return new ClientResult("Joined game number " + params[0], "InGame");
    }


    private static ClientResult observeGame(String[] params) throws Exception {
        if (params.length < 1) {
            return new ClientResult("Must include the game number you want to observe.", null);
        }
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (Exception ex) {
            return new ClientResult("Use the numeric version to join a game, ex: 1 3 5 ect.", null);
        }
        if (!gameNumbering.containsKey(gameNumber)) {
            return new ClientResult("Invalid game number. Use 'list' to see available games.", null);
        }

        int gameID = gameNumbering.get(gameNumber).gameID();

        webSocket = new WebSocketFacade();
        ClientState.setGameColor(ChessGame.TeamColor.WHITE);
        webSocket.connect(gameID);
        ClientState.setGameID(gameID);
        ClientState.setIsObserver(true);

        return new ClientResult("Observing game " + params[0], "InGame");
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


