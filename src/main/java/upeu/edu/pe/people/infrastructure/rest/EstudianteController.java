package upeu.edu.pe.people.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.people.application.dto.EstudianteRequestDTO;
import upeu.edu.pe.people.application.dto.EstudianteResponseDTO;
import upeu.edu.pe.people.application.mapper.EstudianteMapper;
import upeu.edu.pe.people.domain.commands.ActualizarEstudianteCommand;
import upeu.edu.pe.people.domain.commands.CambiarEstadoAcademicoCommand;
import upeu.edu.pe.people.domain.commands.CrearEstudianteCommand;
import upeu.edu.pe.people.domain.entities.Estudiante;
import upeu.edu.pe.people.domain.services.EstudianteService;
import upeu.edu.pe.people.domain.usecases.ActualizarEstudianteUseCase;
import upeu.edu.pe.people.domain.usecases.CambiarEstadoAcademicoUseCase;
import upeu.edu.pe.people.domain.usecases.CrearEstudianteUseCase;
import upeu.edu.pe.people.domain.usecases.EliminarEstudianteUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para estudiantes.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/estudiantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Estudiantes", description = "API para gestión de estudiantes")
public class EstudianteController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearEstudianteUseCase crearUseCase;

    @Inject
    ActualizarEstudianteUseCase actualizarUseCase;

    @Inject
    CambiarEstadoAcademicoUseCase cambiarEstadoUseCase;

    @Inject
    EliminarEstudianteUseCase eliminarUseCase;

    // Service para operaciones de lectura
    @Inject
    EstudianteService estudianteService;

    @Inject
    EstudianteMapper estudianteMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar todos los estudiantes")
    public Response findAll() {
        List<EstudianteResponseDTO> estudiantes = estudianteService.findAll();
        return Response.ok(ApiResponse.success("Estudiantes listados", estudiantes)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener estudiante por ID")
    public Response findById(@PathParam("id") Long id) {
        EstudianteResponseDTO estudiante = estudianteService.findById(id);
        return Response.ok(ApiResponse.success("Estudiante encontrado", estudiante)).build();
    }

    @GET
    @Path("/codigo/{codigoEstudiante}")
    @Operation(summary = "Buscar estudiante por código")
    public Response findByCodigoEstudiante(@PathParam("codigoEstudiante") String codigoEstudiante) {
        EstudianteResponseDTO estudiante = estudianteService.findByCodigoEstudiante(codigoEstudiante);
        return Response.ok(ApiResponse.success("Estudiante encontrado", estudiante)).build();
    }

    @GET
    @Path("/programa/{programaId}")
    @Operation(summary = "Listar por programa académico")
    public Response findByProgramaAcademico(@PathParam("programaId") Long programaId) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.findByProgramaAcademico(programaId);
        return Response.ok(ApiResponse.success("Estudiantes del programa", estudiantes)).build();
    }

    @GET
    @Path("/programa/{programaId}/activos")
    @Operation(summary = "Listar estudiantes activos de un programa")
    public Response findEstudiantesActivos(@PathParam("programaId") Long programaId) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.findEstudiantesActivos(programaId);
        return Response.ok(ApiResponse.success("Estudiantes activos", estudiantes)).build();
    }

    @GET
    @Path("/estado/{estado}")
    @Operation(summary = "Listar por estado académico")
    public Response findByEstadoAcademico(@PathParam("estado") String estado) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.findByEstadoAcademico(estado);
        return Response.ok(ApiResponse.success("Estudiantes por estado", estudiantes)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear nuevo estudiante")
    public Response create(@Valid EstudianteRequestDTO dto) {
        CrearEstudianteCommand command = new CrearEstudianteCommand(
                dto.getPersonaId(),
                dto.getProgramaAcademicoId(),
                dto.getCodigoEstudiante(),
                dto.getFechaIngreso(),
                dto.getCicloActual(),
                dto.getModalidadIngreso(),
                dto.getTipoEstudiante());

        Estudiante estudiante = crearUseCase.execute(command);
        EstudianteResponseDTO response = estudianteMapper.toResponseDTO(estudiante);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Estudiante creado", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar estudiante")
    public Response update(@PathParam("id") Long id, @Valid EstudianteRequestDTO dto) {
        ActualizarEstudianteCommand command = new ActualizarEstudianteCommand(
                id,
                dto.getProgramaAcademicoId(),
                dto.getCodigoEstudiante(),
                dto.getCicloActual(),
                dto.getCreditosAprobados(),
                dto.getCreditosCursando(),
                dto.getCreditosObligatoriosAprobados(),
                dto.getCreditosElectivosAprobados(),
                dto.getPromedioPonderado(),
                dto.getEstadoAcademico(),
                dto.getTipoEstudiante());

        Estudiante estudiante = actualizarUseCase.execute(command);
        EstudianteResponseDTO response = estudianteMapper.toResponseDTO(estudiante);

        return Response.ok(ApiResponse.success("Estudiante actualizado", response)).build();
    }

    @PATCH
    @Path("/{id}/estado")
    @Operation(summary = "Cambiar estado académico")
    public Response cambiarEstadoAcademico(
            @PathParam("id") Long id,
            @QueryParam("nuevoEstado") String nuevoEstado) {
        CambiarEstadoAcademicoCommand command = new CambiarEstadoAcademicoCommand(id, nuevoEstado);
        Estudiante estudiante = cambiarEstadoUseCase.execute(command);
        EstudianteResponseDTO response = estudianteMapper.toResponseDTO(estudiante);

        return Response.ok(ApiResponse.success("Estado actualizado", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar estudiante")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Estudiante eliminado", null)).build();
    }
}
