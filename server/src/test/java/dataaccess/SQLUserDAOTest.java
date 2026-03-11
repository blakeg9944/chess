package dataaccess;

import dataaccess.interfaces.UserDAO;
import dataaccess.sql.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTest {
    private final UserDAO userDAO = new SQLUserDAO();


    @BeforeEach
    void setup() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    @DisplayName("Create User Successfully")
    void createUserSuccess() throws DataAccessException {
        UserData newUser = new UserData("user", "password", "email");

        userDAO.createUser(newUser);

        UserData user = userDAO.getUser("user");

        assertNotNull(user, "User should be found in the database.");
        assertEquals("user", user.username());
    }

    @Test
    @DisplayName("Negative: Create Duplicate User")
    void createUserDuplicate() throws DataAccessException {
        UserData firstUser = new UserData("duplicate_me", "pass1", "one@test.com");
        UserData repeatUser = new UserData("duplicate_me", "pass2", "two@test.com");

        userDAO.createUser(firstUser);

        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(repeatUser);
        }, "should throw a DataAccessException");
    }

    @Test
    @DisplayName("Get User Successfully")
    void getUserSuccess() throws DataAccessException {
        UserData newUser = new UserData("user", "password", "email");

        userDAO.createUser(newUser);

        UserData user = userDAO.getUser("user");

        assertNotNull(user, "User should be found in the database.");
        assertEquals("user", user.username());
    }

    @Test
    @DisplayName("Negative: Create Duplicate User")
    void getUserDuplicate() throws DataAccessException {
        UserData firstUser = new UserData("duplicate_me", "pass1", "one@test.com");
        UserData repeatUser = new UserData("duplicate_me", "pass2", "two@test.com");

        userDAO.createUser(firstUser);

        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(repeatUser);
        }, "should throw a DataAccessException");
    }

    @Test
    @DisplayName("Positive: clear user")
    void clearUserSuccess() throws DataAccessException {
        UserData newUser = new UserData("user", "password", "email");
        UserData newUser1 = new UserData("user1", "password1", "email1");

        userDAO.clear();

        assertNull(userDAO.getUser("user"), "Database should be empty after clear()");
        assertNull(userDAO.getUser("user1"), "Database should be empty after clear()");

    }

}
