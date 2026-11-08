package auction.auctions.shared.HttpRequests;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

// Convert the exception to Http, return http response
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles unexpected Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<Void>> handleUnexpectedException(Exception ex) {
        var errors = List.of(new GlobalResponse.ErrorMessage(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GlobalResponse.error(errors));
    }


    // Handles Validation Exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new GlobalResponse.ErrorMessage(
                        error.getField() + ": " + error.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponse.error(errors));
    }

    // Customized Exceptions
    @ExceptionHandler(CustomResponseException.class)
    public ResponseEntity<GlobalResponse<Void>> handleCustomResponseException(CustomResponseException ex) {
        var errors = List.of(new GlobalResponse.ErrorMessage(ex.getMessage()));
        return ResponseEntity
                .status(HttpStatus.valueOf(ex.getCode()))
                .body(GlobalResponse.error(errors));
    }
}