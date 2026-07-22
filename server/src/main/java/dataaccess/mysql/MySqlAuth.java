package dataaccess.mysql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import server.ResponseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuth extends MySqlParent implements AuthDAO {

    public MySqlAuth() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public void createAuth(String authToken, String username) throws ResponseException, DataAccessException {
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, username, authToken);
    }

    public void deleteAll() throws ResponseException, DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM auth")) {

            ps.executeUpdate();

        }
    }

    public AuthData getAuthData(String authToken) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(String.format("Error: Unable to read data: %s", e.getMessage()), 500);
        }
        return null;
    }

    public void deleteAuth(String authToken) throws ResponseException, DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` VARCHAR(255) NOT NULL,
              `username` VARCHAR(255) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var authToken = rs.getString("authToken");
        return new AuthData(authToken, username);
    }

    @Override
    protected String[] getCreateStatements() {
        return new String[]{
                """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` VARCHAR(255) NOT NULL,
              `username` VARCHAR(255) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
    }
}
