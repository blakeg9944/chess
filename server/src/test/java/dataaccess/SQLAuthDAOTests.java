package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import service.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {

    private SQLAuthDAO authDAO;

    @BeforeEach
    public void setup() throws Exception {
        DatabaseManager.createDatabase();
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    @DisplayName("createAuth positive")
    public void createAuthPositive() throws Exception {
        AuthData auth = new AuthData("authToken", "user");

        authDAO.createAuth(auth);

        AuthData create = authDAO.getAuth("authToken");

        assertNotNull(create);
        assertEquals("user", create.username());
    }

    @Test
    @DisplayName("createAuth negative")
    public void createAuthDuplicate() throws Exception {
        AuthData authData = new AuthData("authToken", "user");

        authDAO.createAuth(authData);

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(authData);
        });
    }

    @Test
    @DisplayName("getAuth positive")
    public void getAuthPositive() throws Exception {
        AuthData authData = new AuthData("authToken", "user");

        authDAO.createAuth(authData);

        AuthData authData1 = authDAO.getAuth("authToken");

        assertNotNull(authData1);
        assertEquals("user", authData1.username());
        assertEquals("authToken", authData1.authToken());
    }

    @Test
    @DisplayName("getAuth negative")
    public void getAuthNegative() throws Exception {
        AuthData result = authDAO.getAuth("badToken");

        assertNull(result);
    }



    @Test
    @DisplayName("deleteAuth positive")
    public void deleteAuthPositive() throws Exception {
        AuthData auth = new AuthData("authToken", "user");

        authDAO.createAuth(auth);

        authDAO.deleteAuth("authToken");

        AuthData result = authDAO.getAuth("tokenDelete");

        assertNull(result);
    }

    @Test
    @DisplayName("deleteAuth nonexistent token negative")
    public void deleteAuthNegative() {
        assertDoesNotThrow(() -> {
            authDAO.deleteAuth("authToken");
        });
    }

    @Test
    @DisplayName("clear positive")
    public void clearPositive() throws Exception {
        authDAO.createAuth(new AuthData("authToken1", "user1"));
        authDAO.createAuth(new AuthData("authToken2", "user2"));

        authDAO.clear();

        assertNull(authDAO.getAuth("authToken1"));
        assertNull(authDAO.getAuth("authToken2"));
    }

    @Test
    @DisplayName("clear empty table")
    public void clearEmpty() throws Exception {
        authDAO.clear();

        AuthData result = authDAO.getAuth("anything");

        assertNull(result);
    }
}