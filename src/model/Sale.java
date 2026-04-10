package model;

import java.sql.Timestamp;

public class Sale {

    private int sale_id;
    private Timestamp sale_date;
    private double total_amount;
    private int user_id;

    public Sale() {
        // Default constructor.
    }

    public Sale(int sale_id, Timestamp sale_date, double total_amount, int user_id) {
        this.sale_id = sale_id;
        this.sale_date = sale_date;
        this.total_amount = total_amount;
        this.user_id = user_id;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public Timestamp getSale_date() {
        return sale_date;
    }

    public void setSale_date(Timestamp sale_date) {
        this.sale_date = sale_date;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Sale{"
                + "sale_id=" + sale_id
                + ", sale_date=" + sale_date
                + ", total_amount=" + total_amount
                + ", user_id=" + user_id
                + '}';
    }
}
