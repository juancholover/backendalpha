package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.academic.application.dto.EstudianteRequestDTO;
import upeu.edu.pe.academic.application.dto.EstudianteResponseDTO;
import upeu.edu.pe.academic.domain.services.EstudianteService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/v1/estudiantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Estudiantes", description = "API para gestión de estudiantes")
public class EstudianteController {

    @Inject
    EstudianteService estudianteService;

    @GET
    @Operation(summary = "Listar todos los estudiantes", description = "Obtiene una lista de todos los estudiantes activos en el sistema")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de estudiantes obtenida exitosamente",
                     content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public Response findAll() {
        List<EstudianteResponseDTO> estudiantes = estudianteService.findAll();
        return Response.ok(ApiResponse.success("Estudiantes listados exitosamente", estudiantes)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener estudiante por ID", description = "Obtiene los detalles de un estudiante específico por su ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Estudiante encontrado"),
        @APIResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public Response findById(
            @Parameter(description = "ID del estudiante", required = true)
            @PathParam("id") Long id) {
        EstudianteResponseDTO estudiante = estudianteService.findById(id);
        return Response.ok(ApiResponse.success("Estudiante encontrado", estudiante)).build();
    }

    @GET
    @Path("/codigo/{codigoEstudiante}")
    @Operation(summary = "Buscar estudiante por código", description = "Busca un estudiante por su código único")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Estudiante encontrado"),
        @APIResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public Response findByCodigoEstudiante(
            @Parameter(description = "Código del estudiante", required = true)
            @PathParam("codigoEstudiante") String codigoEstudiante) {
        EstudianteResponseDTO estudiante = estudianteService.findByCodigoEstudiante(codigoEstudiante);
        return Response.ok(ApiResponse.success("Estudiante encontrado", estudiante)).build();
    }

    @GET
    @Path("/programa/{programaId}")
    @Operation(summary = "Listar estudiantes por programa académico", description = "Obtiene todos los estudiantes de un programa académico específico")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de estudiantes obtenida")
    })
    public Response findByProgramaAcademico(
            @Parameter(description = "ID del programa académico", required = true)
            @PathParam("programaId") Long programaId) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.findByProgramaAcademico(programaId);
        return Response.ok(ApiResponse.success("Estudiantes del programa listados", estudiantes)).build();
    }

    @GET
    @Path("/programa/{programaId}/activos")
    @Operation(summary = "Listar estudiantes activos de un programa", description = "Obtiene todos los estudiantes con estado académico ACTIVO de un programa")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de estudiantes activos obtenida")
    })
    public Response findEstudiantesActivos(
            @Parameter(description = "ID del programa académico", required = true)
            @PathParam("programaId") Long programaId) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.findEstudiantesActivos(programaId);
        return Response.ok(ApiResponse.success("Estudiantes activos listados", estudiantes)).build();
    }

    @GET
    @Path("/estado/{estado}")
    @Operation(summary = "Listar estudiantes por estado académico", description = "Filtra estudiantes por su estado académico (ACTIVO, RETIRADO, EGRESADO, etc.)")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de estudiantes obtenida")
    })
    public Response findByEstadoAcademico(
            @Parameter(description = "Estado académico (ACTIVO, RETIRADO, EGRESADO, GRADUADO, LICENCIA)", required = true)
            @PathParam("estado") String estado) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.findByEstadoAcademico(estado);
        return Response.ok(ApiResponse.success("Estudiantes filtrados por estado", estudiantes)).build();
    }

    @POST
    @Operation(summary = "Crear nuevo estudiante", description = "Registra un nuevo estudiante en el sistema asociándolo a una persona y programa académico")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Estudiante creado exitosamente"),
        @APIResponse(responseCode = "400", description = "Datos de entrada inválidos o regla de negocio violada"),
        @APIResponse(responseCode = "404", description = "Persona o programa académico no encontrado"),
        @APIResponse(responseCode = "409", description = "El estudiante ya existe")
    })
    public Response create(
            @Parameter(description = "Datos del estudiante a crear", required = true)
            @Valid EstudianteRequestDTO dto) {
        EstudianteResponseDTO estudiante = estudianteService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Estudiante creado exitosamente", estudiante))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar estudiante", description = "Actualiza los datos de un estudiante existente")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Estudiante actualizado exitosamente"),
        @APIResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @APIResponse(responseCode = "404", description = "Estudiante no encontrado"),
        @APIResponse(responseCode = "409", description = "Conflicto con datos existentes")
    })
    public Response update(
            @Parameter(description = "ID del estudiante", required = true)
            @PathParam("id") Long id,
            @Parameter(description = "Datos actualizados del estudiante", required = true)
            @Valid EstudianteRequestDTO dto) {
        EstudianteResponseDTO estudiante = estudianteService.update(id, dto);
        return Response.ok(ApiResponse.success("Estudiante actualizado exitosamente", estudiante)).build();
    }

    @PATCH
    @Path("/{id}/estado")
    @Operation(summary = "Cambiar estado académico", description = "Cambia el estado académico de un estudiante (ACTIVO, RETIRADO, EGRESADO, GRADUADO, LICENCIA)")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @APIResponse(responseCode = "400", description = "Estado inválido"),
        @APIResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public Response cambiarEstadoAcademico(
            @Parameter(description = "ID del estudiante", required = true)
            @PathParam("id") Long id,
            @Parameter(description = "Nuevo estado académico", required = true)
            @QueryParam("nuevoEstado") String nuevoEstado) {
        EstudianteResponseDTO estudiante = estudianteService.cambiarEstadoAcademico(id, nuevoEstado);
        return Response.ok(ApiResponse.success("Estado académico actualizado", estudiante)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar estudiante", description = "Elimina lógicamente un estudiante del sistema")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Estudiante eliminado exitosamente"),
        @APIResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public Response delete(
            @Parameter(description = "ID del estudiante", required = true)
            @PathParam("id") Long id) {
        estudianteService.delete(id);
        return Response.ok(ApiResponse.success("Estudiante eliminado exitosamente")).build();
    }
}
