package server;

import chess.ChessGame;
import chess.ChessPiece;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import io.javalin.*;
import server.handler.*;
import server.websocket.WebSocketHandler;
import service.*;

import static dataaccess.DatabaseManager.createDatabase;
import static dataaccess.DatabaseManager.createTables;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new SQLUserDAO();
    private final AuthDAO authDAO = new SQLAuthDAO();
    private final GameDAO gameDAO = new SQLGameDAO();

    public Server() {
        System.out.println("[Server] Constructor called");
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        System.out.println("[Server] Javalin instance created");
        try {
            System.out.println("[Server] Attempting to create database...");
            createDatabase();
            System.out.println("[Server] Database created successfully");
            System.out.println("[Server] Attempting to create tables...");
            createTables();
            System.out.println("[Server] Tables created successfully");
        }
        catch(Exception e){
            System.err.println("[Server] Exception during database/table creation: " + e.getMessage());
            e.printStackTrace();
            System.err.println("Uhh oh! Your Server is Cooked!");
        }
    }

    public int run(int desiredPort) {
        System.out.println("[Server] run() called with desiredPort=" + desiredPort);

        System.out.println("[Server] Initializing WebSocketHandler...");
        WebSocketHandler webSocketHandler = new WebSocketHandler(authDAO, gameDAO);
        System.out.println("[Server] WebSocketHandler initialized");

        javalin.ws("/ws", ws -> {
            System.out.println("[Server] Registering WebSocket handlers on /ws");
            ws.onConnect(webSocketHandler);
            System.out.println("[Server] WebSocket onConnect handler registered");
            ws.onMessage(webSocketHandler);
            System.out.println("[Server] WebSocket onMessage handler registered");
            ws.onClose(webSocketHandler);
            System.out.println("[Server] WebSocket onClose handler registered");
        });

        System.out.println("[Server] Registering DELETE /db (ClearHandler)");
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        javalin.delete("/db", (ctx) -> new ClearHandler(clearService).handle(ctx));

        System.out.println("[Server] Registering POST /user (RegisterHandler)");
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        RegisterHandler registerHandler = new RegisterHandler(registerService);
        javalin.post("/user", registerHandler);

        System.out.println("[Server] Registering POST /session (LoginHandler)");
        LoginService loginService = new LoginService(userDAO, authDAO);
        LoginHandler loginHandler = new LoginHandler(loginService);
        javalin.post("/session", loginHandler);

        System.out.println("[Server] Registering DELETE /session (LogoutHandler)");
        LogoutService logoutService = new LogoutService(userDAO, authDAO);
        LogoutHandler logoutHandler = new LogoutHandler(logoutService);
        javalin.delete("/session", logoutHandler);

        System.out.println("[Server] Registering POST /game (CreateGameHandler)");
        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        CreateGameHandler createGameHandler = new CreateGameHandler(createGameService);
        javalin.post("/game", createGameHandler);

        System.out.println("[Server] Registering GET /game (ListGamesHandler)");
        ListGamesService listGamesService = new ListGamesService(gameDAO, authDAO);
        ListGamesHandler listGamesHandler = new ListGamesHandler(listGamesService);
        javalin.get("/game", listGamesHandler);

        System.out.println("[Server] Registering PUT /game (JoinGameHandler)");
        JoinGameService joinGameService = new JoinGameService(authDAO, gameDAO);
        JoinGameHandler joinGameHandler = new JoinGameHandler(joinGameService);
        javalin.put("/game", joinGameHandler);

        // START after all routes are registered
        javalin.start(desiredPort);
        System.out.println("[Server] All routes registered. Server running on port " + javalin.port());
        return javalin.port();
    }

    public void stop() {
        System.out.println("[Server] stop() called");
        javalin.stop();
        System.out.println("[Server] Javalin stopped");
    }

}