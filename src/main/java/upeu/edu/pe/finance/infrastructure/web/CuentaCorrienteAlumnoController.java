package upeu.edu.pe.finance.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoRequestDTO;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoResponseDTO;
import upeu.edu.pe.finance.application.mapper.CuentaCorrienteAlumnoMapper;
import upeu.edu.pe.finance.domain.commands.CrearCuentaCorrienteCommand;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.services.CuentaCorrienteAlumnoService;
import upeu.edu.pe.finance.domain.usecases.CrearCuentaCorrienteUseCase;
import upeu.edu.pe.finance.domain.usecases.AplicarPagoCuentaUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de cuentas corrientes de estudiantes.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/cuentas-corrientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cuentas Corrientes", description = "Gestión de cuentas corrientes de estudiantes")
public class CuentaCorrienteAlumnoController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearCuentaCorrienteUseCase crearCuentaUseCase;

    @Inject
    AplicarPagoCuentaUseCase aplicarPagoUseCase;

    // Service para operaciones de lectura
    @Inject
    CuentaCorrienteAlumnoService cuentaService;

    @Inject
    CuentaCorrienteAlumnoMapper cuentaMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar cuentas por universidad")
    public Response findByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findByUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Cuentas obtenidas", cuentas)).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}")
    @Operation(summary = "Listar cuentas por estudiante")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findByEstudiante(estudianteId);
        return Response.ok(ApiResponse.success("Cuentas obtenidas", cuentas)).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/vencidas")
    @Operation(summary = "Listar cuentas vencidas por estudiante")
    public Response findVencidasByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findVencidasByEstudiante(estudianteId);
        return Response.ok(ApiResponse.success("Cuentas vencidas obtenidas", cuentas)).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/pendientes")
    @Operation(summary = "Listar cuentas pendientes por estudiante")
    public Response findPendientesByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<CuentaCorrienteAlumnoResponseDTO> cuentas = cuentaService.findPendientesByEstudiante(estudianteId);
        return Response.ok(ApiResponse.success("Cuentas pendientes obtenidas", cuentas)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar cuenta por ID")
    public Response findById(@PathParam("id") Long id) {
        CuentaCorrienteAlumnoResponseDTO cuenta = cuentaService.findById(id);
        return Response.ok(ApiResponse.success("Cuenta encontrada", cuenta)).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/deuda-total")
    @Operation(summary = "Calcular deuda total del estudiante")
    public Response calcularDeudaTotalByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        BigDecimal deudaTotal = cuentaService.calcularDeudaTotalByEstudiante(estudianteId);
        return Response.ok(ApiResponse.success("Deuda total calculada", deudaTotal)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear cuenta corriente")
    public Response create(@Valid CuentaCorrienteAlumnoRequestDTO dto) {

        // Convertir DTO a Command
        CrearCuentaCorrienteCommand command = new CrearCuentaCorrienteCommand(
                dto.getEstudianteId(),
                dto.getConcepto(),
                dto.getMonto(),
                dto.getFechaEmision(),
                dto.getFechaVencimiento(),
                dto.getTipoCargo(),
                dto.getObservaciones());

        // Ejecutar Use Case
        CuentaCorrienteAlumno cuenta = crearCuentaUseCase.execute(command);

        // Convertir a DTO
        CuentaCorrienteAlumnoResponseDTO response = cuentaMapper.toResponseDTO(cuenta);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Cuenta corriente creada exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}/aplicar-pago")
    @Operation(summary = "Aplicar pago a cuenta corriente")
    public Response aplicarPago(@PathParam("id") Long id, @QueryParam("montoPago") BigDecimal montoPago) {

        // Ejecutar Use Case
        CuentaCorrienteAlumno cuenta = aplicarPagoUseCase.execute(id, montoPago);

        // Convertir a DTO
        CuentaCorrienteAlumnoResponseDTO response = cuentaMapper.toResponseDTO(cuenta);

        return Response.ok(ApiResponse.success("Pago aplicado exitosamente", response)).build();
    }
}
