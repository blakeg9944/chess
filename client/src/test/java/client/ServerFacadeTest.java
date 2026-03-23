package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTest {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clear() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerPositive() throws Exception {
        var result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void registerNegative() throws Exception{
        facade.register(new RegisterRequest("user", "password", "email@email.com"));
        Assertions.assertThrows(Exception.class, () -> {
            facade.register(new RegisterRequest("user", "password", "email@email.com"));
        });
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register(new RegisterRequest("user", "password", "email@email.com"));
        var result = facade.login(new LoginRequest("user", "password"));
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void loginNegative() throws Exception{
        Assertions.assertThrows(Exception.class, () -> {
            facade.login(new LoginRequest("user", "password"));
        });
    }

    @Test
    public void logoutPositive() throws Exception{
        RegisterResult res = facade.register(new RegisterRequest("user", "password", "email@email.com"));
        facade.logout(new LogoutRequest(res.authToken()));
        Assertions.assertThrows(Exception.class, () -> {
            facade.listGames(new ListGamesRequest(res.authToken()));
        });
    }

    @Test
    public void logoutNegative() throws Exception{
        Assertions.assertThrows(Exception.class, () -> {
            facade.logout(new LogoutRequest("fake"));
        });
    }

    @Test
    public void listGamesPositive() throws Exception{
        RegisterResult res = facade.register(new RegisterRequest("user", "password", "email@email.com"));
        String token = res.authToken();

        // 2. Create 3 different games
        facade.createGame(new CreateGameRequest("Game 1"), token);
        facade.createGame(new CreateGameRequest("Game 2"), token);
        facade.createGame(new CreateGameRequest("Game 3"), token);
        var result = facade.listGames(new ListGamesRequest(res.authToken()));
        Assertions.assertNotNull(result.games(), "The games collection should not be null");
        Assertions.assertEquals(3, result.games().size(), "Should have exactly 3 games");
    }

    @Test
    public void listGamesNegative() throws Exception{
        Assertions.assertThrows(Exception.class, () -> {
            facade.listGames(new ListGamesRequest("fake"));
        });
    }

    @Test
    public void createGamesPositive() throws  Exception{
        RegisterResult result = facade.register(new RegisterRequest("username", "pass", "not@email.com"));
        String token = result.authToken();
        facade.createGame(new CreateGameRequest("Game 1"), token);
        Assertions.assertNotNull(facade.listGames(new ListGamesRequest(result.authToken())));
        var list = facade.listGames(new ListGamesRequest(token));
        Assertions.assertEquals(1, list.games().size());
    }

    @Test
    public void createGamesNegative() throws  Exception{
        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame(new CreateGameRequest("Game"), "fake");
        });
    }

    @Test
    public void joinGamesPositive() throws  Exception{
        RegisterResult result = facade.register(new RegisterRequest("username", "pass", "not@email.com"));
        String token = result.authToken();
        CreateGameResult createGameResult = facade.createGame(new CreateGameRequest("Game 1"), token);
        facade.joinGame(new JoinGameRequest("WHITE", createGameResult.gameID()), token);
        var listResult = facade.listGames(new ListGamesRequest(token));

        // Find the game we just joined in the list
        var joinedGame = listResult.games().stream()
                .filter(g -> g.gameID() == createGameResult.gameID())
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(joinedGame);
        Assertions.assertEquals("username", joinedGame.whiteUsername());
        Assertions.assertNotNull(facade.listGames(new ListGamesRequest(result.authToken())));
    }

    @Test
    public void joinGamesNegative() throws  Exception{
        RegisterResult result = facade.register(new RegisterRequest("join", "join", "email@email.com"));
        String token = result.authToken();

        CreateGameResult game = facade.createGame(new CreateGameRequest("Game"), token);

        Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(new JoinGameRequest("WHITE", game.gameID()), "fakeToken");
        });
    }

    @Test
    public void clearPositive() throws Exception {
        facade.register(new RegisterRequest("clear", "clear", "clear@email.com"));
        facade.clear();

        Assertions.assertThrows(Exception.class, () -> {
            facade.login(new LoginRequest("clear", "clear"));
        });
    }

    @Test
    public void clearNegative() throws Exception {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }
}
