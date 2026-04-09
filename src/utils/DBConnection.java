package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/pims_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "pims_user";
    private static final String PASSWORD = "pims123";

    private static volatile DBConnection instance;

    private DBConnection() {
        // Singleton constructor.
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            ensureDriverLoaded();
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException exception) {
            throw new SQLException("Failed to connect to the MySQL database.", exception);
        }
    }

    private void ensureDriverLoaded() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException exception) {
            throw new SQLException(
                    "MySQL JDBC driver not found. Add mysql-connector-j to the application classpath.",
                    exception
            );
        }
    }

    public void closeConnection(Connection connection) {
        closeQuietly(connection);
    }

    public void closeStatement(Statement statement) {
        closeQuietly(statement);
    }

    public void closeResultSet(ResultSet resultSet) {
        closeQuietly(resultSet);
    }

    public void closeResources(ResultSet resultSet, Statement statement, Connection connection) {
        closeResultSet(resultSet);
        closeStatement(statement);
        closeConnection(connection);
    }

    private void closeQuietly(AutoCloseable resource) {
        if (resource == null) {
            return;
        }

        try {
            resource.close();
        } catch (Exception exception) {
            System.err.println("Failed to close database resource: " + exception.getMessage());
        }
    }
}
