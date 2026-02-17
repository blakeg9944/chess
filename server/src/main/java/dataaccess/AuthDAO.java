package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void creatAuth() throws DataAccessException;
    AuthData getAuth() throws DataAccessException;
    void deleteAuth() throws DataAccessException;
}
