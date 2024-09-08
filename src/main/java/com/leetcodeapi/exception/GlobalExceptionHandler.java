package com.leetcodeapi.exception;

import com.leetcodeapi.dto.ApiResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message,false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEntry(DuplicateEntryException ex) {
        String message = ex.getMessage();
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

    @ExceptionHandler(WrongInputException.class)
    public ResponseEntity<ApiResponse> handleWrongInputExceptionHandler(WrongInputException ex){
        String message = "Invalid username or password";
        ApiResponse apiResponse = new ApiResponse(message,false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongTokenException.class)
    public ResponseEntity<ApiResponse> handleWrongTokenExceptionHandler(WrongTokenException ex){
        String message = "Invalid or Expired Reset Link";
        ApiResponse apiResponse = new ApiResponse(message,false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> handleInvalidJwtExceptionHandler(JwtException ex){
        return new ResponseEntity<>(new ApiResponse("There is some error with server right now",false), HttpStatus.FORBIDDEN);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleExtraException(Exception ex) {
        log.error("Exception occurred: ", ex);
        return new ResponseEntity<>(new ApiResponse("There is some error with server right now",false), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
