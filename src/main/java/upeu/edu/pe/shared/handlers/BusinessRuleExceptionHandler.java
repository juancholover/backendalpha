package upeu.edu.pe.shared.handlers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Provider
public class BusinessRuleExceptionHandler implements ExceptionMapper<BusinessRuleException> {

    private static final Logger LOGGER = Logger.getLogger(BusinessRuleExceptionHandler.class.getName());

    @Override
    public Response toResponse(BusinessRuleException exception) {
        LOGGER.warning("Regla de negocio violada: " + exception.getMessage());

        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(exception.getMessage());
        response.setTimestamp(LocalDateTime.now());

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }
}
