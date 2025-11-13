package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.academic.application.dto.ProfesorRequestDTO;
import upeu.edu.pe.academic.application.dto.ProfesorResponseDTO;
import upeu.edu.pe.academic.domain.services.ProfesorService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller REST para gestión de profesores
 * Endpoints: listar, buscar, crear, actualizar, eliminar
 */
@Path("/api/v1/profesores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Profesores", description = "Gestión de profesores del sistema académico")
public class ProfesorController {

    @Inject
    ProfesorService profesorService;

    @GET
    @Operation(summary = "Listar todos los profesores", description = "Obtiene lista de todos los profesores activos")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Lista de profesores obtenida exitosamente")
    })
    public Response listarTodos() {
        List<ProfesorResponseDTO> profesores = profesorService.listarTodos();
        ApiResponse<List<ProfesorResponseDTO>> response = new ApiResponse<>(
            true,
            "Profesores listados exitosamente",
            profesores,
            null,
            LocalDateTime.now()
        );
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar profesor por ID", description = "Obtiene un profesor específico por su ID")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profesor encontrado"),
        @APIResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    public Response buscarPorId(
            @Parameter(description = "ID del profesor", required = true, example = "1")
            @PathParam("id") Long id) {
        ProfesorResponseDTO profesor = profesorService.buscarPorId(id);
        ApiResponse<ProfesorResponseDTO> response = new ApiResponse<>(
            true,
            "Profesor encontrado",
            profesor,
            null,
            LocalDateTime.now()
        );
        return Response.ok(response).build();
    }

    @GET
    @Path("/empleado/{empleadoId}")
    @Operation(summary = "Buscar profesor por empleado", description = "Obtiene el profesor asociado a un empleado")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profesor encontrado"),
        @APIResponse(responseCode = "404", description = "No se encontró profesor para este empleado")
    })
    public Response buscarPorEmpleado(
            @Parameter(description = "ID del empleado", required = true, example = "1")
            @PathParam("empleadoId") Long empleadoId) {
        ProfesorResponseDTO profesor = profesorService.buscarPorEmpleado(empleadoId);
        ApiResponse<ProfesorResponseDTO> response = new ApiResponse<>(
            true,
            "Profesor encontrado",
            profesor,
            null,
            LocalDateTime.now()
        );
        return Response.ok(response).build();
    }

    @GET
    @Path("/grado/{gradoAcademico}")
    @Operation(summary = "Listar profesores por grado académico", 
               description = "Obtiene lista de profesores filtrados por grado académico")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profesores listados")
    })
    public Response listarPorGradoAcademico(
            @Parameter(description = "Grado académico (BACHILLER, LICENCIADO, MAGISTER, DOCTOR)", 
                      required = true, example = "DOCTOR")
            @PathParam("gradoAcademico") String gradoAcademico) {
        List<ProfesorResponseDTO> profesores = profesorService.listarPorGradoAcademico(gradoAcademico);
        ApiResponse<List<ProfesorResponseDTO>> response = new ApiResponse<>(
            true,
            "Profesores con grado " + gradoAcademico + " listados",
            profesores,
            null,
            LocalDateTime.now()
        );
        return Response.ok(response).build();
    }

    @GET
    @Path("/categoria/{categoriaDocente}")
    @Operation(summary = "Listar profesores por categoría docente", 
               description = "Obtiene lista de profesores filtrados por categoría")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profesores listados")
    })
    public Response listarPorCategoriaDocente(
            @Parameter(description = "Categoría docente (AUXILIAR, ASOCIADO, PRINCIPAL)", 
                      required = true, example = "PRINCIPAL")
            @PathParam("categoriaDocente") String categoriaDocente) {
        List<ProfesorResponseDTO> profesores = profesorService.listarPorCategoriaDocente(categoriaDocente);
        ApiResponse<List<ProfesorResponseDTO>> response = new ApiResponse<>(
            true,
            "Profesores de categoría " + categoriaDocente + " listados",
            profesores,
            null,
            LocalDateTime.now()
        );
        return Response.ok(response).build();
    }

    @GET
    @Path("/dedicacion/{dedicacion}")
    @Operation(summary = "Listar profesores por dedicación", 
               description = "Obtiene lista de profesores filtrados por tipo de dedicación")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profesores listados")
    })
    public Response listarPorDedicacion(
            @Parameter(description = "Tipo de dedicación (TC, TP, PH)", 
                      required = true, example = "TC")
            @PathParam("dedicacion") String dedicacion) {
        List<ProfesorResponseDTO> profesores = profesorService.listarPorDedicacion(dedicacion);
        ApiResponse<List<ProfesorResponseDTO>> response = new ApiResponse<>(
            true,
            "Profesores con dedicación " + dedicacion + " listados",
            profesores,
            null,
            LocalDateTime.now()
        );
        return Response.ok(response).build();
    }

    @GET
    @Path("/renacyt")
    @Operation(summary = "Listar profesores con código RENACYT", 
               description = "Obtiene lista de profesores que cuentan con código RENACYT")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profesores con RENACYT listados")
    })
    public Response listarConRenacyt() {
        List<ProfesorResponseDTO> profesores = profesorService.listarConRenacyt();
        ApiResponse<List<ProfesorResponseDTO>> response = new ApiResponse<>(
            true,
            "Profesores con código RENACYT listados",
            profesores,
            null,
            LocalDateTime.now()
        );
        return Response.ok(response).build();
    }

    @POST
    @Operation(summary = "Crear nuevo profesor", description = "Crea un nuevo profesor en el sistema")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Profesor creado exitosamente"),
        @APIResponse(responseCode = "400", description = "Datos inválidos o regla de negocio violada"),
        @APIResponse(responseCode = "404", description = "Empleado no encontrado"),
        @APIResponse(responseCode = "409", description = "El empleado ya es profesor")
    })
    public Response crear(@Valid ProfesorRequestDTO dto) {
        ProfesorResponseDTO profesor = profesorService.crear(dto);
        ApiResponse<ProfesorResponseDTO> response = new ApiResponse<>(
            true,
            "Profesor creado exitosamente",
            profesor,
            null,
            LocalDateTime.now()
        );
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar profesor", description = "Actualiza los datos de un profesor existente")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profesor actualizado exitosamente"),
        @APIResponse(responseCode = "400", description = "Datos inválidos o regla de negocio violada"),
        @APIResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    public Response actualizar(
            @Parameter(description = "ID del profesor", required = true, example = "1")
            @PathParam("id") Long id,
            @Valid ProfesorRequestDTO dto) {
        ProfesorResponseDTO profesor = profesorService.actualizar(id, dto);
        ApiResponse<ProfesorResponseDTO> response = new ApiResponse<>(
            true,
            "Profesor actualizado exitosamente",
            profesor,
            null,
            LocalDateTime.now()
        );
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar profesor", description = "Elimina lógicamente un profesor del sistema")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Profesor eliminado exitosamente"),
        @APIResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    public Response eliminar(
            @Parameter(description = "ID del profesor", required = true, example = "1")
            @PathParam("id") Long id) {
        profesorService.eliminar(id);
        ApiResponse<Void> response = new ApiResponse<>(
            true,
            "Profesor eliminado exitosamente",
            null,
            null,
            LocalDateTime.now()
        );
        return Response.ok(response).build();
    }
}
