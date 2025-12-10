package upeu.edu.pe.curriculum.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.curriculum.application.dto.ProgramaAcademicoRequestDTO;
import upeu.edu.pe.curriculum.application.dto.ProgramaAcademicoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.ProgramaAcademicoMapper;
import upeu.edu.pe.curriculum.domain.commands.CrearProgramaAcademicoCommand;
import upeu.edu.pe.curriculum.domain.commands.ActualizarProgramaAcademicoCommand;
import upeu.edu.pe.curriculum.domain.entities.ProgramaAcademico;
import upeu.edu.pe.curriculum.domain.services.ProgramaAcademicoService;
import upeu.edu.pe.curriculum.domain.usecases.CrearProgramaAcademicoUseCase;
import upeu.edu.pe.curriculum.domain.usecases.ActualizarProgramaAcademicoUseCase;
import upeu.edu.pe.curriculum.domain.usecases.EliminarProgramaAcademicoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de programas académicos.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/programas-academicos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Programas Académicos", description = "Gestión de carreras y programas de estudio")
public class ProgramaAcademicoController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearProgramaAcademicoUseCase crearProgramaUseCase;

    @Inject
    ActualizarProgramaAcademicoUseCase actualizarProgramaUseCase;

    @Inject
    EliminarProgramaAcademicoUseCase eliminarProgramaUseCase;

    // Service para operaciones de lectura
    @Inject
    ProgramaAcademicoService programaService;

    @Inject
    ProgramaAcademicoMapper programaMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar programas académicos")
    public Response findAll() {
        List<ProgramaAcademicoResponseDTO> programas = programaService.findAll();
        return Response.ok(ApiResponse.success("Programas académicos obtenidos", programas)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar programa por ID")
    public Response findById(@PathParam("id") Long id) {
        ProgramaAcademicoResponseDTO programa = programaService.findById(id);
        return Response.ok(ApiResponse.success("Programa académico obtenido", programa)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Operation(summary = "Buscar programa por código")
    public Response findByCodigo(@PathParam("codigo") String codigo) {
        ProgramaAcademicoResponseDTO programa = programaService.findByCodigo(codigo);
        return Response.ok(ApiResponse.success("Programa académico obtenido", programa)).build();
    }

    @GET
    @Path("/unidad/{unidadId}")
    @Operation(summary = "Buscar programas por unidad organizativa")
    public Response findByUnidadOrganizativa(@PathParam("unidadId") Long unidadId) {
        List<ProgramaAcademicoResponseDTO> programas = programaService.findByUnidadOrganizativa(unidadId);
        return Response.ok(ApiResponse.success("Programas obtenidos", programas)).build();
    }

    @GET
    @Path("/nivel/{nivelAcademico}")
    @Operation(summary = "Buscar programas por nivel académico")
    public Response findByNivelAcademico(@PathParam("nivelAcademico") String nivelAcademico) {
        List<ProgramaAcademicoResponseDTO> programas = programaService.findByNivelAcademico(nivelAcademico);
        return Response.ok(ApiResponse.success("Programas obtenidos", programas)).build();
    }

    @GET
    @Path("/activos")
    @Operation(summary = "Listar programas activos")
    public Response findProgramasActivos() {
        List<ProgramaAcademicoResponseDTO> programas = programaService.findProgramasActivos();
        return Response.ok(ApiResponse.success("Programas activos obtenidos", programas)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear programa académico")
    public Response create(@Valid ProgramaAcademicoRequestDTO dto) {

        // Convertir DTO a Command
        CrearProgramaAcademicoCommand command = new CrearProgramaAcademicoCommand(
                dto.getUnidadOrganizativaId(),
                dto.getCodigo(),
                dto.getNombre(),
                dto.getGradoAcademico(),
                dto.getNivelAcademico(),
                dto.getModalidad(),
                dto.getDuracionSemestres(),
                null // descripcion no está en el DTO
        );

        // Ejecutar Use Case
        ProgramaAcademico programa = crearProgramaUseCase.execute(command);

        // Convertir a DTO
        ProgramaAcademicoResponseDTO response = programaMapper.toResponseDTO(programa);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Programa académico creado exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar programa académico")
    public Response update(@PathParam("id") Long id, @Valid ProgramaAcademicoRequestDTO dto) {

        // Convertir DTO a Command
        ActualizarProgramaAcademicoCommand command = new ActualizarProgramaAcademicoCommand(
                id,
                dto.getUnidadOrganizativaId(),
                dto.getCodigo(),
                dto.getNombre(),
                dto.getGradoAcademico(),
                dto.getNivelAcademico(),
                dto.getModalidad(),
                dto.getDuracionSemestres(),
                null, // descripcion
                dto.getEstado());

        // Ejecutar Use Case
        ProgramaAcademico programa = actualizarProgramaUseCase.execute(command);

        // Convertir a DTO
        ProgramaAcademicoResponseDTO response = programaMapper.toResponseDTO(programa);

        return Response.ok(ApiResponse.success("Programa académico actualizado exitosamente", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar programa académico")
    public Response delete(@PathParam("id") Long id) {

        // Ejecutar Use Case
        eliminarProgramaUseCase.execute(id);

        return Response.ok(ApiResponse.success("Programa académico eliminado exitosamente", null)).build();
    }
}
