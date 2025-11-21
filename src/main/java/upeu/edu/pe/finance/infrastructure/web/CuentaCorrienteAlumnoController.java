package upeu.edu.pe.finance.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoRequestDTO;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoResponseDTO;
import upeu.edu.pe.finance.domain.services.CuentaCorrienteAlumnoService;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/v1/cuentas-corrientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CuentaCorrienteAlumnoController {

    @Inject
    CuentaCorrienteAlumnoService cuentaService;

    @GET
    @Path("/universidad/{universidadId}")
    public Response findByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findByUniversidad(universidadId);
        return Response.ok(cuentas).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findByEstudiante(estudianteId);
        return Response.ok(cuentas).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/vencidas")
    public Response findVencidasByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findVencidasByEstudiante(estudianteId);
        return Response.ok(cuentas).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/pendientes")
    public Response findPendientesByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findPendientesByEstudiante(estudianteId);
        return Response.ok(cuentas).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/proximas-vencer")
    public Response findProximasVencerByEstudiante(
            @PathParam("estudianteId") Long estudianteId,
            @QueryParam("dias") @DefaultValue("30") Integer dias) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findProximasVencerByEstudiante(estudianteId, dias);
        return Response.ok(cuentas).build();
    }

    @GET
    @Path("/tipo-cargo/{tipoCargo}")
    public Response findByTipoCargo(@PathParam("tipoCargo") String tipoCargo) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findByTipoCargo(tipoCargo);
        return Response.ok(cuentas).build();
    }

    @GET
    @Path("/estado/{estado}")
    public Response findByEstado(@PathParam("estado") String estado) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findByEstado(estado);
        return Response.ok(cuentas).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        CuentaCorrienteAlumnoResponseDTO cuenta = cuentaService.findById(id);
        return Response.ok(cuenta).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/deuda-total")
    public Response calcularDeudaTotalByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        BigDecimal deudaTotal = cuentaService.calcularDeudaTotalByEstudiante(estudianteId);
        return Response.ok().entity("{\"deudaTotal\": " + deudaTotal + "}").build();
    }

    @GET
    @Path("/estado/{estado}/universidad/{universidadId}/count")
    public Response countByEstadoAndUniversidad(
            @PathParam("estado") String estado,
            @PathParam("universidadId") Long universidadId) {
        long count = cuentaService.countByEstadoAndUniversidad(estado, universidadId);
        return Response.ok().entity("{\"count\": " + count + "}").build();
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

    @PUT
    @Path("/{id}/aplicar-pago")
    public Response aplicarPago(@PathParam("id") Long id, @QueryParam("montoPago") BigDecimal montoPago) {
        CuentaCorrienteAlumnoResponseDTO cuenta = cuentaService.aplicarPago(id, montoPago);
        return Response.ok(cuenta).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        cuentaService.delete(id);
        return Response.noContent().build();
    }
}
