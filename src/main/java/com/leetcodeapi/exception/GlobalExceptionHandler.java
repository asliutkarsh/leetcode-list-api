package com.leetcodeapi.exception;

import com.leetcodeapi.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message,false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEntry(DuplicateEntryException ex) {
        String message = "This user-name already exists";
        return new ResponseEntity<>(new ApiResponse(message,false), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LeetcodeIdNotFoundException.class)
    public ResponseEntity<ApiResponse> handleLeetcodeNotFound(LeetcodeIdNotFoundException ex) {
        String message = "This user-name not found on leetcode";
        return new ResponseEntity<>(new ApiResponse(message,false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProblemNotFoundException.class)
    public ResponseEntity<ApiResponse> handleProblemNotFound(ProblemNotFoundException ex) {
        String message = "This problem not found on leetcode";
        return new ResponseEntity<>(new ApiResponse(message,false), HttpStatus.NOT_FOUND);
    }

}
