package client;

import model.*;

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
            if (authToken == null) {
                result = preLoginEval(cmd, params);
            } else {
                result = postLoginEval(cmd, params);
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

    private String postLoginEval(String command, String[] params){
        try {
            return switch (command) {
                case "logout" -> logout();
                case "list games" -> login(params);
                case "join game" -> register(params);
                case "create game" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
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

    private String register(String[] params) throws Exception {
        if(params.length == 3){
            String username = params[0];
            String password = params[1];
            String email = params[2];
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            RegisterResult registerResult = facade.register(registerRequest);
            this.authToken = registerResult.authToken();
            return String.format("Now registered as %s", username);
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String logout() throws Exception {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        facade.logout(logoutRequest);
        this.authToken = null;
        return "Now logged out";
    }
}
