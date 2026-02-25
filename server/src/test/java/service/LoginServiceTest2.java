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

public class LoginServiceTest2 {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private LoginService loginService;
    private RegisterService registerService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        loginService = new LoginService(userDAO, authDAO);
        registerService = new RegisterService(userDAO, authDAO);
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
    @DisplayName("Login Fail - Wrong Password")
    public void loginFailWrongPassword() {

        // Register correct user
        RegisterRequest registerRequest =
                new RegisterRequest("username", "password", "email@test.com");

        try {
            registerService.register(registerRequest);
        } catch (Exception ignored) {}

        // Attempt login with wrong password
        LoginRequest loginRequest =
                new LoginRequest("username", "wrongPassword");

        assertThrows(Exception.class, () -> {
            loginService.login(loginRequest);
        });
    }
}
