package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.LoginRequest;
import model.LoginResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginServiceTest {
    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();
    private LoginService loginService = new LoginService(userDAO, authDAO);
    private RegisterService registerService = new RegisterService(userDAO, authDAO);

    @BeforeEach
    public void setup(){
        loginService = new LoginService(userDAO, authDAO);
        registerService = new RegisterService(userDAO, authDAO);
    }

    @Test
    @DisplayName("LoginServiceTests")
    public void setLoginService() throws Exception{
        LoginRequest loginRequest = new LoginRequest("username", "password");
        LoginResult loginResult = loginService.login(loginRequest);
    }

}
