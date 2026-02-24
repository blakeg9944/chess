package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest loginRequest) throws Exception{
        validateRequest(loginRequest);
        UserData userData = userDAO.getUser(loginRequest.username());
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, userData.username());
        authDAO.createAuth(authData);
        return new LoginResult(userData.username(), authToken);
    }

    private void validateRequest (LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.password() == null || loginRequest.username() == null
                || userDAO.getUser(loginRequest.username()) == null){
            throw new DataAccessException("Error: bad request");
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

}
