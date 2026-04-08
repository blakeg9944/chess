package client.REPL;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ChessClient;
import client.ServerFacade;
import ui.DisplayBoard;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;

import java.util.Collection;
import java.util.List;
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
        System.out.println("[GameplayRepl] gameplayEval() called - cmd=" + cmd);
        try {
            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave(params);
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlightMovePieces(params);
                case "quit" -> "quit";
                default -> help3();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String highlightMovePieces(String[] params) throws Exception {
        if (params == null){
            throw new Exception("Error: Expected <PIECE POSITION>");
        }
        String cord = params[0];
        ChessPosition pos = parsePos(cord);
        Collection<ChessMove> moves = client.getGame().validMoves(pos);
        List<ChessPosition> endPositions = moves.stream()
                .map(ChessMove::getEndPosition)
                .toList();
        DisplayBoard.printBoardWithHighlights(client.getGame().getBoard(), pos, client.getPlayerColor(), endPositions);
        return "";
    }

    private String resign() throws Exception {
        if (client.getPlayerColor() == null) {
            throw new Exception("Error: Observers cannot resign");
        }
        String choice = promptConfirmResign();
        if (choice.equals("yes")){
            ResignCommand resignCommand = new ResignCommand(client.getAuthToken(), gameID);
            client.getWs().sendCommand(resignCommand);
            return "You have resigned";
        }
        else{
            return "Canceled: Continue with Game";
        }
    }

    private String promptConfirmResign() {
        System.out.println("Are you sure you want to resign? (Game will be forfeited): [yes] or [no]");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().toLowerCase();

    }

    private String move(String[] params) throws Exception {
        if (client.getPlayerColor() == null) {
            throw new Exception("Error: Observers cannot make moves");
        }
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
            if (game.getTeamTurn() != client.getPlayerColor()) {
                throw new Exception("Error: Not your turn");
            }
            if (piece == null) {
                throw new Exception("Error: No piece at position");
            }
            if (piece.getTeamColor() != client.getPlayerColor()) {
                throw new Exception("Error: Cannot move opponent's piece");
            }
            client.getWs().sendCommand(makeMoveCommand);
            return "Sending Move";

        } catch (Exception e) {
            System.out.println("[GameplayRepl] move() caught exception: " + e.getMessage());
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

    private String leave(String[] params) throws Exception {
        LeaveCommand leaveCommand = new LeaveCommand(client.getAuthToken(), gameID);
        try {
            client.getWs().sendCommand(leaveCommand);
            client.getWs().session.close();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        client.setState(ChessClient.State.LOGGED_IN);
        return "Game has been left";
    }

    private String redraw() {
        String perspective = playerColor.equalsIgnoreCase("observer") ? "white" : playerColor;
        return client.showBoard(perspective.toLowerCase());
    }

    private String help3() {
        return """
            redraw - redraws the chess board
            leave - exits the game back to menu
            move - <START POSITION> <END POSITION> - moves piece of choice
            resign - quit the game
            highlight - <PIECE POSITION> - highlights all legal moves
            help- with possible commands
            """;
    }

    private ChessPosition parsePos(String input) throws Exception {
        if (input == null || input.length() < 2) {
            throw new Exception("Error: Invalid format. Use 'a1' through 'h8'");
        }
        String cleanInput = input.toLowerCase();
        int col = cleanInput.charAt(0) - 'a' + 1;
        char rowChar = cleanInput.charAt(1);
        if (!Character.isDigit(rowChar)) {
            throw new Exception("Error: Row must be a number (1-8)");
        }
        int row = rowChar - '0';
        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new Exception("Error: Position out of bounds (a1-h8)");
        }
        return new ChessPosition(row, col);
    }
}