package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static ui.EscapeSequences.*;

public class DisplayBoard {

    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static void drawRow(ChessBoard board, int row, ChessGame.TeamColor perspective) {
        printBorderSquare(String.valueOf(row));

        if (perspective == ChessGame.TeamColor.WHITE) {
            for (int col = 1; col <= 8; col++) {
                printSquare(board, row, col);
            }
        } else {
            for (int col = 8; col >= 1; col--) {
                printSquare(board, row, col);
            }
        }
        printBorderSquare(String.valueOf(row));
        OUT.println(RESET_BG_COLOR); // Move to next line
    }

    private static void printBorderSquare(String letter ) {
        OUT.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + letter + " ");
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

    public static void printBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        String[] headers = perspective == ChessGame.TeamColor.WHITE
                ? new String[]{" ", "a", "b", "c", "d", "e", "f", "g", "h", " "}
                : new String[]{" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};

        printHeader(headers);

        if (perspective == ChessGame.TeamColor.WHITE) {
            for (int r = 8; r >= 1; r--) {
                drawRow(board, r, perspective);
            }
        } else {
            for (int r = 1; r <= 8; r++) {
                drawRow(board, r, perspective);
            }
        }

        printHeader(headers);
        OUT.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private static void printSquare(ChessBoard board, int row, int col) {
        if ((row + col) % 2 == 0) {
            OUT.print(SET_BG_COLOR_DARK_GREY);
        } else {
            OUT.print(SET_BG_COLOR_LIGHT_GREY);
        }
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece != null) {
            OUT.print(piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK);
            OUT.print(getPieceChar(piece));
        } else {
            OUT.print(EMPTY);
        }
    }

    private static void printHeader(String[] headers) {
        for (String h : headers) {
            printBorderSquare(h);
        }
        OUT.println(RESET_BG_COLOR);
    }
}
