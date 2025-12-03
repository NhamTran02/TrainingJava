package com.example.Shoe_shop.mapper;

import com.example.Shoe_shop.dto.response.CartItemResponse;
import com.example.Shoe_shop.dto.response.ProductVariantResponse;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartItemMapper implements RowMapper<CartItemResponse> {

    @Override
    public CartItemResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setCartItemId(rs.getLong("id"));
        cartItemResponse.setProductId(rs.getLong("product_id"));

        ProductVariantResponse variantResponse = new ProductVariantResponse();
        variantResponse.setId(rs.getLong("variant_id"));
        variantResponse.setColor(rs.getString("color"));
        variantResponse.setSize(rs.getString("size"));
        cartItemResponse.setVariantResponse(variantResponse);

        cartItemResponse.setProductName(rs.getString("product_name"));
        cartItemResponse.setQuantity(rs.getInt("quantity"));
        BigDecimal price = rs.getBigDecimal("unit_price");
        cartItemResponse.setUnitPrice(price);

        cartItemResponse.setSubtotal(cartItemResponse.getUnitPrice().multiply(BigDecimal.valueOf(cartItemResponse.getQuantity())));
        cartItemResponse.setSelected(rs.getBoolean("selected"));
        cartItemResponse.setImageUrl(rs.getString("image_url"));

        return cartItemResponse;
    }
}
