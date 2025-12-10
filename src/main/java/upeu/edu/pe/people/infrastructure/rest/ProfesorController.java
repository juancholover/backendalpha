package upeu.edu.pe.people.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.people.application.dto.ProfesorRequestDTO;
import upeu.edu.pe.people.application.dto.ProfesorResponseDTO;
import upeu.edu.pe.people.application.mapper.ProfesorMapper;
import upeu.edu.pe.people.domain.commands.CrearProfesorCommand;
import upeu.edu.pe.people.domain.commands.ActualizarProfesorCommand;
import upeu.edu.pe.people.domain.entities.Profesor;
import upeu.edu.pe.people.domain.services.ProfesorService;
import upeu.edu.pe.people.domain.usecases.CrearProfesorUseCase;
import upeu.edu.pe.people.domain.usecases.ActualizarProfesorUseCase;
import upeu.edu.pe.people.domain.usecases.EliminarProfesorUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de profesores.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/profesores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Profesores", description = "Gestión de profesores del sistema académico")
public class ProfesorController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearProfesorUseCase crearProfesorUseCase;

    @Inject
    ActualizarProfesorUseCase actualizarProfesorUseCase;

    @Inject
    EliminarProfesorUseCase eliminarProfesorUseCase;

    // Service para operaciones de lectura
    @Inject
    ProfesorService profesorService;

    @Inject
    ProfesorMapper profesorMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar todos los profesores")
    public Response listarTodos() {
        List<ProfesorResponseDTO> profesores = profesorService.listarTodos();
        return Response.ok(ApiResponse.success("Profesores obtenidos exitosamente", profesores)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar profesor por ID")
    public Response buscarPorId(@PathParam("id") Long id) {
        ProfesorResponseDTO profesor = profesorService.buscarPorId(id);
        return Response.ok(ApiResponse.success("Profesor encontrado", profesor)).build();
    }

    @GET
    @Path("/persona/{personaId}")
    @Operation(summary = "Buscar profesor por persona")
    public Response buscarPorPersona(@PathParam("personaId") Long personaId) {
        ProfesorResponseDTO profesor = profesorService.buscarPorPersona(personaId);
        return Response.ok(ApiResponse.success("Profesor encontrado", profesor)).build();
    }

    @GET
    @Path("/grado/{gradoAcademico}")
    @Operation(summary = "Listar profesores por grado académico")
    public Response listarPorGradoAcademico(@PathParam("gradoAcademico") String gradoAcademico) {
        List<ProfesorResponseDTO> profesores = profesorService.listarPorGradoAcademico(gradoAcademico);
        return Response.ok(ApiResponse.success("Profesores filtrados por grado", profesores)).build();
    }

    @GET
    @Path("/categoria/{categoriaDocente}")
    @Operation(summary = "Listar profesores por categoría docente")
    public Response listarPorCategoriaDocente(@PathParam("categoriaDocente") String categoriaDocente) {
        List<ProfesorResponseDTO> profesores = profesorService.listarPorCategoriaDocente(categoriaDocente);
        return Response.ok(ApiResponse.success("Profesores filtrados por categoría", profesores)).build();
    }

    @GET
    @Path("/dedicacion/{dedicacion}")
    @Operation(summary = "Listar profesores por dedicación")
    public Response listarPorDedicacion(@PathParam("dedicacion") String dedicacion) {
        List<ProfesorResponseDTO> profesores = profesorService.listarPorDedicacion(dedicacion);
        return Response.ok(ApiResponse.success("Profesores filtrados por dedicación", profesores)).build();
    }

    @GET
    @Path("/renacyt")
    @Operation(summary = "Listar profesores con RENACYT")
    public Response listarConRenacyt() {
        List<ProfesorResponseDTO> profesores = profesorService.listarConRenacyt();
        return Response.ok(ApiResponse.success("Profesores con RENACYT", profesores)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear profesor")
    public Response crear(@Valid ProfesorRequestDTO dto) {

        // Convertir DTO a Command
        CrearProfesorCommand command = new CrearProfesorCommand(
                dto.getEmpleadoId(),
                dto.getGradoAcademico(),
                dto.getCategoriaDocente(),
                dto.getDedicacion(),
                dto.getCodigoRenacyt(),
                dto.getEspecialidad());

        // Ejecutar Use Case
        Profesor profesor = crearProfesorUseCase.execute(command);

        // Convertir a DTO
        ProfesorResponseDTO response = profesorMapper.toResponseDTO(profesor);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Profesor creado exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar profesor")
    public Response actualizar(@PathParam("id") Long id, @Valid ProfesorRequestDTO dto) {

        // Convertir DTO a Command
        ActualizarProfesorCommand command = new ActualizarProfesorCommand(
                id,
                dto.getGradoAcademico(),
                dto.getCategoriaDocente(),
                dto.getDedicacion(),
                dto.getCodigoRenacyt(),
                dto.getEspecialidad());

        // Ejecutar Use Case
        Profesor profesor = actualizarProfesorUseCase.execute(command);

        // Convertir a DTO
        ProfesorResponseDTO response = profesorMapper.toResponseDTO(profesor);

        return Response.ok(ApiResponse.success("Profesor actualizado exitosamente", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar profesor")
    public Response eliminar(@PathParam("id") Long id) {

        // Ejecutar Use Case
        eliminarProfesorUseCase.execute(id);

        return Response.ok(ApiResponse.success("Profesor eliminado exitosamente", null)).build();
    }
}
