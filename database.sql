CREATE DATABASE IF NOT EXISTS pims_db;
USE pims_db;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'CASHIER') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT NOT NULL,
    medicine_code VARCHAR(30) NOT NULL UNIQUE,
    medicine_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(50),
    unit_price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    expiry_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_medicines_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    sale_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    payment_method VARCHAR(30) DEFAULT 'CASH',
    CONSTRAINT fk_sales_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE sale_items (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_sale_items_sale
        FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_sale_items_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

INSERT INTO users (username, password, full_name, role, is_active)
VALUES
    ('admin', 'admin123', 'System Administrator', 'ADMIN', TRUE),
    ('cashier', 'cash123', 'Default Cashier', 'CASHIER', TRUE);

INSERT INTO suppliers (supplier_name, contact_person, phone, email, address)
VALUES
    ('HealthPlus Distributors', 'John Mensah', '+27-11-555-1001', 'contact@healthplus.co.za', '12 Market Street, Johannesburg'),
    ('MediCare Supplies', 'Sarah Naidoo', '+27-21-555-2020', 'sales@medicaresupplies.co.za', '45 Main Road, Cape Town');

INSERT INTO medicines (supplier_id, medicine_code, medicine_name, description, category, unit_price, stock_quantity, expiry_date)
VALUES
    (1, 'MED001', 'Paracetamol 500mg', 'Pain relief tablets', 'Analgesic', 25.50, 100, '2027-12-31'),
    (2, 'MED002', 'Amoxicillin 250mg', 'Antibiotic capsules', 'Antibiotic', 68.00, 60, '2027-08-15'),
    (1, 'MED003', 'Vitamin C 1000mg', 'Immune support tablets', 'Supplement', 45.75, 80, '2028-03-20');
