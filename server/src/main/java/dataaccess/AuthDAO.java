package dataaccess;

import model.AuthData;
import service.UnauthorizedException;

public interface AuthDAO {
    void createAuth(AuthData a) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
    void verifyToken(String authToken, AuthData a) throws UnauthorizedException;
}
