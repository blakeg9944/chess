package server.websocket;

import com.mysql.cj.Session;
import websocket.messages.ServerMessage;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    // Instead of one big list, we group sessions by their Game ID
    ConcurrentHashMap<Integer, Set<Session>> gameRooms = new ConcurrentHashMap<>();

    public void broadcast(int gameID, Session excludeSession, ServerMessage serverMessage){
        // 1. Find the specific list of players for this gameID
        // 2. Loop ONLY through those players
        // 3. Send the message (if they aren't the excludeSession)
    }

    public void addSessionToGame(int gameID, Session session){
        Set<Session> sessions = gameRooms.computeIfAbsent(gameID, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
    }

    public void removeSessionFromGame(int gameID, Session session){
        Set<Session> sessionSet = getSessionsForGame(gameID);
        if (sessionSet != null){
            sessionSet.remove(session);
            if (sessionSet.isEmpty()){
                gameRooms.remove(gameID);
            }
        }
    }

    public Set<Session> getSessionsForGame(int gameID){
        return gameRooms.get(gameID);
    }
}
