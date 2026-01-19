package org.paul.gatewayservice.controller.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GatewayControllerAdviceTest {

    private final GatewayControllerAdvice advice = new GatewayControllerAdvice();

    @Test
    void invalidArgumentMapsToBadRequest() {
        var ex = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        var response = advice.handleGrpc(ex);
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Unexpected error", response.getBody().message());
    }

    @Test
    void unauthenticatedMapsToUnauthorized() {
        var ex = new StatusRuntimeException(Status.UNAUTHENTICATED);
        var response = advice.handleGrpc(ex);
        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Unexpected error", response.getBody().message());
    }

    @Test
    void otherStatusMapsToServiceUnavailable() {
        var ex = new StatusRuntimeException(Status.INTERNAL);
        var response = advice.handleGrpc(ex);
        assertEquals(503, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Unexpected error", response.getBody().message());
    }

    @Test
    void validationErrorMapsToBadRequest() throws Exception {
        var bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "must not be blank"));
        var exception = new MethodArgumentNotValidException(placeholderMethod(), bindingResult);

        var response = advice.handleValidation(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("email must not be blank", response.getBody().message());
    }

    @Test
    void constraintViolationMapsToBadRequest() {
        var response = advice.handleConstraintViolation(new ConstraintViolationException("invalid", java.util.Set.of()));
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Invalid request", response.getBody().message());
    }

    @Test
    void genericExceptionMapsToInternalServerError() {
        var response = advice.handleException(new RuntimeException());
        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Unexpected error", response.getBody().message());
    }

    private static org.springframework.core.MethodParameter placeholderMethod() throws NoSuchMethodException {
        var method = GatewayControllerAdviceTest.class.getDeclaredMethod("sample", String.class);
        return new org.springframework.core.MethodParameter(method, 0);
    }

    private void sample(String email) {
    }
}
