package client.REPL;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ChessClient;
import client.ServerFacade;
import websocket.commands.MakeMoveCommand;
import websocket.messages.LoadGameMessage;

import java.util.Scanner;

public class GameplayRepl {
    private final ChessClient client;
    private final ServerFacade facade;
    private final int gameID;
    private final String playerColor;

    public GameplayRepl(ChessClient client, ServerFacade facade, int gameID, String playerColor) {
        this.client = client;
        this.facade = facade;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public String gameplayEval(String cmd, String[] params) {
        try {
            return switch (cmd) {
                case "redraw" -> redraw(params);
                case "leave" -> leave(params);
                case "move" -> move(params);
                case "resign" -> resign(params);
                case "highlight" -> highlightMovePieces(params);
                case "quit" -> "quit";
                default -> help3();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String highlightMovePieces(String[] params) {
        return null;
    }

    private String resign(String[] params) throws Exception {
        return null;
    }

    private String move(String[] params) throws Exception {
        if (params.length != 2){
            throw new Exception("Error: Expected <START POSITION> <END POSITION>");
        }
        String startPosString = params[0];
        String endPosString = params[1];
        ChessGame game = client.getGame();
        try{
            ChessPosition startPos = parsePos(startPosString);
            ChessPosition endPos = parsePos(endPosString);
            isValid(startPos);
            int endRow = endPos.getRow();
            ChessPiece piece = game.getBoard().getPiece(startPos);
            ChessPiece.PieceType type = null;
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && endRow == 8) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && endRow == 1)) {

                type = promptForPromotion();
            }
            ChessMove move = new ChessMove(startPos, endPos, type);
            MakeMoveCommand makeMoveCommand = new MakeMoveCommand(client.getAuthToken(), gameID, move);

            return "Piece Moved!";

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private ChessPiece.PieceType promptForPromotion() {
        System.out.println("Pawn Promotion! Choose a piece: [Q]ueen, [R]ook, [B]ishop, [N]ight");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine().toLowerCase();
        return switch (choice){
            case "q", "queen" -> ChessPiece.PieceType.QUEEN;
            case "r", "rook" -> ChessPiece.PieceType.ROOK;
            case "b", "bishop" -> ChessPiece.PieceType.BISHOP;
            case "n", "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> {
                System.out.println("Invalid choice, defaulting to Queen.");
                yield ChessPiece.PieceType.QUEEN;
            }
        };
    }

    private void isValid(ChessPosition startPos) throws Exception {
        ChessGame game = client.getGame();
        if (game == null){
            throw new Exception("Error: Game not loaded");
        }
        ChessPiece piece = game.getBoard().getPiece(startPos);
        if (piece == null) {
            throw new Exception("Error: No piece at position entered");
        }
    }

    private String leave(String[] params) {
        client.setState(ChessClient.State.LOGGED_IN);
        return "Game has been left";
    }

    private String redraw(String[] params) {
        return client.showBoard(playerColor.toLowerCase());
    }

    private String help3() {
        return """
            redraw - redraws the chess board
            leave - exits the game back to menu
            move - <START POSITION> <END POSITION> - moves piece of choice
            resign - quit the game
            highlight - highlights all legal moves
            help- with possible commands
            """;
    }

    private ChessPosition parsePos(String input) throws Exception {
        int col = input.charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(input.charAt(1));
        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new Exception("Error: Move must be in Coordinate Format (a1)");
        }
        return new ChessPosition(row, col);
    }
}
