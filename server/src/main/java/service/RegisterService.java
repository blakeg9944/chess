package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

import java.util.UUID;


public class RegisterService{

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception{
        validateRequest(registerRequest);
        UserData existingUser = userDAO.getUser(registerRequest.username());
        if (existingUser != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        UserData newUserData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(newUserData);
        String authToken = generateToken();
        AuthData newAuthData = new AuthData(authToken, registerRequest.username());
        authDAO.createAuth(newAuthData);
        return new RegisterResult(registerRequest.username(), authToken);
    }

    private void validateRequest(RegisterRequest registerRequest) throws BadRequestException {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
    }

}
