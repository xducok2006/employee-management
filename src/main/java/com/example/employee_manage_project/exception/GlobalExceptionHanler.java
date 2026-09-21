package com.example.employee_manage_project.exception;

import com.example.employee_manage_project.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHanler {
    @ExceptionHandler(HandleNotFound.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(HandleNotFound ex)
    {
        ApiResponse<Void> response = new ApiResponse<>(404,ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(HandleAlreadyExists.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyExists(HandleAlreadyExists ex)
    {
        ApiResponse<Void> response = new ApiResponse<>(409,ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex)
    {
        String message = ex.getBindingResult().getFieldErrors().stream().
                map(error -> error.getField()+": "+error.getDefaultMessage()).collect(Collectors.joining("; "));
        ApiResponse<Void> response = new ApiResponse<>(400,message,null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleRefreshTokenExpired(RefreshTokenExpiredException ex)
    {
        ApiResponse<Void> response = new ApiResponse<>(401,ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex)
    {
        ApiResponse<Void> response = new ApiResponse<>(405, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex)
    {
        ApiResponse<Void> response = new ApiResponse<>(500, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlerNotFound(NoHandlerFoundException ex)
    {
        ApiResponse<Void> response = new ApiResponse<>(404, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLock(
            ObjectOptimisticLockingFailureException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Attendance was updated by another request");
    }
}
