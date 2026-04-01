package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.*;
import ui.DisplayBoard;

import java.util.*;


public class ChessClient {

    private String authToken = null;
    private final ServerFacade facade;
    private List<GameData> lastGames = new ArrayList<>();
    private State state = State.LOGGED_OUT;
    private final PreLoginRepl preLoginRepl;
    private final PostLoginRepl postLoginRepl;

    public ChessClient(String serverURL) {
        this.facade = new ServerFacade(serverURL);
        this.postLoginRepl = new PostLoginRepl(this, facade);
        this.preLoginRepl = new PreLoginRepl(this, facade);
    }

    public void run() throws Exception {
        System.out.println("Welcome to Chess! Type 'Help' to begin.");
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            String input = scanner.nextLine();
            var tokens = input.toLowerCase().split(" ");
            String cmd;
            if (tokens.length > 0) {
                cmd = tokens[0];
            } else {
                cmd = "help";
            }
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            try {
                result = switch (state) {
                    case LOGGED_OUT -> preLoginRepl.preLoginEval(cmd, params);
                    case LOGGED_IN -> postLoginEval(cmd, params);
                    ///case IN_GAME -> gameplayEval(cmd, params);
                };
                System.out.println(result);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

//    public String gameplayEval(String cmd, String[] params) {
//        try {
//            return switch (cmd) {
//                case "login" -> login(params);
//                case "register" -> register(params);
//                case "quit" -> "quit";
//                default -> help3();
//            };
//        } catch (Exception e) {
//            return e.getMessage();
//        }
//    }

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

    public String postLoginEval(String command, String[] params){
        try {
            return switch (command) {
                case "logout" -> postLoginRepl.logout();
                case "list" -> postLoginRepl.listGames();
                case "play" -> postLoginRepl.joinGame(params);
                case "create" -> postLoginRepl.createGame(params);
                case "observe" -> postLoginRepl.observeGame(params);
                case "quit" -> "quit";
                default -> postLoginRepl.help2();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public enum State {
        LOGGED_OUT,
        LOGGED_IN,
        ///IN_GAME
    }

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public void setState(State state){
        this.state = state;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setLastGames(List<GameData> lastGames) {
        this.lastGames = lastGames;
    }

    public List<GameData> getLastGames() {
        return lastGames;
    }
}
