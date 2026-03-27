package server.websocket;
import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {

    public void onMessage(Session session, String message){
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

    private void handleMakeMove(Session session, String message) {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        int gameID = command.getGameID();


        
    }
}
