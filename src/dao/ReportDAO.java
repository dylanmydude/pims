package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import utils.DBConnection;

public class ReportDAO {

    private static final String SALES_REPORT_SQL =
            "SELECT DATE(sale_date) AS sale_day, SUM(total_amount) AS total_sales "
                    + "FROM sales GROUP BY DATE(sale_date) ORDER BY sale_day DESC";

    private static final String ITEM_WISE_SALES_REPORT_SQL =
            "SELECT m.medicine_id, m.name, SUM(si.quantity_sold) AS total_quantity_sold "
                    + "FROM sale_items si "
                    + "INNER JOIN medicines m ON si.medicine_id = m.medicine_id "
                    + "GROUP BY m.medicine_id, m.name "
                    + "ORDER BY total_quantity_sold DESC, m.name ASC";

    private static final String LOW_STOCK_REPORT_SQL =
            "SELECT medicine_id, name, quantity_in_stock, reorder_level "
                    + "FROM medicines "
                    + "WHERE quantity_in_stock <= reorder_level "
                    + "ORDER BY quantity_in_stock ASC, name ASC";

    private static final String EXPIRY_REPORT_SQL =
            "SELECT medicine_id, name, expiry_date, quantity_in_stock "
                    + "FROM medicines "
                    + "WHERE expiry_date BETWEEN ? AND ? "
                    + "ORDER BY expiry_date ASC, name ASC";

    private final DBConnection dbConnection;

    public ReportDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public List<Object[]> getSalesReport() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(SALES_REPORT_SQL);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                rows.add(new Object[] {
                        resultSet.getDate("sale_day"),
                        resultSet.getDouble("total_sales")
                });
            }

            return rows;
        } finally {
            dbConnection.closeResources(resultSet, preparedStatement, connection);
        }
    }

    public List<Object[]> getItemWiseSalesReport() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(ITEM_WISE_SALES_REPORT_SQL);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                rows.add(new Object[] {
                        resultSet.getInt("medicine_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("total_quantity_sold")
                });
            }

            return rows;
        } finally {
            dbConnection.closeResources(resultSet, preparedStatement, connection);
        }
    }

    public List<Object[]> getLowStockReport() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(LOW_STOCK_REPORT_SQL);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                rows.add(new Object[] {
                        resultSet.getInt("medicine_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity_in_stock"),
                        resultSet.getInt("reorder_level")
                });
            }

            return rows;
        } finally {
            dbConnection.closeResources(resultSet, preparedStatement, connection);
        }
    }

    public List<Object[]> getExpiryReport() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(EXPIRY_REPORT_SQL);
            preparedStatement.setDate(1, Date.valueOf(today));
            preparedStatement.setDate(2, Date.valueOf(thirtyDaysFromNow));
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                rows.add(new Object[] {
                        resultSet.getInt("medicine_id"),
                        resultSet.getString("name"),
                        resultSet.getDate("expiry_date"),
                        resultSet.getInt("quantity_in_stock")
                });
            }

            return rows;
        } finally {
            dbConnection.closeResources(resultSet, preparedStatement, connection);
        }
    }
}
