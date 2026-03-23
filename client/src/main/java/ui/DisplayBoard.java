package ui;

import chess.*;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class DisplayBoard {

    private static void drawSquare(ChessBoard board, int r, int c){
        if ((r + c) % 2 == 0) {
            out.print(SET_BG_COLOR_DARK_GREEN);
        } else {
            out.print(SET_BG_COLOR_WHITE);
        }

        ChessPiece curr = board.getPiece(new ChessPosition(r, c));

        if (curr != null) {
            if (curr.getTeamColor() == ChessGame.TeamColor.WHITE) {
                out.print(SET_TEXT_COLOR_WHITE + getPieceChar(curr));
            } else {
                out.print(SET_TEXT_COLOR_BLACK + getPieceChar(curr));
            }
        } else {
            out.print(EMPTY);
        }


    }

    private void printHeaders(String[] headers){
        for (String h: headers){
            printBorderSquare(h);
        }
    }

    private static void printBorderSquare(String letter ) {
        out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + letter + " ");
    }

    private static String getPieceChar(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
        };
    }
}
