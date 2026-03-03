package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest extends ServiceTest {

    @BeforeEach
    public void setup() {
        initialize();

    }

    @Test
    @DisplayName("List Games Success")
    void listGamesSuccess() throws Exception {
        RegisterRequest registerRequest =
                new RegisterRequest("username", "password", "email@test.com");
        RegisterResult registerResult =
                registerService.register(registerRequest);

        String authToken = registerResult.authToken();

        createGameService.createGame(new CreateGameRequest("Game1"), authToken);
        createGameService.createGame(new CreateGameRequest("Game2"), authToken);

        ListGamesResult listGamesResult = listGamesService.listGames(new ListGamesRequest(authToken));

        assertNotNull(listGamesResult);
        assertEquals(2, listGamesResult.games().size());


    }

    @Test
    @DisplayName("List Games Failure")
    void listGamesFailure() throws Exception {

        ListGamesRequest request = new ListGamesRequest("invalid");

        assertThrows(Exception.class, () -> {
            listGamesService.listGames(request);
        });


    }
}
