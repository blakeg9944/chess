package server.websocket;

import com.google.gson.Gson;
import com.mysql.cj.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler {

    public void broadcast(int gameID, Session excludeSession, ServerMessage serverMessage){
        // 1. Find the specific list of players for this gameID
        // 2. Loop ONLY through those players
        // 3. Send the message (if they aren't the excludeSession)
    }

    public void sendMessage(String message, Session session) throws IOException {
        String json = new Gson().toJson(message);
    }
}
