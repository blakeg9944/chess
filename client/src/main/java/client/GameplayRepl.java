package client;

public class GameplayRepl {
    private final ChessClient client;
    private final ServerFacade facade;

    public GameplayRepl(ChessClient client, ServerFacade facade) {
        this.client = client;
        this.facade = facade;
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
    }

    private String resign(String[] params) {
    }

    private String move(String[] params) {
    }

    private String leave(String[] params) {
    }

    private String redraw(String[] params) {
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
}
