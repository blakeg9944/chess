package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.*;
import ui.DisplayBoard;

import java.util.ArrayList;
import java.util.List;

public class PostLoginRepl {

    private final ChessClient client;
    private final ServerFacade facade;

    public PostLoginRepl(ChessClient client, ServerFacade facade) {
        this.client = client;
        this.facade = facade;
    }

    public String logout() throws Exception {
        LogoutRequest logoutRequest = new LogoutRequest(client.getAuthToken());
        facade.logout(logoutRequest);
        client.setState(ChessClient.State.LOGGED_OUT);
        client.setAuthToken(null);
        return "Now logged out";
    }

    public String listGames() throws Exception{
        ListGamesRequest listGamesRequest = new ListGamesRequest(client.getAuthToken());
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
        var result = new StringBuilder();
        List<GameData> lastGames = new ArrayList<>(listGamesResult.games());
        client.setLastGames(lastGames);
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

    public String joinGame(String[] params) throws Exception {
        if (params.length < 2) {
            throw new Exception("Expected: <NUMBER> [WHITE|BLACK]");
        }
        try {
            int gameIndex = Integer.parseInt(params[0]) - 1;
            List<GameData> lastGames = client.getLastGames();
            if (lastGames == null || lastGames.isEmpty()) {
                throw new Exception("No games found. Please run 'list' first to see available games.");
            }
            if (gameIndex >= lastGames.size() || gameIndex < 0) {
                throw new Exception("Check your game number OR make sure list has been run");
            }
            GameData game = lastGames.get(gameIndex);
            String color = params[1].toUpperCase();
            JoinGameRequest request = new JoinGameRequest(color, game.gameID());
            facade.joinGame(request, client.getAuthToken());
            showBoard(color.toLowerCase());
            ///this.state = State.IN_GAME;
            return String.format("Success! You have joined %s as %s. Configuring board", game.gameName(), color);
        } catch (NumberFormatException e) {
            throw new Exception("The first argument must be a number.");
        }
    }

    public String observeGame(String[] params) throws Exception {
        List<GameData> lastGames = client.getLastGames();
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

    public String createGame(String[] params) throws Exception {
        if (params.length >= 1){
            try {
                String gameName = params[0];
                CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
                facade.createGame(createGameRequest, client.getAuthToken());
                return String.format("Game %s created", gameName);
            } catch (Exception e) {
                throw new Exception("Expected: create <GAMENAME>");
            }

        }
        throw new Exception("Expected: create <GAMENAME>");
    }

    public String showBoard(String color){
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
}
