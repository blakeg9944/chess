package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest extends ServiceTest {

    private LogoutService logoutService;

    @BeforeEach
    public void setup() {
        initialize();
        logoutService = new LogoutService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() throws Exception {

        // First register a user
        RegisterRequest registerRequest =
                new RegisterRequest("username", "password", "email@test.com");
        RegisterResult registerResult =
                registerService.register(registerRequest);

        assertNotNull(registerResult);

        // Now attempt login
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        logoutService.logout(logoutRequest);

        assertNull(authDAO.getAuth(registerResult.authToken()));
    }

    @Test
    @DisplayName("Logout Fail - Bad Auth Token")
    public void logoutFailWrongAuthToken() {

        LogoutRequest logoutRequest = new LogoutRequest("invalid-token");

        assertThrows(Exception.class, () -> {
            logoutService.logout(logoutRequest);
        });
    }
}
