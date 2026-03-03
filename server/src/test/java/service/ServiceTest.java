package service;

import dataaccess.*;

public abstract class ServiceTest {

    protected UserDAO userDAO;
    protected AuthDAO authDAO;
    protected GameDAO gameDAO;

    protected RegisterService registerService;
    protected CreateGameService createGameService;
    protected JoinGameService joinGameService;
    protected ListGamesService listGamesService;

    protected void initialize() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        registerService = new RegisterService(userDAO, authDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO, gameDAO);
        listGamesService = new ListGamesService(gameDAO, authDAO);
    }
}
