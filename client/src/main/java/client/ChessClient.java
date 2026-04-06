package client;


import chess.ChessBoard;
import chess.ChessGame;
import client.REPL.GameplayRepl;
import client.REPL.PostLoginRepl;
import client.REPL.PreLoginRepl;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.*;
import ui.DisplayBoard;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.*;


public class ChessClient implements NotificationHandler {

    private String authToken = null;
    private List<GameData> lastGames = new ArrayList<>();
    private State state = State.LOGGED_OUT;
    private final PreLoginRepl preLoginRepl;
    private final PostLoginRepl postLoginRepl;
    private GameplayRepl gameplayRepl;
    private ChessGame.TeamColor playerColor = ChessGame.TeamColor.WHITE;
    private ChessGame game;
    private WebSocketFacade ws;

    public ChessClient(String serverURL) throws Exception {
        ServerFacade facade = new ServerFacade(serverURL);
        this.postLoginRepl = new PostLoginRepl(this, facade);
        this.preLoginRepl = new PreLoginRepl(this, facade);
        try {
            this.ws = new WebSocketFacade(serverURL, this);
        } catch (Exception e) {
            System.out.println("Warning: Web Socket Connection Faulting" + e.getMessage());
        }
    }

    public void run() {
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
                    case LOGGED_IN -> postLoginRepl.postLoginEval(cmd, params);
                    case IN_GAME -> gameplayRepl.gameplayEval(cmd, params);
                };
                System.out.println(result);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void setGameplayRepl(GameplayRepl gameplayRepl) {
        this.gameplayRepl = gameplayRepl;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }

    public enum State {
        LOGGED_OUT,
        LOGGED_IN,
        IN_GAME
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

    public String showBoard(String color){
//        ChessBoard board = new ChessBoard();
//        board.resetBoard();
//        if (color.equals("black")){
//            DisplayBoard.printBoard(board, ChessGame.TeamColor.BLACK);
//        }
//        else {
//            DisplayBoard.printBoard(board, ChessGame.TeamColor.WHITE);
//        }
        if (this.game != null) {
            DisplayBoard.printBoard(this.game.getBoard(), playerColor);
        } else {
            return "No game currently loaded.";
        }
        return "";
    }

    @Override
    public void notify(ServerMessage message){
        switch (message.getServerMessageType()){
            case LOAD_GAME ->{
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                this.game = loadGameMessage.getGame();
                System.out.println("\n");
                DisplayBoard.printBoard(game.getBoard(), playerColor );
                loadGame(loadGameMessage);
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println(errorMessage.getErrorMessage());
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) message;
                System.out.println(notificationMessage.getNotificationMessage());
            }

        }
    }

    public void loadGame(LoadGameMessage loadGameMessage) {
        ChessGame game = loadGameMessage.getGame();
        this.game = game;
        DisplayBoard.printBoard(game.getBoard(), this.playerColor);
        System.out.print("\n[IN_GAME] >>> ");
    }

    public ChessGame getGame() {
        return this.game;
    }

    public void connectSocket(int gameID) throws Exception{
        if (ws == null){
            throw new Exception("Error: WebSocket not initialized yet");
        }
        try{
            ws.connectWebSocket(authToken, gameID);
        } catch (Exception e) {
            throw new Exception("Connection not active");
        }
    }
}
