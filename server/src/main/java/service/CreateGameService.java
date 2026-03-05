package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import model.*;

public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws Exception {
        validateRequest(createGameRequest, authToken);
        AuthData authData = authDAO.getAuth(authToken);
        authDAO.verifyToken(authToken, authData );
        int gameID = gameDAO.createGame(createGameRequest.gameName());
        return new CreateGameResult(gameID);
    }

    private void validateRequest (CreateGameRequest createGameRequest, String authToken) throws Exception {
        if (createGameRequest.gameName() == null || authToken == null){
            throw new BadRequestException("Error: bad request");
        }
    }
}
