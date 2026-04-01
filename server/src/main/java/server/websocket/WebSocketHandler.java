package server.websocket;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import service.BadRequestException;
import service.UnauthorizedException;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler {

    private final AuthDAO authDAO = new SQLAuthDAO();
    private final GameDAO gameDAO = new SQLGameDAO();
    private final ConnectionManager connections = new ConnectionManager();

    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case MAKE_MOVE -> handleMakeMove(session, message);
            case CONNECT -> handleConnect(session, message);
            case LEAVE -> handleLeave(session, message);
            case RESIGN -> handleResign(session, message);
        }
    }

    private void handleResign(Session session, String message) throws Exception{
        try {
            ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
            int gameID = resignCommand.getGameID();
            GameData gameData = gameDAO.getGame(gameID);
            AuthData authData = getAuth(resignCommand.getAuthToken());
            boolean isWhite = authData.username().equals(gameData.whiteUsername());
            boolean isBlack = authData.username().equals(gameData.blackUsername());
            if (!isBlack && !isWhite) {
                throw new Exception("Error: Observer cannot resign");
            }
            if (gameData.game().isGameOver()) {
                throw new Exception("Error: Game is Over");
            }
            gameData.game().setGameOver(true);
            gameDAO.updateGame(gameData);
            String notifMessage = String.format("[%s] has resigned the game", authData.username());
            NotificationMessage notificationMessage = new NotificationMessage(notifMessage);
            connections.broadcast(gameID, null, notificationMessage);
        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
            connections.sendMessage(errorMessage, session);
        }
    }

    private void handleLeave(Session session, String message) throws Exception {
        try{
            LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
            int gameID = leaveCommand.getGameID();
            GameData gameData = gameDAO.getGame(gameID);
            AuthData authData = getAuth(leaveCommand.getAuthToken());
            connections.removeSessionFromGame(gameID, session);
            boolean isWhite = authData.username().equals(gameData.whiteUsername());
            boolean isBlack = authData.username().equals(gameData.blackUsername());
            String notifString = "";
            if (isBlack){
                GameData newGame = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
                gameDAO.updateGame(newGame);
                notifString = String.format("%s has left the game as black", gameData.blackUsername());
            }
            else if (isWhite){
                GameData newGame = new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game());
                gameDAO.updateGame(newGame);
                notifString = String.format("%s has left the game as white", gameData.whiteUsername());
            }
            else {
                notifString = String.format("%s (observer) has left the game", authData.username());
            }
            NotificationMessage notificationMessage = new NotificationMessage(notifString);
            connections.broadcast(gameID, session, notificationMessage);
        }
        catch(DataAccessException e){
            ErrorMessage errorMessage = new ErrorMessage("Error: Check username");
            connections.sendMessage(errorMessage, session);
        }
        catch(UnauthorizedException e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Unauthorized");
            connections.sendMessage(errorMessage, session);
        }
    }

    private void handleConnect(Session session, String message) throws Exception {
        try{
            //parsing
            ConnectCommand command = new Gson().fromJson(message, ConnectCommand.class);
            int gameID = command.getGameID();
            AuthData authData = getAuth(command.getAuthToken());
            GameData gameData = getGame(gameID);
            //registration
            connections.addSessionToGame(gameID, session);
            boolean isWhite = authData.username().equals(gameData.whiteUsername());
            boolean isBlack = authData.username().equals(gameData.blackUsername());
            // communication
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            connections.sendMessage(loadGameMessage, session);
            String notifString = "";
            if (isWhite){
                notifString = String.format("%s has joined the game as white", authData.username());
            }
            else if(isBlack){
                notifString = String.format("%s has joined the game as black", authData.username());
            }
            else{
                notifString = String.format("%s is observing the game", authData.username());
            }
            NotificationMessage notificationMessage = new NotificationMessage(notifString);
            connections.broadcast(gameID, session, notificationMessage);
        }
        catch(UnauthorizedException exception){
            ErrorMessage errorMessage = new ErrorMessage("Error: Unauthorized");
            connections.sendMessage(errorMessage, session);
        } catch (BadRequestException e) {
            ErrorMessage errorMessage = new ErrorMessage("Error: Bad request");
            connections.sendMessage(errorMessage, session);
        }
        catch(DataAccessException exception){
            ErrorMessage errorMessage = new ErrorMessage("Error: Game Does Not Exist");
            connections.sendMessage(errorMessage, session);
        }

    }

    private void handleMakeMove(Session session, String message) throws Exception {
        try{
            MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
            int gameID = command.getGameID();
            AuthData authData = getAuth(command.getAuthToken());
            GameData gameData = getGame(gameID);
            checkMove(authData, gameData, command.getMove());
            gameData.game().makeMove(command.getMove());
            gameDAO.updateGame(gameData);
            connections.broadcast(gameID, null, new LoadGameMessage(gameData.game()));
            postMoveCheck(gameData, authData);
            String notifString = String.format("[%s] has moved to %s", authData.username(), command.getMove().getEndPosition());
            NotificationMessage notificationMessage = new NotificationMessage(notifString);
            connections.broadcast(gameID, session, notificationMessage);

        } catch (Exception e) {
            connections.sendMessage(new ErrorMessage(e.getMessage()), session);
        }
    }

    private AuthData getAuth(String authToken) throws Exception {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        return authData;
    }

    private GameData getGame(Integer gameID) throws Exception{
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null){
            throw new DataAccessException("Error: Game Not Found");
        }
        return gameData;
    }
    private void postMoveCheck(GameData gameData, AuthData authData) throws Exception{
        boolean isWhite = authData.username().equals(gameData.whiteUsername());
        boolean isBlack = authData.username().equals(gameData.blackUsername());
        ChessGame.TeamColor playerColor;
        ChessGame.TeamColor oppColor;
        if (isWhite){
            playerColor = ChessGame.TeamColor.WHITE;
            oppColor = ChessGame.TeamColor.BLACK;

        }
        else if(isBlack) {
            playerColor = ChessGame.TeamColor.BLACK;
            oppColor = ChessGame.TeamColor.WHITE;
        }
        else{
            playerColor = null;
            oppColor = null;
        }
        if (playerColor == null) {
            return;
        }

        if (gameData.game().isInCheckmate(oppColor)){
            gameData.game().setGameOver(true);
            gameDAO.updateGame(gameData);
            String notifString = String.format("Wow checkmate! [%s] wins", authData.username());
            NotificationMessage notificationMessage = new NotificationMessage(notifString);
            connections.broadcast(gameData.gameID(), null, notificationMessage);
        }
        else if (gameData.game().isInStalemate(oppColor)){
            gameDAO.updateGame(gameData);
            NotificationMessage notificationMessage = new NotificationMessage("Stalemate! Game over");
            connections.broadcast(gameData.gameID(), null, notificationMessage);
        }
        else if(gameData.game().isInCheck(oppColor)){
            String opp;
            if (isBlack){
                opp = "WHITE";
            }
            else{
                opp = "BLACK";
            }
            gameDAO.updateGame(gameData);
            String notifString = String.format("[%s] is in Check", opp );
            NotificationMessage notificationMessage = new NotificationMessage(notifString);
            connections.broadcast(gameData.gameID(), null, notificationMessage);
        }

    }

    private void checkMove(AuthData auth, GameData game, ChessMove move) throws Exception{
        if (game.game().isGameOver()){
            throw new Exception("Error: The game is over. No more moves allowed.");
        }
        String username = auth.username();
        ChessGame chessGame = game.game();
        if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
            throw new InvalidMoveException("You are not a player in this game.");
        }
        boolean isWhite = username.equals(game.whiteUsername());
        boolean isBlack = username.equals(game.blackUsername());
        ChessGame.TeamColor playerColor;
        if (isWhite){
            playerColor = ChessGame.TeamColor.WHITE;

        }
        else if(isBlack){
            playerColor = ChessGame.TeamColor.BLACK;
        }
        else{
            playerColor = null;
        }
        ChessGame.TeamColor currentTurn = chessGame.getTeamTurn();
        if (isWhite && currentTurn != ChessGame.TeamColor.WHITE ||
                isBlack && currentTurn != ChessGame.TeamColor.BLACK){
            throw new Exception("Error: It is not your turn");
        }
        ChessPiece piece = chessGame.getBoard().getPiece(move.getStartPosition());
        if (piece == null) {
            throw new Exception("Error: No piece at " + move.getStartPosition());
        }
        if (piece.getTeamColor() != playerColor) {
            throw new Exception("Error: That is not your piece!");
        }
    }
}
