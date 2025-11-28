package upeu.edu.pe.finance.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoRequestDTO;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoResponseDTO;
import upeu.edu.pe.finance.application.services.CuentaCorrienteAlumnoApplicationService;

import java.util.List;

@Path("/api/v1/cuentas-corrientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CuentaCorrienteAlumnoController {

    @Inject
    CuentaCorrienteAlumnoApplicationService cuentaService;

    @GET
    @Path("/estudiante/{estudianteId}")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findByEstudianteId(estudianteId);
        return Response.ok(cuentas).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        CuentaCorrienteAlumnoResponseDTO cuenta = cuentaService.findById(id);
        return Response.ok(cuenta).build();
    }

    @POST
    public Response create(@Valid CuentaCorrienteAlumnoRequestDTO requestDTO) {
        CuentaCorrienteAlumnoResponseDTO cuenta = cuentaService.create(requestDTO);
        return Response.status(Response.Status.CREATED).entity(cuenta).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid CuentaCorrienteAlumnoRequestDTO requestDTO) {
        CuentaCorrienteAlumnoResponseDTO cuenta = cuentaService.update(id, requestDTO);
        return Response.ok(cuenta).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        cuentaService.delete(id);
        return Response.noContent().build();
    }
}
