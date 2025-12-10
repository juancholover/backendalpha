package upeu.edu.pe.finance.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaResponseDTO;
import upeu.edu.pe.finance.application.mapper.PagoDetalleDeudaMapper;
import upeu.edu.pe.finance.domain.commands.AplicarPagoADeudaCommand;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import upeu.edu.pe.finance.domain.services.PagoDetalleDeudaService;
import upeu.edu.pe.finance.domain.usecases.AplicarPagoADeudaUseCase;
import upeu.edu.pe.finance.domain.usecases.RevertirAplicacionPagoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de detalles de pagos aplicados a deudas.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/pago-detalles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Detalle de Pagos", description = "Gestión de detalles de pagos aplicados a deudas")
public class PagoDetalleDeudaController {

    // Use Cases para operaciones de escritura
    @Inject
    AplicarPagoADeudaUseCase aplicarPagoADeudaUseCase;

    @Inject
    RevertirAplicacionPagoUseCase revertirAplicacionUseCase;

    // Service para operaciones de lectura
    @Inject
    PagoDetalleDeudaService detalleService;

    @Inject
    PagoDetalleDeudaMapper detalleMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/pago/{pagoId}")
    @Operation(summary = "Listar detalles por pago")
    public Response findByPago(@PathParam("pagoId") Long pagoId) {
        List<PagoDetalleDeudaResponseDTO> detalles = detalleService.findByPago(pagoId);
        return Response.ok(ApiResponse.success("Detalles obtenidos", detalles)).build();
    }

    @GET
    @Path("/deuda/{deudaId}")
    @Operation(summary = "Listar detalles por deuda")
    public Response findByDeuda(@PathParam("deudaId") Long deudaId) {
        List<PagoDetalleDeudaResponseDTO> detalles = detalleService.findByDeuda(deudaId);
        return Response.ok(ApiResponse.success("Detalles obtenidos", detalles)).build();
    }

    @GET
    @Path("/pago/{pagoId}/activos")
    @Operation(summary = "Listar detalles activos por pago")
    public Response findActivosByPago(@PathParam("pagoId") Long pagoId) {
        List<PagoDetalleDeudaResponseDTO> detalles = detalleService.findActivosByPago(pagoId);
        return Response.ok(ApiResponse.success("Detalles activos obtenidos", detalles)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar detalle por ID")
    public Response findById(@PathParam("id") Long id) {
        PagoDetalleDeudaResponseDTO detalle = detalleService.findById(id);
        return Response.ok(ApiResponse.success("Detalle encontrado", detalle)).build();
    }

    @GET
    @Path("/deuda/{deudaId}/total-aplicado")
    @Operation(summary = "Calcular total aplicado a deuda")
    public Response calcularTotalAplicadoByDeuda(@PathParam("deudaId") Long deudaId) {
        BigDecimal total = detalleService.calcularTotalAplicadoByDeuda(deudaId);
        return Response.ok(ApiResponse.success("Total calculado", total)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Path("/aplicar")
    @Operation(summary = "Aplicar pago a deuda")
    public Response aplicarPagoADeuda(@Valid PagoDetalleDeudaRequestDTO dto) {

        // Convertir DTO a Command
        AplicarPagoADeudaCommand command = new AplicarPagoADeudaCommand(
                dto.getPagoId(),
                dto.getDeudaId(),
                dto.getMontoAplicado());

        // Ejecutar Use Case
        PagoDetalleDeuda detalle = aplicarPagoADeudaUseCase.execute(command);

        // Convertir a DTO
        PagoDetalleDeudaResponseDTO response = detalleMapper.toResponseDTO(detalle);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Pago aplicado a deuda exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}/revertir")
    @Operation(summary = "Revertir aplicación de pago")
    public Response revertirAplicacion(@PathParam("id") Long id, @QueryParam("motivo") String motivo) {

        // Ejecutar Use Case
        PagoDetalleDeuda detalle = revertirAplicacionUseCase.execute(id, motivo);

        // Convertir a DTO
        PagoDetalleDeudaResponseDTO response = detalleMapper.toResponseDTO(detalle);

        return Response.ok(ApiResponse.success("Aplicación revertida exitosamente", response)).build();
    }
}
