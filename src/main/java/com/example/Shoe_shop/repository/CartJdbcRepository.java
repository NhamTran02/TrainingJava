package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.dto.response.CartItemResponse;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.CartItemMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartJdbcRepository {
    JdbcTemplate jdbcTemplate;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    //JdbcTemplate
    public Long getCartIdByUserId(Long userId) {
        String sql = "select id from carts where user_id = ?";
        List<Long> cartIds = jdbcTemplate.queryForList(sql, Long.class, userId);
        if (cartIds.isEmpty()) {
            // tạo cart mới nếu chưa có
            jdbcTemplate.update("INSERT INTO carts(user_id) VALUES (?)", userId);
            cartIds = jdbcTemplate.queryForList(sql, Long.class, userId);
        }
        return cartIds.get(0);
    }

    // call store procedure
    public BigDecimal getCartTotal(Long cartId) {
        String sql="CALL GetCartTotal(?)";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, cartId);
    }

    //NamedJdbcTemplate
    public List<CartItemResponse> findCartItems(Long cartId) {
        String sql = """
                SELECT ci.id,
                       ci.quantity,
                       ci.selected,
                       v.id AS variant_id,
                       v.size,
                       v.color,
                       COALESCE(v.sale_price, v.regular_price) AS unit_price,
                       p.id AS product_id,
                       p.name AS product_name,
                       img.image_url AS image_url
                        
                FROM cart_items ci
                JOIN product_variants v ON ci.variant_id = v.id
                JOIN products p ON v.product_id = p.id
                LEFT JOIN product_images img ON p.id = img.product_id AND img.is_thumbnail = TRUE
                WHERE ci.cart_id = :cartId
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("cartId", cartId);

        return namedParameterJdbcTemplate.query(sql, params, new CartItemMapper());
    }

    // Lấy tất cả cart items theo user
    public List<CartItemResponse> getCartItemsByUserId(Long userId) {
        Long cartId = getCartIdByUserId(userId);
        return findCartItems(cartId);
    }

    //NamedJdbcTemplate
    // Lấy cart items theo danh sách cartItemId
    public List<CartItemResponse> getCartItemsByIds(List<Long> cartItemIds) {
        if (cartItemIds.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        String sql = """
            SELECT ci.id,
                   ci.variant_id,
                   ci.quantity,
                   COALESCE(v.sale_price, v.regular_price) AS unit_price,
                   p.name AS product_name
            FROM cart_items ci
            JOIN product_variants v ON ci.variant_id = v.id
            JOIN products p ON v.product_id = p.id
            WHERE ci.id IN (:ids)
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", cartItemIds);

        return namedParameterJdbcTemplate.query(sql, params, new CartItemMapper());
    }

    public void addOrUpdateCartItem(Long cartId, Long variantId, int quantity) {
        String checkSql = "SELECT COUNT(*) FROM cart_items WHERE cart_id=? AND variant_id=?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, cartId, variantId);
        if (count != null && count > 0) {
            jdbcTemplate.update(
                    "UPDATE cart_items SET quantity = quantity + ? WHERE cart_id=? AND variant_id=?",
                    quantity, cartId, variantId
            );
        } else {
            jdbcTemplate.update(
                    "INSERT INTO cart_items(cart_id, variant_id, quantity,selected) VALUES (?,?,?,?)",
                    cartId, variantId, quantity,false
            );
        }
    }

    public void toggleSelectedItem(Long cartId, Long variantId, boolean selected) {
        String checkSql = "SELECT COUNT(*) FROM cart_items WHERE cart_id=? AND variant_id=?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, cartId, variantId);
        if (count == null || count == 0) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        String updateSql = "UPDATE cart_items SET selected=? WHERE cart_id=? AND variant_id=?";
        jdbcTemplate.update(updateSql, selected, cartId, variantId);
    }

    public void removeItem(Long cartId, Long variantId){
        jdbcTemplate.update("DELETE FROM cart_items WHERE cart_id=? AND variant_id=?",cartId,variantId);
    }

    public void removeCart(Long cartId){
        jdbcTemplate.update("DELETE FROM cart_items WHERE cart_id=?",cartId);
    }
}
