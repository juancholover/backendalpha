package upeu.edu.pe.enrollment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.enrollment.application.dto.PeriodoAcademicoRequestDTO;
import upeu.edu.pe.enrollment.application.dto.PeriodoAcademicoResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.PeriodoAcademicoMapper;
import upeu.edu.pe.enrollment.domain.commands.CrearPeriodoAcademicoCommand;
import upeu.edu.pe.enrollment.domain.entities.PeriodoAcademico;
import upeu.edu.pe.enrollment.domain.services.PeriodoAcademicoService;
import upeu.edu.pe.enrollment.domain.usecases.CrearPeriodoAcademicoUseCase;
import upeu.edu.pe.enrollment.domain.usecases.EliminarPeriodoAcademicoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para períodos académicos.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/periodos-academicos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Períodos Académicos", description = "Gestión de semestres y períodos académicos")
public class PeriodoAcademicoController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearPeriodoAcademicoUseCase crearUseCase;

    @Inject
    EliminarPeriodoAcademicoUseCase eliminarUseCase;

    // Service para operaciones de lectura
    @Inject
    PeriodoAcademicoService periodoService;

    @Inject
    PeriodoAcademicoMapper periodoMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar periodos por universidad")
    public Response findByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<PeriodoAcademicoResponseDTO> periodos = periodoService.findByUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Períodos obtenidos", periodos)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar periodo por ID")
    public Response findById(@PathParam("id") Long id) {
        PeriodoAcademicoResponseDTO periodo = periodoService.findById(id);
        return Response.ok(ApiResponse.success("Período encontrado", periodo)).build();
    }

    @GET
    @Path("/universidad/{universidadId}/actual")
    @Operation(summary = "Obtener periodo actual")
    public Response findActualByUniversidad(@PathParam("universidadId") Long universidadId) {
        PeriodoAcademicoResponseDTO periodo = periodoService.findActualByUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Período actual", periodo)).build();
    }

    @GET
    @Path("/codigo/{codigo}/universidad/{universidadId}")
    @Operation(summary = "Buscar por código")
    public Response findByCodigoAndUniversidad(
            @PathParam("codigo") String codigo,
            @PathParam("universidadId") Long universidadId) {
        PeriodoAcademicoResponseDTO periodo = periodoService.findByCodigoAndUniversidad(codigo, universidadId);
        return Response.ok(ApiResponse.success("Período encontrado", periodo)).build();
    }

    @GET
    @Path("/anio/{anio}/universidad/{universidadId}")
    @Operation(summary = "Listar por año")
    public Response findByAnioAndUniversidad(
            @PathParam("anio") Integer anio,
            @PathParam("universidadId") Long universidadId) {
        List<PeriodoAcademicoResponseDTO> periodos = periodoService.findByAnioAndUniversidad(anio, universidadId);
        return Response.ok(ApiResponse.success("Períodos del año " + anio, periodos)).build();
    }

    @GET
    @Path("/estado/{estado}/universidad/{universidadId}")
    @Operation(summary = "Listar por estado")
    public Response findByEstadoAndUniversidad(
            @PathParam("estado") String estado,
            @PathParam("universidadId") Long universidadId) {
        List<PeriodoAcademicoResponseDTO> periodos = periodoService.findByEstadoAndUniversidad(estado, universidadId);
        return Response.ok(ApiResponse.success("Períodos en estado " + estado, periodos)).build();
    }

    @GET
    @Path("/universidad/{universidadId}/activos")
    @Operation(summary = "Listar periodos activos")
    public Response findActivosAndUniversidad(@PathParam("universidadId") Long universidadId) {
        List<PeriodoAcademicoResponseDTO> periodos = periodoService.findActivosAndUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Períodos activos", periodos)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear periodo académico")
    public Response create(@Valid PeriodoAcademicoRequestDTO dto) {
        CrearPeriodoAcademicoCommand command = new CrearPeriodoAcademicoCommand(
                dto.getCodigoPeriodo(),
                dto.getNombre(),
                dto.getAnio(),
                dto.getTipoPeriodo(),
                dto.getFechaInicio(),
                dto.getFechaFin(),
                dto.getFechaInicioMatricula(),
                dto.getFechaFinMatricula(),
                dto.getEsActual());

        PeriodoAcademico periodo = crearUseCase.execute(command);
        PeriodoAcademicoResponseDTO response = periodoMapper.toResponseDTO(periodo);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Período creado", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar periodo académico")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Período eliminado", null)).build();
    }
}
