package server.websocket;

import com.mysql.cj.Session;
import websocket.messages.ServerMessage;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    // Instead of one big list, we group sessions by their Game ID
    ConcurrentHashMap<Integer, List<Session>> gameRooms = new ConcurrentHashMap<>();

    public void broadcast(int gameID, Session excludeSession, ServerMessage serverMessage){
        // 1. Find the specific list of players for this gameID
        // 2. Loop ONLY through those players
        // 3. Send the message (if they aren't the excludeSession)
    }
}
