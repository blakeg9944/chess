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
        PieceType[] promotions = {PieceType.QUEEN, PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP};
        if (piece.getPieceType() == PieceType.BISHOP){
            /*int [][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
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
            }*/
            //return List.of(new ChessMove(new ChessPosition(5, 4), new ChessPosition(1,8), null));
            moves = (List<ChessMove>) bishopMoves(board, myPosition);
        }
        else if (piece.getPieceType() == PieceType.ROOK){
            /*int [][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
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
            }*/
            moves = (List<ChessMove>) rookMoves(board, myPosition);
        }
        else if (piece.getPieceType() == PieceType.KNIGHT){
            /*int[][] directions = {{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}};
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
                    }
                }
            }*/
            return knightMoves(board, myPosition);
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
            int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1,-1}, {0, -1}, {1, -1}};
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
                    }
                }
            }
        }
        else if (piece.getPieceType() == PieceType.PAWN){
            if (piece.pieceColor == ChessGame.TeamColor.WHITE){
                int[][] whiteDir = {{1, -1}, {1, 1}};
                for (int[] d : whiteDir){
                    int rowDestin = d[0] + startRow;
                    int colDestin = d[1] + startCol;
                    if (ChessBoard.inBounds(rowDestin, colDestin)) {
                        ChessPosition destin = new ChessPosition(rowDestin, colDestin);
                        ChessPiece destinPiece = board.getPiece(destin);
                        if (destinPiece != null) {
                            if (destinPiece.pieceColor != piece.pieceColor) {
                                if (rowDestin == 8){
                                    for (PieceType p : promotions){
                                        moves.add(new ChessMove(start, destin, p));
                                    }
                                }
                                else {
                                    moves.add(new ChessMove(start, destin, null));
                                }
                            }
                        }
                    }
                }
                if (ChessBoard.inBounds(startRow + 1, startCol)) {
                    ChessPiece straightPiece = board.getPiece(new ChessPosition(startRow + 1, startCol));
                    if (straightPiece == null) {
                        if ((startRow + 1) == 8) {
                            for (PieceType p: promotions){
                                moves.add(new ChessMove(start, new ChessPosition(startRow + 1, startCol), p));
                            }
                        }
                        else if (startRow == 2){
                            moves.add(new ChessMove(start, new ChessPosition(startRow + 1, startCol), null));
                            ChessPiece destinPiece = board.getPiece(new ChessPosition(startRow + 2, startCol));
                            if (destinPiece == null) {
                                moves.add(new ChessMove(start, new ChessPosition(startRow + 2, startCol), null));
                            }
                        }
                        else {
                            moves.add(new ChessMove(start, new ChessPosition(startRow + 1, startCol), null));
                        }
                    }
                }
            }
            else {
                int[][] blackDir = {{-1, -1}, {-1, 1}};
                for (int[] d : blackDir){
                    int rowDestin = d[0] + startRow;
                    int colDestin = d[1] + startCol;
                    if (ChessBoard.inBounds(rowDestin, colDestin)) {
                        ChessPosition destin = new ChessPosition(rowDestin, colDestin);
                        ChessPiece destinPiece = board.getPiece(destin);
                        if (destinPiece != null) {
                            if (destinPiece.pieceColor != piece.pieceColor) {
                                if (rowDestin == 1){
                                    for (PieceType p : promotions){
                                        moves.add(new ChessMove(start, destin, p));
                                    }
                                }
                                else {
                                    moves.add(new ChessMove(start, destin, null));
                                }
                            }
                        }
                    }
                }
                if (ChessBoard.inBounds(startRow, startCol - 1)) {
                    ChessPosition straightDestin = new ChessPosition(startRow -1 , startCol);
                    ChessPiece straightPiece = board.getPiece(straightDestin);
                    if (straightPiece == null) {
                        if ((startRow-1) == 1) {
                            for (PieceType p : promotions){
                                moves.add(new ChessMove(start, straightDestin, p));
                            }
                        }
                        else {
                            moves.add(new ChessMove(start, straightDestin, null));
                        }
                        if (startRow == 7){
                            ChessPiece destinPiece = board.getPiece(new ChessPosition(startRow - 2, startCol));
                            if (destinPiece == null) {
                                moves.add(new ChessMove(start, new ChessPosition(startRow - 2, startCol), null));
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        List <ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        int startCol = myPosition.getColumn();
        int startRow = myPosition.getRow();
        int [][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] d : directions){
            int rowDestin = d[0] + startRow;
            int colDestin = d[1] + startCol;
            while (ChessBoard.inBounds(rowDestin, colDestin)){
                ChessPosition destin = new ChessPosition(rowDestin, colDestin);
                ChessPiece destinPiece = board.getPiece(destin);
                if (destinPiece == null){
                    moves.add(new ChessMove(myPosition, destin, null));
                }
                else {
                    if (destinPiece.getTeamColor() != piece.pieceColor){
                        moves.add(new ChessMove(myPosition, destin, null));
                    }
                    break;
                }
                rowDestin += d[0];
                colDestin += d[1];
            }
        }
        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        int [][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int [] d : directions){
            int destinRow = d[0] + startRow;
            int destinCol = d[1] + startCol;
            while (ChessBoard.inBounds(destinRow, destinCol)){
                ChessPosition destin = new ChessPosition(destinRow, destinCol);
                ChessPiece destinPiece= board.getPiece(destin);
                if (destinPiece == null){
                    moves.add(new ChessMove(myPosition, destin, null));
                }
                else{
                    if (piece.pieceColor !=  destinPiece.getTeamColor()){
                        moves.add(new ChessMove(myPosition, destin, null));
                    }
                    break;
                }
                destinRow += d[0];
                destinCol += d[1];
            }

        }
        return moves;
    }
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
}


