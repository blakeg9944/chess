package server;

import dataaccess.*;
import io.javalin.*;
import model.LoginResult;
import service.ClearService;
import service.LoginService;
import service.RegisterService;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

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
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
