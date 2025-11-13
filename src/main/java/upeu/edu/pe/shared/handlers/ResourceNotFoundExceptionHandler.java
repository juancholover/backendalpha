package upeu.edu.pe.shared.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;
import upeu.edu.pe.shared.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Provider
public class ResourceNotFoundExceptionHandler implements ExceptionMapper<ResourceNotFoundException> {

    private static final Logger LOGGER = Logger.getLogger(ResourceNotFoundExceptionHandler.class.getName());

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        LOGGER.warning("Recurso no encontrado: " + exception.getMessage());

        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(exception.getMessage());
        response.setTimestamp(LocalDateTime.now());

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(response)
                .build();
    }
}
