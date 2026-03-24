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
            if (!result.equals("quit")) {
                System.out.println(result);
            }
        }
    }

    public String help1() {
        return """
            register <USERNAME> <PASSWORD> <EMAIL> - to create an account
            login <USERNAME> <PASSWORD> - to play chess
            quit - playing chess
            help - with possible commands
            """;
    }

    public String help2() {
        return """
            logout - to logout of account
            create <GAMENAME> - to create a game for you and a friend
            list - lists all current games
            play- <NUMBER> [WHITE|BLACK] only valid once game has been created. if so, hop into competition! 
            observe- watch your friends play chess
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
                default -> help1();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String postLoginEval(String command, String[] params){
        try {
            return switch (command) {
                case "logout" -> logout();
                case "list" -> listGames();
                case "play" -> joinGame(params);
                case "create" -> createGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help2();
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
                this.authToken = loginResult.authToken();
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
                this.authToken = registerResult.authToken();
                return String.format("Now registered as %s", username);
            } catch (Exception e) {
                throw new Exception("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
            }
        }
        throw new Exception("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String logout() throws Exception {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        facade.logout(logoutRequest);
        this.authToken = null;
        return "Now logged out";
    }

    private String listGames() throws Exception{
        ListGamesRequest listGamesRequest = new ListGamesRequest(this.authToken);
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
        var result = new StringBuilder();
        this.lastGames = new ArrayList<>(listGamesResult.games());
        if (lastGames.isEmpty()){
            return "There are no games";
        }
        for (int i = 0; i < lastGames.size(); i++ ){
            var game = lastGames.get(i);
            String displayWhite = (game.whiteUsername() == null) ? "---" : game.whiteUsername();
            String displayBlack = (game.blackUsername() == null) ? "---" : game.blackUsername();
            result.append(String.format("%d. %s (W: %s, B: %s)\n",
                    i + 1, game.gameName(), displayWhite, displayBlack));
        }
        return result.toString();
    }

    private String joinGame(String[] params) throws Exception {
        if (params.length < 2) {
            throw new Exception("Expected: <NUMBER> [WHITE|BLACK]");
        }
        try {
            int gameIndex = Integer.parseInt(params[0]) - 1;
            if (gameIndex >= lastGames.size() || gameIndex < 0) {
                throw new Exception("Check your game number OR make sure list has been run");
            }
            GameData game = lastGames.get(gameIndex);
            String color = params[1].toUpperCase();
            JoinGameRequest request = new JoinGameRequest(color, game.gameID());
            facade.joinGame(request, authToken);
            showBoard(color.toLowerCase());
            return String.format("Success! You have joined %s as %s. Configuring board", game.gameName(), color);
        } catch (NumberFormatException e) {
            throw new Exception("The first argument must be a number.");
        }
    }

    private String observeGame(String[] params) throws Exception {
        if (params.length != 1) {
            throw new Exception("Expected: <ID>");
        }
        int gameIndex;
        try{
            gameIndex = Integer.parseInt(params[0]) - 1;
        }
        catch(Exception e){
            throw new Exception("The first argument must be a number.");
        }
        if (gameIndex >= lastGames.size() || gameIndex < 0) {
            throw new Exception("Check your game number OR make sure list has been run");
        }
        showBoard("white");
        return String.format("Enjoy!");
    }

    private String createGame(String[] params) throws Exception {
        if (params.length >= 1){
            try {
                String gameName = params[0];
                CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
                facade.createGame(createGameRequest, authToken);
                return String.format("Game %s created", gameName);
            } catch (Exception e) {
                throw new Exception("Expected: create <GAMENAME>");
            }

        }
        throw new Exception("Expected: create <GAMENAME>");
    }

    private String showBoard(String color){
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        if (color.equals("black")){
            DisplayBoard.printBoard(board, ChessGame.TeamColor.BLACK);
        }
        else {
            DisplayBoard.printBoard(board, ChessGame.TeamColor.WHITE);
        }
        return "";
    }
}
