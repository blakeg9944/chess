package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;

public abstract class ServiceTest {

    protected UserDAO userDAO;
    protected AuthDAO authDAO;
    protected GameDAO gameDAO;

    protected RegisterService registerService;
    protected CreateGameService createGameService;
    protected JoinGameService joinGameService;
    protected ListGamesService listGamesService;

    protected void initialize() {
        userDAO =  new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();

        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (Exception e) {
            System.out.println("Error clearing database: " + e.getMessage());
        }

        registerService = new RegisterService(userDAO, authDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);
        joinGameService = new JoinGameService(authDAO, gameDAO);
        listGamesService = new ListGamesService(gameDAO, authDAO);
    }
}
