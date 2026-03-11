package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {

    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setup() throws Exception {
        DatabaseManager.createDatabase();
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    // -------------------------
    // createGame Tests
    // -------------------------

    @Test
    @DisplayName("createGame positive")
    public void createGamePositive() throws Exception {
        int gameID = gameDAO.createGame("gameName");

        GameData game = gameDAO.getGame(gameID);

        assertNotNull(game);
        assertEquals("gameName", game.gameName());
    }

    @Test
    @DisplayName("createGame null name negative")
    public void createGameNullName() {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null);
        });
    }

    @Test
    @DisplayName("getGame positive")
    public void getGamePositive() throws Exception {
        int id = gameDAO.createGame("game");

        GameData result = gameDAO.getGame(id);

        assertNotNull(result);
        assertEquals(id, result.gameID());
    }

    @Test
    @DisplayName("getGame negative")
    public void getGameNegative() throws Exception {
        GameData result = gameDAO.getGame(67);
        assertNull(result);
    }


    @Test
    @DisplayName("listGames positive")
    public void listGamesPositive() throws Exception {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");

        Collection<GameData> games = gameDAO.listGames();

        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("listGames empty table")
    public void listGamesEmpty() throws Exception {
        Collection<GameData> games = gameDAO.listGames();

        assertTrue(games.isEmpty());
    }


    @Test
    @DisplayName("updateGame positive")
    public void updateGamePositive() throws Exception {
        int id = gameDAO.createGame("game");

        GameData updatedGame = new GameData(
                id,
                "whiteUser",
                "blackUser",
                "newName",
                new ChessGame()
        );

        gameDAO.updateGame(updatedGame);

        GameData updated= gameDAO.getGame(id);

        assertEquals("whiteUser", updated.whiteUsername());
        assertEquals("blackUser", updated.blackUsername());
        assertEquals("newName", updated.gameName());
    }

    @Test
    @DisplayName("updateGame nonexistent negative")
    public void updateGameNegative() {
        GameData negName = new GameData(
                9999,
                "white",
                "black",
                "negName",
                new ChessGame()
        );

        assertDoesNotThrow(() -> gameDAO.updateGame(negName));
    }

    @Test
    @DisplayName("clear positive")
    public void clearPositive() throws Exception {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");

        gameDAO.clear();

        Collection<GameData> games = gameDAO.listGames();

        assertTrue(games.isEmpty());
    }
}