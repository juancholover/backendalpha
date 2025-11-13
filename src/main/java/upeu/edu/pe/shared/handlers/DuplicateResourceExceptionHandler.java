package upeu.edu.pe.shared.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Provider
public class DuplicateResourceExceptionHandler implements ExceptionMapper<DuplicateResourceException> {

    private static final Logger LOGGER = Logger.getLogger(DuplicateResourceExceptionHandler.class.getName());

    @Override
    public Response toResponse(DuplicateResourceException exception) {
        LOGGER.warning("Recurso duplicado: " + exception.getMessage());

        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(exception.getMessage());
        response.setTimestamp(LocalDateTime.now());

        return Response
                .status(Response.Status.CONFLICT)
                .entity(response)
                .build();
    }
}
