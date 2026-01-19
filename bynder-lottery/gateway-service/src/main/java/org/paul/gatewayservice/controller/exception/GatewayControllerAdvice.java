package org.paul.gatewayservice.controller.exception;

import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.paul.gatewayservice.controller.domain.ErrorResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GatewayControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldError();
        var message = fieldError != null
                ? fieldError.getField() + " " + fieldError.getDefaultMessage()
                : ex.getBindingResult().getAllErrors().stream()
                    .findFirst()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .orElse("Invalid request");
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Invalid request"));
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGrpc(StatusRuntimeException ex) {
        var status = ex.getStatus();
        var message = status.getDescription() != null ? status.getDescription() : "Unexpected error";
        var httpStatus = switch (status.getCode()) {
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.SERVICE_UNAVAILABLE;
        };
        log.warn("GRPC error: {} : {}", status, message);
        return ResponseEntity.status(httpStatus).body(new ErrorResponse(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled gateway error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Unexpected error"));
    }
}
