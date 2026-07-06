package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;
    ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = gameBoard.getPiece(startPosition);
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> moves = ChessPiece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> checkedMoves = new ArrayList<>();

        //Loop through moves and if after the move the team is not in check, add that move to valid moves.
        gameBoard.addPiece(startPosition, null);
        for (ChessMove move : moves){
            ChessPosition end = move.getEndPosition();
            ChessPiece tempSavedPiece = gameBoard.getPiece(end);
            gameBoard.addPiece(end, piece);
            if (!isInCheck(color)){
                checkedMoves.add(move);
            }
            gameBoard.addPiece(end, tempSavedPiece);
        }
        gameBoard.addPiece(startPosition, piece);
        return checkedMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //Get the team color kings position
        ChessPosition kingPosition = null;
        for (int a = 1; a <= 8; a++) {
            for (int b = 1; b <= 8; b++) {
                ChessPosition checkPosition = new ChessPosition(a, b);
                ChessPiece possibleKing = gameBoard.getPiece(checkPosition);
                if (possibleKing != null &&
                        possibleKing.getPieceType() == ChessPiece.PieceType.KING &&
                        possibleKing.getTeamColor() == teamColor) {
                    kingPosition = checkPosition;
                    break;
                }
            }
            if (kingPosition != null) {
                break;
            }
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition positionToCheck = new ChessPosition(i, j);
                ChessPiece pieceToCheck = gameBoard.getPiece(positionToCheck);
                if (pieceToCheck != null && pieceToCheck.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = ChessPiece.pieceMoves(gameBoard, positionToCheck);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }
}
