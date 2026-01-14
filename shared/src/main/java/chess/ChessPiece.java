package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        int startCol = myPosition.getColumn();
        int startRow = myPosition.getRow();
        ChessPosition start = new ChessPosition(startRow, startCol);
        List <ChessMove> moves = new ArrayList<>();
        if (piece.getPieceType() == PieceType.BISHOP){
            int [][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int[] d : directions){
                int rowDestin = d[0] + startRow;
                int colDestin = d[1] + startCol;
                while (ChessBoard.inBounds(rowDestin, colDestin)){
                    ChessPosition destin = new ChessPosition(rowDestin, colDestin);
                    ChessPiece destinPiece = board.getPiece(destin);
                    if (destinPiece == null){
                        moves.add(new ChessMove(start, destin, null));
                    }
                    else {
                        if (destinPiece.getTeamColor() != piece.pieceColor){
                            moves.add(new ChessMove(start, destin, null));
                        }
                        break;
                    }
                    rowDestin += d[0];
                    colDestin += d[1];
                }
            }
            //return List.of(new ChessMove(new ChessPosition(5, 4), new ChessPosition(1,8), null));
        }
        else if (piece.getPieceType() == PieceType.ROOK){
            int [][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
            for (int[] d : directions){
                int rowDestin = d[0] + startRow;
                int colDestin = d[1] + startCol;
                while (ChessBoard.inBounds(rowDestin, colDestin)){
                    ChessPosition destin = new ChessPosition(rowDestin, colDestin);
                    ChessPiece destinPiece = board.getPiece(destin);
                    if (destinPiece == null){
                        moves.add(new ChessMove(start, destin, null));
                    }
                    else {
                        if (destinPiece.getTeamColor() != piece.pieceColor){
                            moves.add(new ChessMove(start, destin, null));
                        }
                        break;
                    }
                    rowDestin += d[0];
                    colDestin += d[1];
                }
            }
        }
        else if (piece.getPieceType() == PieceType.KNIGHT){
            int[][] directions = {{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
            for (int[] d : directions){
                int rowDestin = startRow + d[0];
                int colDestin = startCol + d[1];
                if (ChessBoard.inBounds(rowDestin, colDestin)){
                    ChessPosition destin = new ChessPosition(rowDestin, colDestin);
                    ChessPiece destinPiece = board.getPiece(destin);
                    if (destinPiece == null){
                        moves.add(new ChessMove(start, destin, null));
                    }
                    else {
                        if (destinPiece.pieceColor != piece.pieceColor){
                            moves.add(new ChessMove(start, destin, null));
                        }
                        continue;
                    }
                }
            }
        }
        else if (piece.getPieceType() == PieceType.QUEEN){
            int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1,-1}, {0, -1}, {1, -1}};
            for (int[] d : directions){
                int rowDestin = d[0] + startRow;
                int colDestin = d[1] + startCol;
                while (ChessBoard.inBounds(rowDestin, colDestin)){
                    ChessPosition destin = new ChessPosition(rowDestin, colDestin);
                    ChessPiece destinPiece = board.getPiece(destin);
                    if (destinPiece == null){
                        moves.add(new ChessMove(start, destin, null));
                    }
                    else {
                        if (destinPiece.getTeamColor() != piece.getTeamColor()){
                            moves.add(new ChessMove(start, destin, null));
                        }
                        break;
                    }
                    rowDestin += d[0];
                    colDestin += d[1];
                }

            }
        }
        else if (piece.getPieceType() == PieceType.KING){
            ///
        }
        else if (piece.getPieceType() == PieceType.PAWN){
            ///
        }
        return moves;
    }

}
