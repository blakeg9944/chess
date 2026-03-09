package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private int sqlGameInt = 1;
    private String gameJson = new Gson().toJson(new ChessGame());

    @Override
    public int createGame(String gameName) throws DataAccessException {
        sqlGameInt += 1;
        String statement = "INSERT INTO games(gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
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
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?";
        try(Connection connect = DatabaseManager.getConnection()) {
            try(PreparedStatement preparedStatement = connect.prepareStatement(statement)){
                preparedStatement.setInt(1, gameID);
               try(ResultSet rs = preparedStatement.executeQuery()){
                   if (rs.next()){
                       return readGame(rs);
                   }
                   return null;
               }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> endList = new ArrayList<>();
        try(Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                try(ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        endList.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endList;
    }

    @Override
    public void updateGame(GameData g) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName =rs.getString("gameName");
        var jsonGame = rs.getString("game");
        var chessGame = new Gson().fromJson(jsonGame, chess.ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);

    }
}
