package chess;

import java.util.*;
import java.util.Map;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null;
        }
        List<ChessMove> moves = new ArrayList<>();
        moves = (List<ChessMove>) piece.pieceMoves(board, startPosition);
        List<ChessMove> movesFinal = new ArrayList<>();
        for (ChessMove m : moves){
            ChessBoard simulatedBoard = board.clone();
            simulatedBoard.addPiece(m.getEndPosition(), piece);
            simulatedBoard.addPiece(m.getStartPosition(), null);
            if (!simulateisInCheck(piece.getTeamColor(), simulatedBoard)){
                movesFinal.add(m);
            }
        }
        return movesFinal;
        // find piece at start position
        // run through piece calculator
        // remove pieces that put piece in check
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
       int placeHolder = 3;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.getKingPosition(teamColor);
        Map<ChessPosition, ChessPiece> enemyPieces;
        List<ChessMove> enemyMoves = new ArrayList<>();
        if (teamColor == TeamColor.BLACK){
            enemyPieces = board.getPieces(TeamColor.WHITE);
        }
        else{
            enemyPieces = board.getPieces(TeamColor.BLACK);
        }
        for (Map.Entry<ChessPosition, ChessPiece> entry : enemyPieces.entrySet()){
            ChessPiece piece = entry.getValue();
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, entry.getKey()); // piece's moves
            enemyMoves.addAll(pieceMoves); // append all moves to master list
        }
        for (ChessMove m : enemyMoves){
            if (m.getEndPosition().equals(kingPosition)){
                return true;
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
        if (!isInCheck(teamColor)){
            return false;
        }
        Map<ChessPosition, ChessPiece> myPieces;
        myPieces = board.getPieces(teamColor);
        for (Map.Entry<ChessPosition, ChessPiece> entry : myPieces.entrySet()) {
            ChessPiece piece = entry.getValue();
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, entry.getKey());
            for (ChessMove m: pieceMoves){
                ChessBoard simulatedBoard = board.clone();
                simulatedBoard.addPiece(m.getEndPosition(), piece);
                simulatedBoard.addPiece(m.getStartPosition(), null);
                if (!simulateisInCheck(teamColor, simulatedBoard)){
                    return false;
                }

            }
        }
        return true;
        //    if NOT inCheck(currentPlayer):
        //            return false
        //
        //            for each piece belonging to currentPlayer:
        //            for each legal move of that piece:
        //    simulate the move
        //        if king is NOT in check after the move:
        //    undo the move
        //            return false  // escape exists
        //    undo the move

        //return true  // no escape → checkmate
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            return false;
        }
        Map<ChessPosition, ChessPiece> myPieces;
        myPieces = board.getPieces(teamColor);
        for (Map.Entry<ChessPosition, ChessPiece> entry : myPieces.entrySet()) {
            ChessPiece piece = entry.getValue();
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, entry.getKey());
            for (ChessMove m: pieceMoves){
                ChessBoard simulatedBoard = board.clone();
                simulatedBoard.addPiece(m.getEndPosition(), piece);
                simulatedBoard.addPiece(m.getStartPosition(), null);
                if (!simulateisInCheck(teamColor, simulatedBoard)){
                    return false;
                }

            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", turn=" + teamTurn +
                '}';
    }

    public boolean simulateisInCheck(TeamColor teamColor, ChessBoard theBoard) {
        ChessPosition kingPosition = theBoard.getKingPosition(teamColor);
        Map<ChessPosition, ChessPiece> enemyPieces;
        List<ChessMove> enemyMoves = new ArrayList<>();
        if (teamColor == TeamColor.BLACK){
            enemyPieces = theBoard.getPieces(TeamColor.WHITE);
        }
        else{
            enemyPieces = theBoard.getPieces(TeamColor.BLACK);
        }
        for (Map.Entry<ChessPosition, ChessPiece> entry : enemyPieces.entrySet()){
            ChessPiece piece = entry.getValue();
            Collection<ChessMove> pieceMoves = piece.pieceMoves(theBoard, entry.getKey()); // piece's moves
            enemyMoves.addAll(pieceMoves); // append all moves to master list
        }
        for (ChessMove m : enemyMoves){
            if (m.getEndPosition().equals(kingPosition)){
                return true;
            }
        }
        return false;
    }
}
