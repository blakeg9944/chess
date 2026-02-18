package dataaccess;

import model.AuthData;

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
    public void deleteAuth() throws DataAccessException {
        authDataHashMap.clear();

    }
}
