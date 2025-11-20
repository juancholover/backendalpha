package upeu.edu.pe.finance.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.finance.application.dto.PagoRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoResponseDTO;
import upeu.edu.pe.finance.domain.services.PagoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Path("/api/v1/pagos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoController {

    @Inject
    PagoService pagoService;

    @GET
    @Path("/universidad/{universidadId}")
    public Response findByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<PagoResponseDTO> pagos = pagoService.findByUniversidad(universidadId);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<PagoResponseDTO> pagos = pagoService.findByEstudiante(estudianteId);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/recibo/{numeroRecibo}")
    public Response findByNumeroRecibo(
            @PathParam("numeroRecibo") String numeroRecibo,
            @QueryParam("universidadId") Long universidadId) {
        PagoResponseDTO pago = pagoService.findByNumeroRecibo(numeroRecibo, universidadId);
        return Response.ok(pago).build();
    }

    @GET
    @Path("/estado/{estado}")
    public Response findByEstado(@PathParam("estado") String estado) {
        List<PagoResponseDTO> pagos = pagoService.findByEstado(estado);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/metodo-pago/{metodoPago}")
    public Response findByMetodoPago(@PathParam("metodoPago") String metodoPago) {
        List<PagoResponseDTO> pagos = pagoService.findByMetodoPago(metodoPago);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/fecha/{fecha}")
    public Response findByFecha(@PathParam("fecha") String fecha) {
        LocalDate fechaPago = LocalDate.parse(fecha);
        List<PagoResponseDTO> pagos = pagoService.findByFecha(fechaPago);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/cajero/{cajero}")
    public Response findByCajero(@PathParam("cajero") String cajero) {
        List<PagoResponseDTO> pagos = pagoService.findByCajero(cajero);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/pendientes-aplicar")
    public Response findPendientesAplicarByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<PagoResponseDTO> pagos = pagoService.findPendientesAplicarByEstudiante(estudianteId);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/rango-fechas")
    public Response findByRangoFechas(
            @QueryParam("fechaInicio") String fechaInicio,
            @QueryParam("fechaFin") String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        List<PagoResponseDTO> pagos = pagoService.findByRangoFechas(inicio, fin);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/cajero/{cajero}/fecha/{fecha}")
    public Response findByCajeroAndFecha(
            @PathParam("cajero") String cajero,
            @PathParam("fecha") String fecha) {
        LocalDate fechaPago = LocalDate.parse(fecha);
        List<PagoResponseDTO> pagos = pagoService.findByCajeroAndFecha(cajero, fechaPago);
        return Response.ok(pagos).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        PagoResponseDTO pago = pagoService.findById(id);
        return Response.ok(pago).build();
    }

    @GET
    @Path("/fecha/{fecha}/total")
    public Response calcularTotalPagosByFecha(@PathParam("fecha") String fecha) {
        LocalDate fechaPago = LocalDate.parse(fecha);
        BigDecimal total = pagoService.calcularTotalPagosByFecha(fechaPago);
        return Response.ok().entity("{\"total\": " + total + "}").build();
    }

    @GET
    @Path("/metodo-pago/{metodoPago}/fecha/{fecha}/total")
    public Response calcularTotalPagosByMetodo(
            @PathParam("metodoPago") String metodoPago,
            @PathParam("fecha") String fecha) {
        LocalDate fechaPago = LocalDate.parse(fecha);
        BigDecimal total = pagoService.calcularTotalPagosByMetodo(metodoPago, fechaPago);
        return Response.ok().entity("{\"total\": " + total + "}").build();
    }

    @GET
    @Path("/estado/{estado}/universidad/{universidadId}/count")
    public Response countByEstadoAndUniversidad(
            @PathParam("estado") String estado,
            @PathParam("universidadId") Long universidadId) {
        long count = pagoService.countByEstadoAndUniversidad(estado, universidadId);
        return Response.ok().entity("{\"count\": " + count + "}").build();
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
        PagoResponseDTO pago = pagoService.anular(id, motivo);
        return Response.ok(pago).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        pagoService.delete(id);
        return Response.noContent().build();
    }
}
