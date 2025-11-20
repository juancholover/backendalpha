package upeu.edu.pe.finance.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaResponseDTO;
import upeu.edu.pe.finance.domain.services.PagoDetalleDeudaService;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/v1/pago-detalles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoDetalleDeudaController {

    @Inject
    PagoDetalleDeudaService detalleService;

    @GET
    @Path("/pago/{pagoId}")
    public Response findByPago(@PathParam("pagoId") Long pagoId) {
        List<PagoDetalleDeudaResponseDTO> detalles = detalleService.findByPago(pagoId);
        return Response.ok(detalles).build();
    }

    @GET
    @Path("/deuda/{deudaId}")
    public Response findByDeuda(@PathParam("deudaId") Long deudaId) {
        List<PagoDetalleDeudaResponseDTO> detalles = detalleService.findByDeuda(deudaId);
        return Response.ok(detalles).build();
    }

    @GET
    @Path("/pago/{pagoId}/activos")
    public Response findActivosByPago(@PathParam("pagoId") Long pagoId) {
        List<PagoDetalleDeudaResponseDTO> detalles = detalleService.findActivosByPago(pagoId);
        return Response.ok(detalles).build();
    }

    @GET
    @Path("/deuda/{deudaId}/activos")
    public Response findActivosByDeuda(@PathParam("deudaId") Long deudaId) {
        List<PagoDetalleDeudaResponseDTO> detalles = detalleService.findActivosByDeuda(deudaId);
        return Response.ok(detalles).build();
    }

    @GET
    @Path("/deuda/{deudaId}/revertidos")
    public Response findRevertidosByDeuda(@PathParam("deudaId") Long deudaId) {
        List<PagoDetalleDeudaResponseDTO> detalles = detalleService.findRevertidosByDeuda(deudaId);
        return Response.ok(detalles).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        PagoDetalleDeudaResponseDTO detalle = detalleService.findById(id);
        return Response.ok(detalle).build();
    }

    @GET
    @Path("/deuda/{deudaId}/total-aplicado")
    public Response calcularTotalAplicadoByDeuda(@PathParam("deudaId") Long deudaId) {
        BigDecimal total = detalleService.calcularTotalAplicadoByDeuda(deudaId);
        return Response.ok().entity("{\"totalAplicado\": " + total + "}").build();
    }

    @GET
    @Path("/pago/{pagoId}/total-aplicado")
    public Response calcularTotalAplicadoByPago(@PathParam("pagoId") Long pagoId) {
        BigDecimal total = detalleService.calcularTotalAplicadoByPago(pagoId);
        return Response.ok().entity("{\"totalAplicado\": " + total + "}").build();
    }

    @GET
    @Path("/pago/{pagoId}/count")
    public Response countByPago(@PathParam("pagoId") Long pagoId) {
        long count = detalleService.countByPago(pagoId);
        return Response.ok().entity("{\"count\": " + count + "}").build();
    }

    @GET
    @Path("/deuda/{deudaId}/count")
    public Response countByDeuda(@PathParam("deudaId") Long deudaId) {
        long count = detalleService.countByDeuda(deudaId);
        return Response.ok().entity("{\"count\": " + count + "}").build();
    }

    @POST
    @Path("/aplicar")
    public Response aplicarPagoADeuda(@Valid PagoDetalleDeudaRequestDTO requestDTO) {
        PagoDetalleDeudaResponseDTO detalle = detalleService.aplicarPagoADeuda(requestDTO);
        return Response.status(Response.Status.CREATED).entity(detalle).build();
    }

    @PUT
    @Path("/{id}/revertir")
    public Response revertirAplicacion(@PathParam("id") Long id, @QueryParam("motivo") String motivo) {
        PagoDetalleDeudaResponseDTO detalle = detalleService.revertirAplicacion(id, motivo);
        return Response.ok(detalle).build();
    }
}
