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
import service.*;

import static dataaccess.DatabaseManager.createDatabase;
import static dataaccess.DatabaseManager.createTables;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new SQLUserDAO();
    private final AuthDAO authDAO = new SQLAuthDAO();
    private final GameDAO gameDAO = new SQLGameDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        try {
            createDatabase();
            createTables();
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("Uhh oh! Your Server is Cooked!");
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        //
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        javalin.delete("/db", (ctx) -> new ClearHandler(clearService).handle(ctx));
        //
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        RegisterHandler registerHandler = new RegisterHandler(registerService);
        javalin.post("/user", registerHandler);
        //
        LoginService loginService = new LoginService(userDAO, authDAO);
        LoginHandler loginHandler = new LoginHandler(loginService);
        javalin.post("/session",loginHandler);
        //
        LogoutService logoutService = new LogoutService(userDAO, authDAO);
        LogoutHandler logoutHandler = new LogoutHandler(logoutService);
        javalin.delete("/session",logoutHandler);
        //
        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        CreateGameHandler createGameHandler = new CreateGameHandler(createGameService);
        javalin.post("/game", createGameHandler);
        //
        ListGamesService listGamesService = new ListGamesService(gameDAO, authDAO);
        ListGamesHandler listGamesHandler = new ListGamesHandler(listGamesService);
        javalin.get("/game", listGamesHandler);
        //
        JoinGameService joinGameService = new JoinGameService(authDAO, gameDAO);
        JoinGameHandler joinGameHandler = new JoinGameHandler(joinGameService);
        javalin.put("/game", joinGameHandler);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
