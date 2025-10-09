CREATE DATABASE product_service;
USE product_service;

CREATE TABLE brands (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) DEFAULT NULL,
  brand_id BIGINT DEFAULT NULL,
  category_id BIGINT DEFAULT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) DEFAULT 0,
  FOREIGN KEY (brand_id) REFERENCES brands(id),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE product_variants (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  size VARCHAR(255) NOT NULL,
  color VARCHAR(255) NOT NULL,
  regular_price DECIMAL(38,2) NOT NULL,
  sale_price DECIMAL(38,2) DEFAULT NULL,
  stock_quantity INT NOT NULL DEFAULT 0,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE product_images (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  is_thumbnail TINYINT(1) DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,     -- từ User Service (chỉ lưu ID tham chiếu)
  product_id BIGINT NOT NULL,
  order_id BIGINT NOT NULL,    -- từ Order Service (chỉ lưu ID)
  rating INT DEFAULT NULL CHECK (rating BETWEEN 1 AND 5),
  comment VARCHAR(255) DEFAULT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_edited TINYINT(1) DEFAULT 0,
  edit_count INT DEFAULT 0,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE wishlists (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,   -- từ User Service 
  product_id BIGINT NOT NULL,
  CONSTRAINT unique_user_product UNIQUE (user_id, product_id),
  CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products(id)
)




