CREATE DATABASE order_service;
USE order_service;

CREATE TABLE shipping_methods (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  method_name VARCHAR(100) NOT NULL,
  fee DECIMAL(38,2) NOT NULL
);

CREATE TABLE orders (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  user_id CHAR(36) NOT NULL,  -- từ User Service
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  total_amount DECIMAL(38,2) NOT NULL,
  total_cost DECIMAL(38,2) NOT NULL,
  status ENUM('PENDING','PROCESSING','ON_DELIVERY','DELIVERED','CANCELLED') DEFAULT 'PENDING',
  shipping_method_id CHAR(36) DEFAULT NULL,
  shipping_address VARCHAR(255) NOT NULL,
  note VARCHAR(255) DEFAULT NULL,
  tracking_number VARCHAR(100) DEFAULT NULL UNIQUE,
  shipping_fee DECIMAL(38,2) DEFAULT NULL,
  FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id)
);

CREATE TABLE order_details (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  order_id CHAR(36) NOT NULL,
  variant_id CHAR(36) NOT NULL,  -- từ Product Catalog
  quantity INT NOT NULL,
  unit_price DECIMAL(38,2) NOT NULL,
  unit_cost DECIMAL(38,2) NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id)
);
