package upeu.edu.pe.shared.handlers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import upeu.edu.pe.shared.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOGGER = Logger.getLogger(ValidationExceptionHandler.class.getName());

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        LOGGER.warning("Error de validación: " + exception.getMessage());

        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }

        ApiResponse<Map<String, String>> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("Error de validación en los datos enviados");
        response.setData(errors);
        response.setTimestamp(LocalDateTime.now());

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }
}
