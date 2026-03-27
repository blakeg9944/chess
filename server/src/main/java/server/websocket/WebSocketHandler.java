package server.websocket;
import chess.ChessGame;
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
            String username = authData.username();
            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                throw new InvalidMoveException("You are not a player in this game.");
            }

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
}
