package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private int sqlGameInt = 1;
    private String gameJson = new Gson().toJson(new ChessGame());

    @Override
    public int createGame(String gameName) throws DataAccessException {
        sqlGameInt += 1;
        String statement = "INSERT INTO game(gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try(Connection con = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = con.prepareStatement(statement);
            preparedStatement.setInt(1, sqlGameInt);
            preparedStatement.setString(2, null);
            preparedStatement.setString(3, null);
            preparedStatement.setString(4, gameName);
            preparedStatement.setString(5, gameJson);
            preparedStatement.executeUpdate();
            return sqlGameInt;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
