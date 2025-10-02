CREATE DATABASE inventory_service;
USE inventory_service;

CREATE TABLE purchase_orders (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  supplier_name VARCHAR(255) DEFAULT NULL,
  order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  total_cost DECIMAL(38,2) NOT NULL
);

CREATE TABLE purchase_order_items (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  purchase_order_id CHAR(36) NOT NULL,
  variant_id CHAR(36) NOT NULL,  -- từ Product Catalog
  quantity INT NOT NULL,
  unit_cost DECIMAL(38,2) NOT NULL,
  remaining_qty INT NOT NULL DEFAULT 0,
  FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id)
);
