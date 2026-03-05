package dataaccess.memory;

import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import service.UnauthorizedException;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authDataHashMap = new HashMap<>();

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        authDataHashMap.put(a.authToken(), a);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDataHashMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDataHashMap.remove(authToken);

    }

    public void clear() throws DataAccessException {
        authDataHashMap.clear();
    }

    @Override
    public void verifyToken(String authToken, AuthData a) throws UnauthorizedException {
        if(a == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}
