package com.example.Shoe_shop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL) //khi field nào null thì bỏ qua kh trả về
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ApiResponse <T>{
    @Builder.Default
    int code=200;
    String message;
    T result;

}
