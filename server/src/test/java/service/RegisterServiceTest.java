package service;

import dataaccess.*;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest extends ServiceTest {

        @BeforeEach
        public void setup(){
            initialize();
        }

        @Test
        @DisplayName("Register Success")
        public void setRegisterService() throws Exception{
            RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
            model.RegisterResult registerResult = registerService.register(registerRequest);

            assertNotNull(registerResult.authToken(), "Auth token needs to be generated");
            assertEquals("username", registerResult.username());

        }
        @Test
        @DisplayName("Register Failure")
        public void registerFailure() throws Exception{
            RegisterRequest request = new RegisterRequest("username", "password", null);
            assertThrows(Exception.class, () -> {
                registerService.register(request);
            });
        }
}
