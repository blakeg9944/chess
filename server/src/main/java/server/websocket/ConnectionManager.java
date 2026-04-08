package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    ConcurrentHashMap<Integer, Set<Session>> gameRooms = new ConcurrentHashMap<>();

    public void addSessionToGame(int gameID, Session session) {
        gameRooms.computeIfAbsent(gameID, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(session);
    }

    public void removeSessionFromGame(int gameID, Session session) {
        Set<Session> sessionSet = getSessionsForGame(gameID);
        if (sessionSet != null) {
            sessionSet.remove(session);
            if (sessionSet.isEmpty()) {
                gameRooms.remove(gameID);
            }
        }
    }

    public Set<Session> getSessionsForGame(int gameID) {
        return gameRooms.get(gameID);
    }

    // FIX: check session.isOpen() before sending
    public void sendMessage(ServerMessage serverMessage, Session session) throws IOException {
        if (session.isOpen()) {
            String message = new Gson().toJson(serverMessage);
            session.getRemote().sendString(message);
        } else {
            System.out.println("[ConnectionManager] sendMessage() skipped - session is closed");
        }
    }

    // FIX: null-safe excludeSession check + isOpen() guard
    public void broadcast(int gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        Set<Session> sessions = getSessionsForGame(gameID);
        System.out.println("Broadcasting to game " + gameID + ". Total sessions found: " + (sessions != null ? sessions.size() : 0));
        if (sessions == null) {
            return;
        }
        String message = new Gson().toJson(serverMessage);
        for (Session s : sessions) {
            boolean isExcluded = excludeSession != null && s.equals(excludeSession);
            if (s.isOpen() && !isExcluded) {
                s.getRemote().sendString(message);
            }
        }
    }
}