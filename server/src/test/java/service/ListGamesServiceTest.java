package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private RegisterService registerService;
    private ListGamesService listGamesService;
    private CreateGameService createGameService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        registerService = new RegisterService(userDAO, authDAO);
        listGamesService = new ListGamesService(gameDAO, authDAO);
        createGameService = new CreateGameService(authDAO, gameDAO);

    }

    @Test
    @DisplayName("List Games Success")
    void ListGamesSuccess() throws Exception {
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
    void ListGamesFailure() throws Exception {

        ListGamesRequest request = new ListGamesRequest("invalid");

        assertThrows(Exception.class, () -> {
            listGamesService.listGames(request);
        });


    }
}
