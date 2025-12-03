package com.example.Shoe_shop.exception;

import com.example.Shoe_shop.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException e){
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException e){
        ApiResponse apiResponse = new ApiResponse();
        
        // Handle specific runtime exceptions
        if (e.getMessage() != null) {
            if (e.getMessage().contains("REFRESH_REVOKED") || e.getMessage().contains("REFRESH_NOT_FOUND")) {
                apiResponse.setCode(401);
                apiResponse.setMessage("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
            }
            if (e.getMessage().contains("INVALID_REFRESH_TOKEN")) {
                apiResponse.setCode(401);
                apiResponse.setMessage("Token không hợp lệ. Vui lòng đăng nhập lại.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
            }
        }
        
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingGeneralException(Exception e){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    ResponseEntity<ApiResponse> handlingAuthorizationDeniedException(AuthorizationDeniedException e){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.FORBIDDEN.getCode());
        apiResponse.setMessage(ErrorCode.FORBIDDEN.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException e){
        String enumKey=e.getFieldError().getDefaultMessage();
        ErrorCode errorCode=ErrorCode.INVALID_MESSAGE_KEY;
        try {
            errorCode=ErrorCode.valueOf(enumKey);
        }catch (IllegalArgumentException exception) {

        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    ResponseEntity<ApiResponse> handlingBadCredentials(BadCredentialsException e){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.PASSWORD_INCORRECT.getCode());
        apiResponse.setMessage("Tên đăng nhập hoặc mật khẩu không chính xác");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

}
