package client;

import model.LoginRequest;
import model.LoginResult;

import java.util.Arrays;
import java.util.Scanner;


public class ChessClient {

    private String authToken = null;
    private final ServerFacade facade;

    public ChessClient(String serverURL) {
        this.facade = new ServerFacade(serverURL);
    }

    public void run() throws Exception {
        System.out.println("Welcome to Chess! Type 'Help' to begin.");
        Scanner scanner = new Scanner(System.in);
        String result = '';
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
            if (authToken == null) {
                preLoginEval(cmd, params);
            } else {
                postLoginEval(input);
            }
        }
    }

    public String help() {
        return """
            register <USERNAME> <PASSWORD> <EMAIL> - to create an account
            login <USERNAME> <PASSWORD> - to play chess
            quit - playing chess
            help - with possible commands
            """;
    }

    private String preLoginEval(String cmd, String[] params) throws Exception{
        try {
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void postLoginEval(String input){

    }

    private String login(String[] params) throws Exception {
        if(params.length == 2){
            String username = params[0];
            String password = params[1];
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = facade.login(loginRequest);
            this.authToken = loginResult.authToken();
            return String.format("Now logged in as %s", username);
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD>");
    }

    private void register(String username, String password, String email){

    }
}
