package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame() throws DataAccessException;
    GameData getGame() throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame() throws DataAccessException;



}
