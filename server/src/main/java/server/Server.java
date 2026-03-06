package server;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.sql.SQLUserDAO;
import io.javalin.*;
import model.ErrorResponse;
import server.handler.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new SQLUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
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
