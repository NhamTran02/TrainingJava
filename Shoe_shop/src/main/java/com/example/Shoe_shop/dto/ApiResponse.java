package com.example.Shoe_shop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL) //khi field nào null thì bỏ qua kh trả về
@Data
public class ApiResponse <T>{
    private int code=200;
    private String message;
    private T result;

}
