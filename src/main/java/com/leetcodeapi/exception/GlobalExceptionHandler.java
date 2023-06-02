package com.leetcodeapi.exception;

import com.leetcodeapi.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message,false);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEntry(DuplicateEntryException ex) {
        String message = "This user-name already exists";
        return new ResponseEntity<>(new ApiResponse(message,false), HttpStatus.CONFLICT);
    }

}
