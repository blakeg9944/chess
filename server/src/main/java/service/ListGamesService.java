package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import model.AuthData;
import model.ListGamesRequest;
import model.ListGamesResult;

public class ListGamesService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws Exception {
        AuthData authData = authDAO.getAuth(listGamesRequest.authToken());
        authDAO.verifyToken(listGamesRequest.authToken(), authData);
        return new ListGamesResult(gameDAO.listGames());
    }
}
