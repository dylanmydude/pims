package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import model.Sale;
import model.SaleItem;
import utils.DBConnection;

public class SaleDAO {

    private static final String INSERT_SALE_SQL =
            "INSERT INTO sales (user_id, sale_date, total_amount) VALUES (?, ?, ?)";

    private static final String INSERT_SALE_ITEM_SQL =
            "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_MEDICINE_STOCK_SQL =
            "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? "
                    + "WHERE medicine_id = ? AND quantity_in_stock >= ?";

    private final DBConnection dbConnection;

    public SaleDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public void createSale(Sale sale, List<SaleItem> items) throws SQLException {
        Connection connection = null;
        PreparedStatement saleStatement = null;
        PreparedStatement saleItemStatement = null;
        PreparedStatement stockStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = dbConnection.getConnection();
            connection.setAutoCommit(false);

            saleStatement = connection.prepareStatement(INSERT_SALE_SQL, Statement.RETURN_GENERATED_KEYS);
            saleStatement.setInt(1, sale.getUser_id());
            saleStatement.setTimestamp(2, sale.getSale_date());
            saleStatement.setDouble(3, sale.getTotal_amount());
            saleStatement.executeUpdate();

            generatedKeys = saleStatement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to generate sale_id for the new sale.");
            }

            int saleId = generatedKeys.getInt(1);
            sale.setSale_id(saleId);

            saleItemStatement = connection.prepareStatement(INSERT_SALE_ITEM_SQL);
            stockStatement = connection.prepareStatement(UPDATE_MEDICINE_STOCK_SQL);

            for (SaleItem item : items) {
                item.setSale_id(saleId);

                saleItemStatement.setInt(1, saleId);
                saleItemStatement.setInt(2, item.getMedicine_id());
                saleItemStatement.setInt(3, item.getQuantity_sold());
                saleItemStatement.setDouble(4, item.getPrice_at_sale());
                saleItemStatement.executeUpdate();

                stockStatement.setInt(1, item.getQuantity_sold());
                stockStatement.setInt(2, item.getMedicine_id());
                stockStatement.setInt(3, item.getQuantity_sold());

                int updatedRows = stockStatement.executeUpdate();
                if (updatedRows == 0) {
                    throw new SQLException(
                            "Insufficient stock or invalid medicine_id for medicine_id=" + item.getMedicine_id()
                    );
                }
            }

            connection.commit();
        } catch (SQLException exception) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
            }
            throw exception;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException exception) {
                    System.err.println("Failed to restore auto-commit: " + exception.getMessage());
                }
            }

            dbConnection.closeResources(generatedKeys, saleStatement, null);
            dbConnection.closeResources(null, saleItemStatement, null);
            dbConnection.closeResources(null, stockStatement, connection);
        }
    }
}
