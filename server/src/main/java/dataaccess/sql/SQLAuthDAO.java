package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class SQLAuthDAO implements GameDAO {
    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData g) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
