package service;

import dataaccess.*;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RegisterServiceTest {

        private UserDAO userDAO = new MemoryUserDAO();
        private AuthDAO authDAO = new MemoryAuthDAO();
        private RegisterService registerService = new RegisterService(userDAO, authDAO);

        @BeforeEach
        public void setup(){
            registerService = new RegisterService(userDAO, authDAO);
        }

        @Test
        @DisplayName("Register Success")
        public void setRegisterService() throws Exception{
            RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
            model.RegisterResult registerResult = registerService.register(registerRequest);

            assertNotNull(registerResult.authToken(), "Auth token needs to be generated");
            assertEquals("username", registerResult.username());

        }
}
