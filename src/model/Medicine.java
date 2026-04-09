package model;

import java.sql.Date;

public class Medicine {

    private int medicine_id;
    private String name;
    private String company;
    private String medicine_type;
    private double price;
    private int quantity_in_stock;
    private int reorder_level;
    private Date expiry_date;
    private int supplier_id;

    public Medicine() {
        // Default constructor.
    }

    public Medicine(
            int medicine_id,
            String name,
            String company,
            String medicine_type,
            double price,
            int quantity_in_stock,
            int reorder_level,
            Date expiry_date,
            int supplier_id
    ) {
        this.medicine_id = medicine_id;
        this.name = name;
        this.company = company;
        this.medicine_type = medicine_type;
        this.price = price;
        this.quantity_in_stock = quantity_in_stock;
        this.reorder_level = reorder_level;
        this.expiry_date = expiry_date;
        this.supplier_id = supplier_id;
    }

    public int getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(int medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMedicine_type() {
        return medicine_type;
    }

    public void setMedicine_type(String medicine_type) {
        this.medicine_type = medicine_type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity_in_stock() {
        return quantity_in_stock;
    }

    public void setQuantity_in_stock(int quantity_in_stock) {
        this.quantity_in_stock = quantity_in_stock;
    }

    public int getReorder_level() {
        return reorder_level;
    }

    public void setReorder_level(int reorder_level) {
        this.reorder_level = reorder_level;
    }

    public Date getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(Date expiry_date) {
        this.expiry_date = expiry_date;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    @Override
    public String toString() {
        return "Medicine{"
                + "medicine_id=" + medicine_id
                + ", name='" + name + '\''
                + ", company='" + company + '\''
                + ", medicine_type='" + medicine_type + '\''
                + ", price=" + price
                + ", quantity_in_stock=" + quantity_in_stock
                + ", reorder_level=" + reorder_level
                + ", expiry_date=" + expiry_date
                + ", supplier_id=" + supplier_id
                + '}';
    }
}
