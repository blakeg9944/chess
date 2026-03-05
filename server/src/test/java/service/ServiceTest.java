package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;

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
