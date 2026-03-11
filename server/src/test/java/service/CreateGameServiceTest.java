package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest extends ServiceTest {
        @BeforeEach
        public void setup() {
            initialize();

        }

        @Test
        @DisplayName("Create Game Success")
        void createGameSuccess() throws Exception {
            RegisterRequest registerRequest =
                    new RegisterRequest("username", "password", "email@test.com");
            RegisterResult registerResult =
                    registerService.register(registerRequest);

            String authToken = registerResult.authToken();

            CreateGameResult result =
                    createGameService.createGame(
                            new CreateGameRequest("Game1"), authToken);


            assertNotNull(result);
            assertTrue(result.gameID() > 0);

            // Verify game actually exists in DAO
            GameData storedGame = gameDAO.getGame(result.gameID());
            assertNotNull(storedGame);
            assertEquals("Game1", storedGame.gameName());



        }

        @Test
        @DisplayName("Create Game Failure")
        void createGameFailure() throws Exception {

            CreateGameRequest request = new CreateGameRequest("game1");
            assertThrows(Exception.class, () -> {
                createGameService.createGame(request, "invalid");

            });
        }
}
