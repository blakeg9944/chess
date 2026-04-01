package client;


import model.*;

import java.util.*;


public class ChessClient {

    private String authToken = null;
    private List<GameData> lastGames = new ArrayList<>();
    private State state = State.LOGGED_OUT;
    private final PreLoginRepl preLoginRepl;
    private final PostLoginRepl postLoginRepl;
    private GameplayRepl gameplayRepl;

    public ChessClient(String serverURL) {
        ServerFacade facade = new ServerFacade(serverURL);
        this.postLoginRepl = new PostLoginRepl(this, facade);
        this.preLoginRepl = new PreLoginRepl(this, facade);
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
}
