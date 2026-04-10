package model;

public class SaleItem {

    private int sale_item_id;
    private int sale_id;
    private int medicine_id;
    private int quantity_sold;
    private double price_at_sale;

    public SaleItem() {
        // Default constructor.
    }

    public SaleItem(int sale_item_id, int sale_id, int medicine_id, int quantity_sold, double price_at_sale) {
        this.sale_item_id = sale_item_id;
        this.sale_id = sale_id;
        this.medicine_id = medicine_id;
        this.quantity_sold = quantity_sold;
        this.price_at_sale = price_at_sale;
    }

    public int getSale_item_id() {
        return sale_item_id;
    }

    public void setSale_item_id(int sale_item_id) {
        this.sale_item_id = sale_item_id;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public int getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(int medicine_id) {
        this.medicine_id = medicine_id;
    }

    public int getQuantity_sold() {
        return quantity_sold;
    }

    public void setQuantity_sold(int quantity_sold) {
        this.quantity_sold = quantity_sold;
    }

    public double getPrice_at_sale() {
        return price_at_sale;
    }

    public void setPrice_at_sale(double price_at_sale) {
        this.price_at_sale = price_at_sale;
    }

    @Override
    public String toString() {
        return "SaleItem{"
                + "sale_item_id=" + sale_item_id
                + ", sale_id=" + sale_id
                + ", medicine_id=" + medicine_id
                + ", quantity_sold=" + quantity_sold
                + ", price_at_sale=" + price_at_sale
                + '}';
    }
}
