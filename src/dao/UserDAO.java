package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.User;
import utils.DBConnection;

public class UserDAO {

    private static final String AUTHENTICATE_SQL =
            "SELECT user_id, username, password, full_name, role "
                    + "FROM users WHERE username = ? AND password = ? LIMIT 1";

    private static final String GET_ALL_USERS_SQL =
            "SELECT user_id, username, password, full_name, role FROM users ORDER BY user_id";

    private static final String ADD_USER_SQL =
            "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_USER_SQL =
            "UPDATE users SET full_name = ?, role = ? WHERE user_id = ?";

    private static final String DELETE_USER_SQL =
            "DELETE FROM users WHERE user_id = ?";

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

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_ALL_USERS_SQL);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }

            return users;
        } finally {
            dbConnection.closeResources(resultSet, preparedStatement, connection);
        }
    }

    public void addUser(User user) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(ADD_USER_SQL);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFull_name());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeResources(null, preparedStatement, connection);
        }
    }

    public void deleteUser(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_USER_SQL);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeResources(null, preparedStatement, connection);
        }
    }

    public void updateUser(User user) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_USER_SQL);
            preparedStatement.setString(1, user.getFull_name());
            preparedStatement.setString(2, user.getRole());
            preparedStatement.setInt(3, user.getUser_id());
            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeResources(null, preparedStatement, connection);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUser_id(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setFull_name(resultSet.getString("full_name"));
        user.setRole(resultSet.getString("role"));
        return user;
    }
}
