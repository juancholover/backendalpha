package upeu.edu.pe.finance.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.finance.application.dto.PagoRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoResponseDTO;
import upeu.edu.pe.finance.application.services.PagoApplicationService;

import java.util.List;

@Path("/api/v1/pagos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoController {

    @Inject
    PagoApplicationService pagoService;

    @GET
    @Path("/estudiante/{estudianteId}")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<PagoResponseDTO> pagos = pagoService.findByEstudianteId(estudianteId);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        PagoResponseDTO pago = pagoService.findById(id);
        return Response.ok(pago).build();
    }

    @POST
    public Response create(@Valid PagoRequestDTO requestDTO) {
        PagoResponseDTO pago = pagoService.create(requestDTO);
        return Response.status(Response.Status.CREATED).entity(pago).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid PagoRequestDTO requestDTO) {
        PagoResponseDTO pago = pagoService.update(id, requestDTO);
        return Response.ok(pago).build();
    }

    @PUT
    @Path("/{id}/anular")
    public Response anular(@PathParam("id") Long id, @QueryParam("motivo") String motivo) {
        pagoService.anular(id, motivo);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        pagoService.delete(id);
        return Response.noContent().build();
    }
}
