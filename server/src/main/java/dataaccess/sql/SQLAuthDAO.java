package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;
import service.UnauthorizedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SQLAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        String statement = "INSERT INTO auth (authtoken, username) VALUES (?, ?)";
        try(Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, a.authToken());
            preparedStatement.setString(2, a.username());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT username, authtoken FROM auth WHERE authtoken = ?";
        try(Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authtoken=?";
        try(Connection connection = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "TRUNCATE TABLE auth";
        try(Connection connection = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    @Override
    public void verifyToken(String authToken, AuthData a) throws UnauthorizedException {
        try{
            if(getAuth(authToken) == null){
                throw new UnauthorizedException("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String authtoken = rs.getString("authtoken");
        return new AuthData(authtoken, username);
    }
}
