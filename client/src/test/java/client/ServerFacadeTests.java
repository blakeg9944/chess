import client.ServerFacade;
import model.LoginRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

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
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void registerPositive() throws Exception {
        var result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    void registerNegative() throws Exception{
        facade.register(new RegisterRequest("user", "password", "email@email.com"));
        Assertions.assertThrows(Exception.class, () -> {
            facade.register(new RegisterRequest("user", "password", "email@email.com"));
        });
    }

    @Test
    void loginPositive() throws Exception {
        facade.register(new RegisterRequest("user", "password", "email@email.com"));
        var result = facade.login(new LoginRequest("user", "password"));
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    void loginNegative() throws Exception{
        Assertions.assertThrows(Exception.class, () -> {
            facade.login(new LoginRequest("user", "password"));
        });
    }


}
