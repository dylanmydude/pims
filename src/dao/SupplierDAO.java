package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Supplier;
import utils.DBConnection;

public class SupplierDAO {

    private static final String GET_ALL_SUPPLIERS_SQL =
            "SELECT supplier_id, name, contact_person, phone, email, address "
                    + "FROM suppliers ORDER BY supplier_id";

    private static final String ADD_SUPPLIER_SQL =
            "INSERT INTO suppliers (name, contact_person, phone, email, address) "
                    + "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_SUPPLIER_SQL =
            "UPDATE suppliers SET name = ?, contact_person = ?, phone = ?, email = ?, address = ? "
                    + "WHERE supplier_id = ?";

    private static final String DELETE_SUPPLIER_SQL =
            "DELETE FROM suppliers WHERE supplier_id = ?";

    private final DBConnection dbConnection;

    public SupplierDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_ALL_SUPPLIERS_SQL);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                suppliers.add(mapSupplier(resultSet));
            }

            return suppliers;
        } finally {
            dbConnection.closeResources(resultSet, preparedStatement, connection);
        }
    }

    public void addSupplier(Supplier supplier) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(ADD_SUPPLIER_SQL);
            bindSupplier(preparedStatement, supplier);
            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeResources(null, preparedStatement, connection);
        }
    }

    public void updateSupplier(Supplier supplier) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_SUPPLIER_SQL);
            bindSupplier(preparedStatement, supplier);
            preparedStatement.setInt(6, supplier.getSupplier_id());
            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeResources(null, preparedStatement, connection);
        }
    }

    public void deleteSupplier(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_SUPPLIER_SQL);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeResources(null, preparedStatement, connection);
        }
    }

    private void bindSupplier(PreparedStatement preparedStatement, Supplier supplier) throws SQLException {
        preparedStatement.setString(1, supplier.getName());
        preparedStatement.setString(2, supplier.getContact_person());
        preparedStatement.setString(3, supplier.getPhone());
        preparedStatement.setString(4, supplier.getEmail());
        preparedStatement.setString(5, supplier.getAddress());
    }

    private Supplier mapSupplier(ResultSet resultSet) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplier_id(resultSet.getInt("supplier_id"));
        supplier.setName(resultSet.getString("name"));
        supplier.setContact_person(resultSet.getString("contact_person"));
        supplier.setPhone(resultSet.getString("phone"));
        supplier.setEmail(resultSet.getString("email"));
        supplier.setAddress(resultSet.getString("address"));
        return supplier;
    }
}
