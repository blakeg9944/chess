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
import server.Server;
import service.UnauthorizedException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;

public class WebSocketHandler {

    private final AuthDAO authDAO = new SQLAuthDAO();
    private final GameDAO gameDAO = new SQLGameDAO();
    private final ConnectionManager connections = new ConnectionManager();

    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()){
            case MAKE_MOVE -> handleMakeMove(session, message);
            case CONNECT -> handleConnect(session, message);
            case LEAVE -> handleLeave(session, message);
            case RESIGN -> handleResign(session, message);
        }
    }

    private void handleResign(Session session, String message) {
    }

    private void handleLeave(Session session, String message) {
        
    }

    private void handleConnect(Session session, String message) {
        
    }

    private void handleMakeMove(Session session, String message) throws Exception {
        try{
            MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
            int gameID = command.getGameID();
            //Verify the Session: Use the authToken from the command to make sure this person is actually who they say they are (and that it's actually their turn!).
            AuthData authData = getAuth(command.getAuthToken());
            GameData gameData = getGame(gameID);

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

    private void checkMove(AuthData auth, GameData game, ChessMove move) throws Exception{
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
        else{
            playerColor = ChessGame.TeamColor.BLACK;
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
