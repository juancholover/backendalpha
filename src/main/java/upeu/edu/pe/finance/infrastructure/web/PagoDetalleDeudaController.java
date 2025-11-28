package upeu.edu.pe.finance.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaResponseDTO;
import upeu.edu.pe.finance.application.services.PagoDetalleDeudaApplicationService;

import java.util.List;

@Path("/api/v1/pago-detalles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoDetalleDeudaController {

    @Inject
    PagoDetalleDeudaApplicationService applicationService;

    @GET
    @Path("/pago/{pagoId}")
    public Response findByPago(@PathParam("pagoId") Long pagoId) {
        List<PagoDetalleDeudaResponseDTO> detalles = applicationService.findByPago(pagoId);
        return Response.ok(detalles).build();
    }

    @GET
    @Path("/deuda/{deudaId}")
    public Response findByDeuda(@PathParam("deudaId") Long deudaId) {
        List<PagoDetalleDeudaResponseDTO> detalles = applicationService.findByDeuda(deudaId);
        return Response.ok(detalles).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        PagoDetalleDeudaResponseDTO detalle = applicationService.findById(id);
        return Response.ok(detalle).build();
    }

    @POST
    @Path("/aplicar")
    public Response aplicarPagoADeuda(@Valid PagoDetalleDeudaRequestDTO requestDTO) {
        PagoDetalleDeudaResponseDTO detalle = applicationService.aplicarPagoADeuda(requestDTO);
        return Response.status(Response.Status.CREATED).entity(detalle).build();
    }

    @PUT
    @Path("/{id}/revertir")
    public Response revertirAplicacion(@PathParam("id") Long id, @QueryParam("motivo") String motivo) {
        PagoDetalleDeudaResponseDTO detalle = applicationService.revertirAplicacion(id, motivo);
        return Response.ok(detalle).build();
    }
}
