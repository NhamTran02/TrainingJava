CREATE DATABASE product_service;
USE product_service;

CREATE TABLE brands (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  name VARCHAR(255) NOT NULL
);

CREATE TABLE categories (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  name VARCHAR(255) NOT NULL
);

CREATE TABLE products (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) DEFAULT NULL,
  brand_id CHAR(36) DEFAULT NULL,
  category_id CHAR(36) DEFAULT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) DEFAULT 0,
  FOREIGN KEY (brand_id) REFERENCES brands(id),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE product_variants (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  product_id CHAR(36) NOT NULL,
  size VARCHAR(255) NOT NULL,
  color VARCHAR(255) NOT NULL,
  regular_price DECIMAL(38,2) NOT NULL,
  sale_price DECIMAL(38,2) DEFAULT NULL,
  stock_quantity INT NOT NULL DEFAULT 0,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE product_images (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  product_id CHAR(36) NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  is_thumbnail TINYINT(1) DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE reviews (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  user_id CHAR(36) NOT NULL,     -- từ User Service (chỉ lưu ID tham chiếu)
  product_id CHAR(36) NOT NULL,
  order_id CHAR(36) NOT NULL,    -- từ Order Service (chỉ lưu ID)
  rating INT DEFAULT NULL CHECK (rating BETWEEN 1 AND 5),
  comment VARCHAR(255) DEFAULT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_edited TINYINT(1) DEFAULT 0,
  edit_count INT DEFAULT 0,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE wishlists (
  id CHAR(36) PRIMARY KEY DEFAULT(uuid()),
  user_id CHAR(36) NOT NULL UNIQUE,   -- từ User Service
  product_id CHAR(36) NOT NULL UNIQUE,
  FOREIGN KEY (product_id) REFERENCES products(id)
);