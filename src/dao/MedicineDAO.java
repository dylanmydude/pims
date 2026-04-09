package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Medicine;
import utils.DBConnection;

public class MedicineDAO {

    private static final String GET_ALL_MEDICINES_SQL =
            "SELECT medicine_id, name, company, medicine_type, price, quantity_in_stock, "
                    + "reorder_level, expiry_date, supplier_id FROM medicines ORDER BY medicine_id";

    private static final String ADD_MEDICINE_SQL =
            "INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, "
                    + "reorder_level, expiry_date, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_MEDICINE_SQL =
            "UPDATE medicines SET name = ?, company = ?, medicine_type = ?, price = ?, "
                    + "quantity_in_stock = ?, reorder_level = ?, expiry_date = ?, supplier_id = ? "
                    + "WHERE medicine_id = ?";

    private static final String DELETE_MEDICINE_SQL =
            "DELETE FROM medicines WHERE medicine_id = ?";

    private static final String SUPPLIER_EXISTS_SQL =
            "SELECT 1 FROM suppliers WHERE supplier_id = ? LIMIT 1";

    private final DBConnection dbConnection;

    public MedicineDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public List<Medicine> getAllMedicines() throws SQLException {
        List<Medicine> medicines = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(GET_ALL_MEDICINES_SQL);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                medicines.add(mapMedicine(resultSet));
            }

            return medicines;
        } finally {
            dbConnection.closeResources(resultSet, preparedStatement, connection);
        }
    }

    public void addMedicine(Medicine medicine) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(ADD_MEDICINE_SQL);
            bindMedicine(preparedStatement, medicine);
            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeResources(null, preparedStatement, connection);
        }
    }

    public void updateMedicine(Medicine medicine) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_MEDICINE_SQL);
            bindMedicine(preparedStatement, medicine);
            preparedStatement.setInt(9, medicine.getMedicine_id());
            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeResources(null, preparedStatement, connection);
        }
    }

    public void deleteMedicine(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_MEDICINE_SQL);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeResources(null, preparedStatement, connection);
        }
    }

    public boolean supplierExists(int supplierId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getConnection();
            preparedStatement = connection.prepareStatement(SUPPLIER_EXISTS_SQL);
            preparedStatement.setInt(1, supplierId);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } finally {
            dbConnection.closeResources(resultSet, preparedStatement, connection);
        }
    }

    private void bindMedicine(PreparedStatement preparedStatement, Medicine medicine) throws SQLException {
        preparedStatement.setString(1, medicine.getName());
        preparedStatement.setString(2, medicine.getCompany());
        preparedStatement.setString(3, medicine.getMedicine_type());
        preparedStatement.setDouble(4, medicine.getPrice());
        preparedStatement.setInt(5, medicine.getQuantity_in_stock());
        preparedStatement.setInt(6, medicine.getReorder_level());
        preparedStatement.setDate(7, medicine.getExpiry_date());
        preparedStatement.setInt(8, medicine.getSupplier_id());
    }

    private Medicine mapMedicine(ResultSet resultSet) throws SQLException {
        Medicine medicine = new Medicine();
        medicine.setMedicine_id(resultSet.getInt("medicine_id"));
        medicine.setName(resultSet.getString("name"));
        medicine.setCompany(resultSet.getString("company"));
        medicine.setMedicine_type(resultSet.getString("medicine_type"));
        medicine.setPrice(resultSet.getDouble("price"));
        medicine.setQuantity_in_stock(resultSet.getInt("quantity_in_stock"));
        medicine.setReorder_level(resultSet.getInt("reorder_level"));
        medicine.setExpiry_date(resultSet.getDate("expiry_date"));
        medicine.setSupplier_id(resultSet.getInt("supplier_id"));
        return medicine;
    }
}
