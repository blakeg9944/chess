package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JoinGameServiceTest extends ServiceTest {


    @BeforeEach
    public void setup() {
        initialize();
    }

    @Test
    @DisplayName("Join Game Sucess")
    public void joinGameSetup() throws Exception{
        RegisterRequest registerRequest =
                new RegisterRequest("username", "password", "email@test.com");
        RegisterResult registerResult =
                registerService.register(registerRequest);

        String authToken = registerResult.authToken();

        CreateGameResult createResult =
                createGameService.createGame(
                        new CreateGameRequest("Game1"), authToken);

        int gameID = createResult.gameID();

        // Join game as WHITE
        JoinGameRequest joinRequest =
                new JoinGameRequest("WHITE", gameID);

        joinGameService.joinGame(joinRequest, authToken);

        // Verify user was added to game
        GameData game = gameDAO.getGame(gameID);
        assertEquals("username", game.whiteUsername());
    }

    @Test
    @DisplayName("Join Game Failure")
    public void joinGameFailure() throws Exception{

        JoinGameRequest request = new JoinGameRequest("WHITE", 1);
        assertThrows(Exception.class, () -> {
            joinGameService.joinGame(request, "invlaid");
        });

    }
}
