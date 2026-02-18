package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();


    @Override
    public void createGame(GameData g) throws DataAccessException {
        games.put(g.gameID(), g);
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(GameData g) throws DataAccessException {
        games.put(g.gameID(), g);

    }

    public void clear() throws DataAccessException {
        games.clear();
    }
}
