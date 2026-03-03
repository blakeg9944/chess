package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest extends ServiceTest {

    private LoginService loginService;

    @BeforeEach
    public void setup() {
        initialize();
        loginService = new LoginService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() throws Exception {

        // First register a user
        RegisterRequest registerRequest =
                new RegisterRequest("username", "password", "email@test.com");
        RegisterResult registerResult =
                registerService.register(registerRequest);

        assertNotNull(registerResult);

        // Now attempt login
        LoginRequest loginRequest =
                new LoginRequest("username", "password");
        LoginResult loginResult =
                loginService.login(loginRequest);

        assertNotNull(loginResult);
        assertEquals("username", loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    @DisplayName("Login Failure")
    public void loginFailure() throws Exception{
        LoginRequest request = new LoginRequest("username", null);
        assertThrows(Exception.class, () -> {
            loginService.login(request);
        });
    }
}
