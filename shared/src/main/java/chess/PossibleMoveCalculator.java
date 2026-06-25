package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PossibleMoveCalculator {

    public static Collection<ChessMove> pieceMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        return switch (piece.getPieceType()) {
            case KING -> kingMoves(board, position);
            case QUEEN -> queenMoves(board, position);
            case KNIGHT -> knightMoves(board, position);
            case ROOK -> rookMoves(board, position);
            case BISHOP -> bishopMoves(board, position);
            case PAWN -> pawnMoves(board, position);
        };
    }

    //Checks if a position is on the board and not taken by another piece.
    private static boolean isValidPosition(ChessPosition originalPosition, ChessPosition newPosition, ChessBoard board) {
        if (newPosition.getRow() < 1 || newPosition.getColumn() < 1 || newPosition.getRow() > 8 || newPosition.getColumn() > 8) {
            return false;
        } else if (board.getPiece(newPosition) != null) {
            if (board.getPiece(originalPosition).getTeamColor() == board.getPiece(newPosition).getTeamColor()) {
                return false;
            }
        }
        return true;
    }

    private static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition position) {
        //Initializing local variables
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition frontSpace;
        ChessPosition doubleFront = null;
        ChessPosition diagonalRight = null;
        ChessPosition diagonalLeft = null;
        String teamColor = board.getPiece(position).getTeamColor().toString();
        int row = position.getRow();
        int column = position.getColumn();

        //Set three positions based on color. Checking for left and right in bounds and if it can move two.
        if (teamColor.equals("WHITE")) {
             if (row == 2){
                doubleFront = new ChessPosition(row + 2 , column);
             }
             frontSpace = new ChessPosition(row + 1, column);
             if (column < 8 ) {
                 diagonalRight = new ChessPosition(row + 1, column + 1);
             }
             if (column > 1) {
                 diagonalLeft = new ChessPosition(row+ 1, column - 1);
             }
        }
        else {
            if (row == 7){
                doubleFront = new ChessPosition(row - 2 , column);
            }
            frontSpace = new ChessPosition(row - 1, column);
            if (column < 8) {
                 diagonalRight = new ChessPosition(row - 1, column + 1);
             }
             if (column > 1) {
                 diagonalLeft = new ChessPosition(row - 1, column - 1);
             }
        }

        //Add set positions if the moves are valid
        if (board.getPiece(frontSpace) == null) {
            if (frontSpace.getRow() == 8 || frontSpace.getRow() == 1) {
                moves.add(new ChessMove(position, frontSpace, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, frontSpace, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, frontSpace, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, frontSpace, ChessPiece.PieceType.KNIGHT));
            }
            else {
                moves.add(new ChessMove(position, frontSpace, null));
            }
            if (doubleFront != null) {
                if (board.getPiece(doubleFront) == null) {
                    moves.add(new ChessMove(position, doubleFront, null));
                }
            }
        }
        if (diagonalRight != null) {
            if (board.getPiece(diagonalRight) != null && board.getPiece(diagonalRight).getTeamColor() != board.getPiece(position).getTeamColor()) {
                if (diagonalRight.getRow() == 8 || diagonalRight.getRow() == 1) {
                    moves.add(new ChessMove(position, diagonalRight, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(position, diagonalRight, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(position, diagonalRight, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, diagonalRight, ChessPiece.PieceType.KNIGHT));
                }
                else {
                    moves.add(new ChessMove(position, diagonalRight, null));
                }
            }
        }
        if (diagonalLeft != null) {
            if (board.getPiece(diagonalLeft) != null && board.getPiece(diagonalLeft).getTeamColor() != board.getPiece(position).getTeamColor()) {
                if (diagonalLeft.getRow() == 8 || diagonalLeft.getRow() == 1) {
                    moves.add(new ChessMove(position, diagonalLeft, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(position, diagonalLeft, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(position, diagonalLeft, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, diagonalLeft, ChessPiece.PieceType.KNIGHT));
                }
                else {
                    moves.add(new ChessMove(position, diagonalLeft, null));
                }
            }
        }
        return moves;
    }

    private static Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] possibleDirections = new int[][]{{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        for (int[] move : possibleDirections) {
            moves.addAll(straitLineMoves(board, position, move[0], move[1]));
        }
        return moves;
    }

    private static Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] possibleDirections = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int[] move : possibleDirections) {
            moves.addAll(straitLineMoves(board, position, move[0], move[1]));
        }
        return moves;
    }

    private static Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] possibleMoves = new int[][]{{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
        for (int[] move : possibleMoves) {
            ChessPosition newPosition = new ChessPosition(position.getRow() + move[0], position.getColumn() + move[1]);
            if (isValidPosition(position, newPosition, board)) {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
        return moves;
    }

    private static Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] possibleDirections = new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        for (int[] move : possibleDirections) {
            moves.addAll(straitLineMoves(board, position, move[0], move[1]));
        }
        return moves;
    }

    //Loops through the 8 possible directions and adds them if they are valid.
    private static Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(position.getRow() + direction[0], position.getColumn() + direction[1]);
            if (isValidPosition(position, newPosition, board)) {
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
        return moves;
    }

    //Will use the given direction in row and column changes and check that direction until a spot is not valid.
    private static Collection<ChessMove> straitLineMoves(ChessBoard board, ChessPosition originalPosition, int rowChange, int colChange) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition position = new ChessPosition(originalPosition.getRow(), originalPosition.getColumn());
        while (true) {
            ChessPosition newPosition = new ChessPosition(position.getRow() + rowChange, position.getColumn() + colChange);
            if (isValidPosition(originalPosition, newPosition, board)) {
                ChessMove validMove = new ChessMove(originalPosition, newPosition, null);
                moves.add(validMove);
                if (board.getPiece(newPosition) != null) {
                    break;
                }
            } else {
                break;
            }
            position = new ChessPosition(position.getRow() + rowChange, position.getColumn() + colChange);
        }
        return moves;
    }
}
