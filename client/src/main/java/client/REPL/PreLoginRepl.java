package client.REPL;

import client.ChessClient;
import client.ServerFacade;
import model.*;

public class PreLoginRepl {
    private final ChessClient client;
    private final ServerFacade facade;

    public PreLoginRepl(ChessClient client, ServerFacade facade) {
        this.client = client;
        this.facade = facade;
    }


    public String preLoginEval(String cmd, String[] params) throws Exception{
        try {
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help1();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String login(String[] params) throws Exception {
        if(params.length == 2){
            try {
                String username = params[0];
                String password = params[1];
                LoginRequest loginRequest = new LoginRequest(username, password);
                LoginResult loginResult = facade.login(loginRequest);
                client.setAuthToken(loginResult.authToken());
                client.setState(ChessClient.State.LOGGED_IN);
                return String.format("Now logged in as %s", username);
            } catch (Exception e) {
                throw new Exception("Error Unauthorized");
            }
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD>");
    }

    private String register(String[] params) throws Exception {
        if(params.length == 3){
            try {
                String username = params[0];
                String password = params[1];
                String email = params[2];
                RegisterRequest registerRequest = new RegisterRequest(username, password, email);
                RegisterResult registerResult = facade.register(registerRequest);
                client.setAuthToken(registerResult.authToken());
                client.setState(ChessClient.State.LOGGED_IN);
                return String.format("Now registered as %s", username);
            } catch (Exception e) {
                throw new Exception("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
            }
        }
        throw new Exception("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String help1() {
        return """
            register <USERNAME> <PASSWORD> <EMAIL> - to create an account
            login <USERNAME> <PASSWORD> - to play chess
            quit - playing chess
            help - with possible commands
            """;
    }
}
