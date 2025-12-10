package upeu.edu.pe.finance.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.finance.application.dto.PagoRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoResponseDTO;
import upeu.edu.pe.finance.application.mapper.PagoMapper;
import upeu.edu.pe.finance.domain.commands.RegistrarPagoCommand;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.services.PagoService;
import upeu.edu.pe.finance.domain.usecases.RegistrarPagoUseCase;
import upeu.edu.pe.finance.domain.usecases.AnularPagoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gestión de pagos.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/pagos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Pagos", description = "Gestión de pagos de estudiantes")
public class PagoController {

    // Use Cases para operaciones de escritura
    @Inject
    RegistrarPagoUseCase registrarPagoUseCase;

    @Inject
    AnularPagoUseCase anularPagoUseCase;

    // Service para operaciones de lectura
    @Inject
    PagoService pagoService;

    @Inject
    PagoMapper pagoMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar pagos por universidad")
    public Response findByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<PagoResponseDTO> pagos = pagoService.findByUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Pagos obtenidos", pagos)).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}")
    @Operation(summary = "Listar pagos por estudiante")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<PagoResponseDTO> pagos = pagoService.findByEstudiante(estudianteId);
        return Response.ok(ApiResponse.success("Pagos obtenidos", pagos)).build();
    }

    @GET
    @Path("/recibo/{numeroRecibo}")
    @Operation(summary = "Buscar pago por número de recibo")
    public Response findByNumeroRecibo(
            @PathParam("numeroRecibo") String numeroRecibo,
            @QueryParam("universidadId") Long universidadId) {
        PagoResponseDTO pago = pagoService.findByNumeroRecibo(numeroRecibo, universidadId);
        return Response.ok(ApiResponse.success("Pago encontrado", pago)).build();
    }

    @GET
    @Path("/estado/{estado}")
    @Operation(summary = "Listar pagos por estado")
    public Response findByEstado(@PathParam("estado") String estado) {
        List<PagoResponseDTO> pagos = pagoService.findByEstado(estado);
        return Response.ok(ApiResponse.success("Pagos obtenidos", pagos)).build();
    }

    @GET
    @Path("/metodo-pago/{metodoPago}")
    @Operation(summary = "Listar pagos por método de pago")
    public Response findByMetodoPago(@PathParam("metodoPago") String metodoPago) {
        List<PagoResponseDTO> pagos = pagoService.findByMetodoPago(metodoPago);
        return Response.ok(ApiResponse.success("Pagos obtenidos", pagos)).build();
    }

    @GET
    @Path("/fecha/{fecha}")
    @Operation(summary = "Listar pagos por fecha")
    public Response findByFecha(@PathParam("fecha") String fecha) {
        LocalDate fechaPago = LocalDate.parse(fecha);
        List<PagoResponseDTO> pagos = pagoService.findByFecha(fechaPago);
        return Response.ok(ApiResponse.success("Pagos obtenidos", pagos)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar pago por ID")
    public Response findById(@PathParam("id") Long id) {
        PagoResponseDTO pago = pagoService.findById(id);
        return Response.ok(ApiResponse.success("Pago encontrado", pago)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Registrar pago")
    public Response create(@Valid PagoRequestDTO dto) {

        // Convertir DTO a Command
        RegistrarPagoCommand command = new RegistrarPagoCommand(
                dto.getEstudianteId(),
                dto.getNumeroRecibo(),
                dto.getMontoPagado(),
                dto.getFechaPago(),
                dto.getMetodoPago(),
                dto.getCajero(),
                dto.getObservaciones());

        // Ejecutar Use Case
        Pago pago = registrarPagoUseCase.execute(command);

        // Convertir a DTO
        PagoResponseDTO response = pagoMapper.toResponseDTO(pago);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Pago registrado exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}/anular")
    @Operation(summary = "Anular pago")
    public Response anular(@PathParam("id") Long id, @QueryParam("motivo") String motivo) {

        // Ejecutar Use Case
        Pago pago = anularPagoUseCase.execute(id, motivo);

        // Convertir a DTO
        PagoResponseDTO response = pagoMapper.toResponseDTO(pago);

        return Response.ok(ApiResponse.success("Pago anulado exitosamente", response)).build();
    }
}
