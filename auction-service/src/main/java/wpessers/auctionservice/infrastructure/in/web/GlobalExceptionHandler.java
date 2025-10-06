package wpessers.auctionservice.infrastructure.in.web;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wpessers.auctionservice.domain.exception.AuctionNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
            "Invalid request content");

        problemDetail.setProperty("timestamp", Instant.now());
        Map<String, String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                    .orElse("Invalid value")
            ));
        problemDetail.setProperty("validationErrors", validationErrors);
        return problemDetail;
    }

    @ExceptionHandler
    public ProblemDetail handleAuctionNotFoundException(AuctionNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
            ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
