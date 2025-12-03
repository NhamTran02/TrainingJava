package com.example.Shoe_shop.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {
    private int page;               // trang hiện tại
    private int size;               // số bản ghi/trang
    private long totalElements;     // tổng số bản ghi
    private int totalPages;         // tổng số trang
    private List<T> content;        // danh sách dữ liệu
}
