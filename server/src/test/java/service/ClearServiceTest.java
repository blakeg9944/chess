package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ClearServiceTest extends ServiceTest {
    private ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    @BeforeEach
    public void setup(){
        initialize();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }


    @Test
    @DisplayName("Clear Success")
    public void setClearService() throws DataAccessException{
        userDAO.createUser(new UserData("username", "password", "email@email.com"));

        clearService.clear();

        Assertions.assertNull(userDAO.getUser("username"), "Database needs to be empty after clear");
    }


}
