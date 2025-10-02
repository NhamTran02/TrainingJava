CREATE DATABASE Shoe_Ecommerce;
USE Shoe_Ecommerce;

CREATE TABLE brands (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE carts (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY user_id (user_id),
  CONSTRAINT carts_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE cart_items (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cart_id INT NOT NULL,
  variant_id INT NOT NULL,
  quantity INT NOT NULL,
  selected TINYINT(1) DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_cart_items_cart_id (cart_id),
  KEY idx_cart_items_variant_id (variant_id),
  CONSTRAINT cart_items_ibfk_1 FOREIGN KEY (cart_id) REFERENCES carts (id),
  CONSTRAINT cart_items_ibfk_2 FOREIGN KEY (variant_id) REFERENCES product_variants (id)
);

CREATE TABLE categories (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE orders (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  total_amount DECIMAL(38,2) NOT NULL,
  total_cost DECIMAL(38,2) NOT NULL,
  status ENUM('PENDING','PROCESSING','ON_DELIVERY','DELIVERED','CANCELLED') DEFAULT 'PENDING',
  shipping_method_id INT DEFAULT NULL,
  shipping_address VARCHAR(255) NOT NULL,
  note VARCHAR(255) DEFAULT NULL,
  tracking_number VARCHAR(100) DEFAULT NULL,
  shipping_fee DECIMAL(38,2) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY idx_tracking_number (tracking_number)
);

CREATE TABLE order_details (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id INT NOT NULL,
  variant_id INT NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(38,2) NOT NULL,
  unit_cost DECIMAL(38,2) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_order_details_order_id (order_id),
  KEY idx_order_details_variant_id (variant_id),
  CONSTRAINT order_details_ibfk_1 FOREIGN KEY (order_id) REFERENCES orders (id),
  CONSTRAINT order_details_ibfk_2 FOREIGN KEY (variant_id) REFERENCES product_variants (id)
);

CREATE TABLE payments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id INT NOT NULL,
  payment_method VARCHAR(50) NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  currency VARCHAR(3) DEFAULT 'VND',
  status ENUM('PENDING','SUCCESS','FAILED','REFUNDED') DEFAULT 'PENDING',
  txn_ref VARCHAR(100) DEFAULT NULL,
  response_code VARCHAR(10) DEFAULT NULL,
  pay_date TIMESTAMP NULL DEFAULT NULL,
  note TEXT,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  transaction_no VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY txn_ref (txn_ref),
  UNIQUE KEY transaction_no (transaction_no),
  KEY order_id (order_id),
  CONSTRAINT payments_ibfk_1 FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

CREATE TABLE products (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) DEFAULT NULL,
  brand_id INT DEFAULT NULL,
  category_id INT DEFAULT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) DEFAULT 0,
  PRIMARY KEY (id),
  KEY products_ibfk_1 (brand_id),
  KEY products_ibfk_2 (category_id),
  CONSTRAINT products_ibfk_1 FOREIGN KEY (brand_id) REFERENCES brands (id),
  CONSTRAINT products_ibfk_2 FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE product_variants (
  id INT NOT NULL AUTO_INCREMENT,
  product_id INT NOT NULL,
  size VARCHAR(255) NOT NULL,
  color VARCHAR(255) NOT NULL,
  regular_price DECIMAL(38,2) NOT NULL,
  sale_price DECIMAL(38,2) DEFAULT NULL,
  stock_quantity INT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_product_variants_product_price (product_id, sale_price),
  CONSTRAINT product_variants_ibfk_1 FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE product_images (
  id BIGINT NOT NULL AUTO_INCREMENT,
  product_id INT NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  is_thumbnail TINYINT(1) DEFAULT 0,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY product_images_ibfk_1 (product_id),
  CONSTRAINT product_images_ibfk_1 FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE purchase_orders (
  id INT NOT NULL AUTO_INCREMENT,
  supplier_name VARCHAR(255) DEFAULT NULL,
  order_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  total_cost DECIMAL(38,2) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE purchase_order_items (
  id BIGINT NOT NULL AUTO_INCREMENT,
  purchase_order_id INT NOT NULL,
  variant_id INT NOT NULL,
  quantity INT NOT NULL,
  unit_cost DECIMAL(38,2) NOT NULL,
  remaining_qty INT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_purchase_order_items_purchase_order_id (purchase_order_id),
  KEY idx_purchase_order_items_variant_id (variant_id),
  CONSTRAINT purchase_order_items_ibfk_1 FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders (id),
  CONSTRAINT purchase_order_items_ibfk_2 FOREIGN KEY (variant_id) REFERENCES product_variants (id)
);

CREATE TABLE reviews (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  product_id INT NOT NULL,
  order_id INT NOT NULL,
  rating INT DEFAULT NULL,
  comment VARCHAR(255) DEFAULT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_edited TINYINT(1) DEFAULT 0,
  edit_count INT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_reviews_product_id (product_id),
  KEY idx_reviews_user_id (user_id),
  KEY idx_reviews_order_id (order_id),
  CONSTRAINT reviews_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT reviews_ibfk_2 FOREIGN KEY (product_id) REFERENCES products (id),
  CONSTRAINT reviews_ibfk_3 FOREIGN KEY (order_id) REFERENCES orders (id),
  CONSTRAINT reviews_chk_1 CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE roles (
  id INT NOT NULL AUTO_INCREMENT,
  role_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE shipping_methods (
  id INT NOT NULL AUTO_INCREMENT,
  method_name VARCHAR(100) NOT NULL,
  fee DECIMAL(38,2) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  access_token VARCHAR(255) DEFAULT NULL,
  refresh_token VARCHAR(255) DEFAULT NULL,
  blacklisted TINYINT(1) DEFAULT 0,
  PRIMARY KEY (id)
);

CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  phone_number VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) DEFAULT 0,
  verified TINYINT(1) DEFAULT 0,
  verification_code VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user_roles (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE wishlists (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  product_id INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY user_id (user_id, product_id),
  KEY wishlists_ibfk_2 (product_id),
  CONSTRAINT wishlists_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT wishlists_ibfk_2 FOREIGN KEY (product_id) REFERENCES products (id)
);
CREATE TABLE notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  type VARCHAR(50) NOT NULL,     -- REGISTER, ORDER, SYSTEM, ...
  title VARCHAR(100) NOT NULL,
  message VARCHAR(255) NOT NULL,
  is_read TINYINT(1) DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

DELIMITER ;;
CREATE PROCEDURE DecreaseStock(IN orderId INT)
BEGIN
    UPDATE product_variants pv
    JOIN order_details od ON pv.id = od.variant_id
    SET pv.stock_quantity = pv.stock_quantity - od.quantity
    WHERE od.order_id = orderId;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE PROCEDURE GetCartTotal(IN cartId BIGINT)
BEGIN
    SELECT COALESCE(SUM(ci.quantity * COALESCE(v.sale_price, v.regular_price)), 0) AS total
    FROM cart_items ci
    JOIN product_variants v ON ci.variant_id = v.id
    WHERE ci.cart_id = cartId
      AND ci.selected = TRUE;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE PROCEDURE GetProductReviews(IN productId INT)
BEGIN
    SELECT r.id, r.rating, r.comment, r.created_at, u.username
    FROM reviews r
    JOIN users u ON r.user_id = u.id
    WHERE r.product_id = productId
    ORDER BY r.created_at DESC;
END ;;
DELIMITER ;

DELIMITER ;;
CREATE PROCEDURE GetUserOrders(IN userId INT)
BEGIN
    SELECT o.id AS order_id,
           o.created_at,
           o.total_amount,
           o.status
    FROM orders o
    WHERE o.user_id = userId
    ORDER BY o.created_at DESC;
END ;;
DELIMITER ;

SHOW INDEX FROM users;



