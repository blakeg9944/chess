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

    @Override
    public String toString() {
        return String.format("%s, %s", pieceColor, type);
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

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 2}, {1, -2},{2, 1}, {2, -1}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        for (int [] d : directions){
            int destinRow = startRow + d[0];
            int destinCol = startCol + d[1];
            if (ChessBoard.inBounds(destinRow, destinCol)){
                ChessPosition destin = new ChessPosition(destinRow, destinCol);
                ChessPiece destinPiece = board.getPiece(destin);
                if (destinPiece == null){
                    moves.add(new ChessMove(myPosition, destin, null));
                }
                else{
                    if (piece.pieceColor != destinPiece.pieceColor){
                        moves.add(new ChessMove(myPosition, destin, null));
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        int [][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int [] d : directions){
            int destinRow = d[0] + startRow;
            int destinCol = d[1] + startCol;
            if (ChessBoard.inBounds(destinRow, destinCol)){
                ChessPosition destin = new ChessPosition(destinRow, destinCol);
                ChessPiece destinPiece= board.getPiece(destin);
                if (destinPiece == null){
                    moves.add(new ChessMove(myPosition, destin, null));
                }
                else{
                    if (piece.pieceColor !=  destinPiece.getTeamColor()){
                        moves.add(new ChessMove(myPosition, destin, null));
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition start) {
        List<ChessMove> moves = new ArrayList<>();

        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRank = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int finalRank = (pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int row = start.getRow();
        int col = start.getColumn();

        int oneForward = row + direction;
        ChessPosition oneStep = new ChessPosition(oneForward, col);

        if (ChessBoard.inBounds(oneForward, col)
                && board.getPiece(oneStep) == null) {

            // Promotion
            if (oneForward == finalRank) {
                moves.add(new ChessMove(start, oneStep, PieceType.QUEEN));
                moves.add(new ChessMove(start, oneStep, PieceType.ROOK));
                moves.add(new ChessMove(start, oneStep, PieceType.BISHOP));
                moves.add(new ChessMove(start, oneStep, PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(start, oneStep, null));
            }

            int twoForward = row + (2 * direction);
            ChessPosition twoStep = new ChessPosition(twoForward, col);

            if (row == startRank
                    && board.getPiece(twoStep) == null) {
                moves.add(new ChessMove(start, twoStep, null));
            }
        }

        int[] captureCols = {col - 1, col + 1};

        for (int captureCol : captureCols) {
            int captureRow = row + direction;

            if (!ChessBoard.inBounds(captureRow, captureCol)) {
                continue;
            }

            ChessPosition capturePos = new ChessPosition(captureRow, captureCol);
            ChessPiece target = board.getPiece(capturePos);

            if (target == null) {
                continue;
            }
            if (target.getTeamColor() == pieceColor) {
                continue;
            }
            if (captureRow == finalRank) {
                moves.add(new ChessMove(start, capturePos, PieceType.QUEEN));
                moves.add(new ChessMove(start, capturePos, PieceType.ROOK));
                moves.add(new ChessMove(start, capturePos, PieceType.BISHOP));
                moves.add(new ChessMove(start, capturePos, PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(start, capturePos, null));
            }
        }

        return moves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        return switch (type) {
            case BISHOP -> bishopMoves(board, position);
            case ROOK -> rookMoves(board, position);
            case KNIGHT -> knightMoves(board, position);
            case QUEEN -> queenMoves(board, position);
            case KING -> kingMoves(board, position);
            case PAWN -> pawnMoves(board, position);
        };
    }

    private Collection<ChessMove> slidingMoves(
            ChessBoard board,
            ChessPosition start,
            int[][] directions) {

        List<ChessMove> moves = new ArrayList<>();

        for (int[] d : directions) {

            int row = start.getRow() + d[0];
            int col = start.getColumn() + d[1];

            while (ChessBoard.inBounds(row, col)) {

                ChessPosition dest = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(dest);

                if (target == null) {
                    moves.add(new ChessMove(start, dest, null));
                } else {
                    if (target.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(start, dest, null));
                    }
                    break;
                }

                row += d[0];
                col += d[1];
            }
        }

        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition position) {
        int[][] directions = {
                {1,1}, {1,-1}, {-1,1}, {-1,-1}
        };

        return slidingMoves(board, position, directions);
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition position) {
        int[][] directions = {
                {1,0}, {-1,0}, {0,1}, {0,-1}
        };

        return slidingMoves(board, position, directions);
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition position) {
        int[][] directions = {
                {1,0}, {-1,0}, {0,1}, {0,-1},
                {1,1}, {1,-1}, {-1,1}, {-1,-1}
        };

        return slidingMoves(board, position, directions);
    }

}


