package com.signit.signature.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class InternalExceptionHandler {

    @ExceptionHandler({RestResponseException.class})
    public ResponseEntity<String> handleValidationErrorOnRestCalls(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptionMethod(Exception ex) {

        // Handle All Field Validation Errors
        if(ex instanceof MethodArgumentNotValidException) {
            StringBuilder eroorMessages = new StringBuilder();
            List<FieldError> fieldErrors = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors();
            for(FieldError fieldError: fieldErrors){
                eroorMessages.append(fieldError.getDefaultMessage());
                eroorMessages.append(";");
            }
            return new ResponseEntity<>(eroorMessages, HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>("Internal Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
