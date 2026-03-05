package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;

public class JoinGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws Exception {
        GameData oldgameData = gameDAO.getGame(joinGameRequest.gameID());
        if (oldgameData == null) {
            throw new BadRequestException("Error: bad request");
        }
        AuthData authData = authDAO.getAuth(authToken);
        authDAO.verifyToken(authToken, authData);
        String color = joinGameRequest.playerColor();
        if (color == null || (!color.equals("WHITE") && !color.equals("BLACK"))){
            throw new BadRequestException("Error: bad request");
        }
        else if ("WHITE".equals(color) && oldgameData.whiteUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        else if ("BLACK".equals(color) && oldgameData.blackUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        GameData newgameData = new GameData(oldgameData.gameID(),
                (color.equals("WHITE") ? authData.username() : oldgameData.whiteUsername()),
                (color.equals("BLACK") ? authData.username() : oldgameData.blackUsername()),
                oldgameData.gameName(), oldgameData.game());
        gameDAO.updateGame(newgameData);

    }
}
