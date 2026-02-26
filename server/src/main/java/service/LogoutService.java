package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.LogoutRequest;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(LogoutRequest logoutRequest) throws Exception{
        AuthData authData = authDAO.getAuth(logoutRequest.authToken());
        isAuthorized(authData);
        authDAO.deleteAuth(logoutRequest.authToken());
    }

    private void isAuthorized(AuthData authData) throws Exception{
        if (authData == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

    }
}
