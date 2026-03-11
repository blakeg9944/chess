package service;

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
    public void loginSuccess() throws Exception {
        // CRITICAL: Wipe the database first
        userDAO.clear();
        authDAO.clear();

        RegisterRequest registerRequest =
                new RegisterRequest("username", "password", "email@test.com");
        registerService.register(registerRequest);

        // 2. Attempt login
        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult loginResult = loginService.login(loginRequest);

        // 3. Verify
        assertNotNull(loginResult);
        assertEquals("username", loginResult.username());
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
