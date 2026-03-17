package client;

import java.util.Arrays;
import java.util.Scanner;


public class ChessClient {

    private String authToken = null;
    private final ServerFacade facade;

    public ChessClient(String serverURL) {
        this.facade = new ServerFacade(serverURL);
    }

    public void run(){
        System.out.println("Welcome to Chess! Type 'Help' to begin.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (authToken == null) {
                preLoginEval(input);
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

    private void preLoginEval(String response){

    }

    private void postLoginEval(String input){

    }
}
