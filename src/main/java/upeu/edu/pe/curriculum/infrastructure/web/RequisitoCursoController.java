package upeu.edu.pe.curriculum.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.curriculum.application.dto.RequisitoCursoRequestDTO;
import upeu.edu.pe.curriculum.application.dto.RequisitoCursoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.RequisitoCursoMapper;
import upeu.edu.pe.curriculum.domain.commands.CrearRequisitoCursoCommand;
import upeu.edu.pe.curriculum.domain.entities.RequisitoCurso;
import upeu.edu.pe.curriculum.domain.services.RequisitoCursoService;
import upeu.edu.pe.curriculum.domain.usecases.CrearRequisitoCursoUseCase;
import upeu.edu.pe.curriculum.domain.usecases.EliminarRequisitoCursoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para requisitos de curso.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/requisitos-curso")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Requisitos de Curso", description = "Gestión de pre-requisitos y co-requisitos")
public class RequisitoCursoController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearRequisitoCursoUseCase crearUseCase;

    @Inject
    EliminarRequisitoCursoUseCase eliminarUseCase;

    // Service para operaciones de lectura
    @Inject
    RequisitoCursoService requisitoService;

    @Inject
    RequisitoCursoMapper requisitoMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/curso/{cursoId}")
    @Operation(summary = "Listar requisitos de un curso")
    public Response findByCurso(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findByCurso(cursoId);
        return Response.ok(ApiResponse.success("Requisitos del curso", requisitos)).build();
    }

    @GET
    @Path("/curso/{cursoId}/prerrequisitos")
    @Operation(summary = "Listar pre-requisitos de un curso")
    public Response findPrerequisitosByCurso(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findPrerequisitosByCurso(cursoId);
        return Response.ok(ApiResponse.success("Pre-requisitos", requisitos)).build();
    }

    @GET
    @Path("/curso/{cursoId}/correquisitos")
    @Operation(summary = "Listar co-requisitos de un curso")
    public Response findCorrequisitosByCurso(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findCorrequisitosByCurso(cursoId);
        return Response.ok(ApiResponse.success("Co-requisitos", requisitos)).build();
    }

    @GET
    @Path("/curso/{cursoId}/obligatorios")
    @Operation(summary = "Listar requisitos obligatorios")
    public Response findObligatoriosByCurso(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findObligatoriosByCurso(cursoId);
        return Response.ok(ApiResponse.success("Requisitos obligatorios", requisitos)).build();
    }

    @GET
    @Path("/curso/{cursoId}/cascada")
    @Operation(summary = "Listar todos los requisitos en cascada")
    public Response findAllRequisitosCascada(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findAllRequisitosCascada(cursoId);
        return Response.ok(ApiResponse.success("Requisitos en cascada", requisitos)).build();
    }

    @GET
    @Path("/curso/{cursoId}/dependientes")
    @Operation(summary = "Listar cursos que tienen este como requisito")
    public Response findCursosQueTienenComoRequisito(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findCursosQueTienenComoRequisito(cursoId);
        return Response.ok(ApiResponse.success("Cursos dependientes", requisitos)).build();
    }

    @GET
    @Path("/curso/{cursoId}/count")
    @Operation(summary = "Contar requisitos de un curso")
    public Response countByCurso(@PathParam("cursoId") Long cursoId) {
        long count = requisitoService.countByCurso(cursoId);
        return Response.ok(ApiResponse.success("Cantidad de requisitos", count)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear requisito de curso")
    public Response create(@Valid RequisitoCursoRequestDTO dto) {
        CrearRequisitoCursoCommand command = new CrearRequisitoCursoCommand(
                dto.getCursoId(),
                dto.getCursoRequisitoId(),
                dto.getTipoRequisito());

        RequisitoCurso requisito = crearUseCase.execute(command);
        RequisitoCursoResponseDTO response = requisitoMapper.toResponseDTO(requisito);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Requisito creado", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar requisito de curso")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Requisito eliminado", null)).build();
    }
}
