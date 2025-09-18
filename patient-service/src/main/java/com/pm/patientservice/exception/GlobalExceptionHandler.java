package com.pm.patientservice.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, String> errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errors.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleResourceNotFound(PatientNotFoundException ex){
       log.error("patient not found",ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
    @ExceptionHandler(EmailAlreadyExist.class)
    public ResponseEntity<Map<String,String>> emailAlredyExist(EmailAlreadyExist ex){
       log.warn("email already exist{}",ex.getMessage());
        Map<String, String> errors=new HashMap<>();
        errors.put("email", ex.getMessage());
     return  ResponseEntity.status(HttpStatus.CONFLICT).body(errors);

    }


}
