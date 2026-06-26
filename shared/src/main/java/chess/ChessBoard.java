package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        Collection<ChessPiece.PieceType> specialPieces = List.of(
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK);

        ChessPosition blackSide = new ChessPosition(8, 1);
        for (ChessPiece.PieceType type : specialPieces) {
            addPiece(blackSide, new ChessPiece(ChessGame.TeamColor.BLACK, type));
            addPiece(new ChessPosition(7, blackSide.getColumn()), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            blackSide = new ChessPosition(8, blackSide.getColumn() + 1);
        }

        ChessPosition whiteSide = new ChessPosition(1, 1);
        for (ChessPiece.PieceType type : specialPieces) {
            addPiece(whiteSide, new ChessPiece(ChessGame.TeamColor.WHITE, type));
            addPiece(new ChessPosition(2, whiteSide.getColumn()), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            whiteSide = new ChessPosition(1, whiteSide.getColumn() + 1);
        }


    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int row = 8; row >= 1; row--) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece =
                        getPiece(new ChessPosition(row, col));

                if (piece == null) {
                    sb.append(". ");
                } else {
                    sb.append(piece.getPieceType().toString().charAt(0))
                            .append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}


