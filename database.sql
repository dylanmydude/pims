CREATE DATABASE IF NOT EXISTS pims_db;
USE pims_db;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL
);

CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

CREATE TABLE medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    company VARCHAR(100) NOT NULL,
    medicine_type VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL,
    expiry_date DATE NOT NULL,
    CONSTRAINT fk_medicines_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    sale_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_sales_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE sale_items (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity_sold INT NOT NULL,
    price_at_sale DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_sale_items_sale
        FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_sale_items_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

INSERT INTO users (username, password, full_name, role)
VALUES
    ('admin', 'admin123', 'System Administrator', 'Admin'),
    ('cashier', 'cash123', 'Default Cashier', 'Cashier');

INSERT INTO suppliers (name, contact_person, phone, email, address)
VALUES
    ('HealthPlus Distributors', 'John Mensah', '+27-11-555-1001', 'contact@healthplus.co.za', '12 11th Ave, Benoni'),
    ('MediCare Supplies', 'Sarah Naidoo', '+27-21-555-2020', 'sales@medicaresupplies.co.za', '45 North Rand Road, Boksburg');

INSERT INTO medicines (
    supplier_id,
    name,
    company,
    medicine_type,
    price,
    quantity_in_stock,
    reorder_level,
    expiry_date
)
VALUES
    (1, 'Paracetamol 500mg', 'HealthFirst Labs', 'Tablet', 25.50, 100, 10, '2027-12-31'),
    (2, 'Panado 250mg', 'MediCare Pharma', 'Capsule', 68.00, 60, 10, '2027-08-15'),
    (1, 'Corenza C 1000mg', 'Vitality Wellness', 'Tablet', 45.75, 80, 10, '2028-03-20');
