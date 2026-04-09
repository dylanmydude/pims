package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import model.Role;
import model.User;
import utils.DBConnection;

public class UserDAO {

    private static final String AUTHENTICATE_SQL =
            "SELECT user_id, username, password, full_name, role, is_active, created_at "
                    + "FROM users WHERE username = ? AND password = ? AND is_active = TRUE LIMIT 1";

    private final DBConnection dbConnection;

    public UserDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public User authenticate(String username, String password) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(AUTHENTICATE_SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            return mapUser(resultSet);
        } finally {
            dbConnection.closeResources(resultSet, preparedStatement, connection);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setFullName(resultSet.getString("full_name"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        user.setActive(resultSet.getBoolean("is_active"));
        user.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        return user;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }
}
