package upeu.edu.pe.enrollment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.enrollment.application.dto.CursoOfertadoRequestDTO;
import upeu.edu.pe.enrollment.application.dto.CursoOfertadoResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.CursoOfertadoMapper;
import upeu.edu.pe.enrollment.domain.commands.CrearCursoOfertadoCommand;
import upeu.edu.pe.enrollment.domain.entities.CursoOfertado;
import upeu.edu.pe.enrollment.domain.services.CursoOfertadoService;
import upeu.edu.pe.enrollment.domain.usecases.CrearCursoOfertadoUseCase;
import upeu.edu.pe.enrollment.domain.usecases.EliminarCursoOfertadoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de cursos ofertados.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/cursos-ofertados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cursos Ofertados", description = "Gestión de cursos ofertados por período")
public class CursoOfertadoController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearCursoOfertadoUseCase crearCursoOfertadoUseCase;

    @Inject
    EliminarCursoOfertadoUseCase eliminarCursoOfertadoUseCase;

    // Service para operaciones de lectura
    @Inject
    CursoOfertadoService cursoOfertadoService;

    @Inject
    CursoOfertadoMapper cursoOfertadoMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar cursos ofertados activos")
    public Response findAll() {
        List<CursoOfertadoResponseDTO> cursos = cursoOfertadoService.findAllActive();
        return Response.ok(ApiResponse.success("Cursos ofertados obtenidos", cursos)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar curso ofertado por ID")
    public Response findById(@PathParam("id") Long id) {
        CursoOfertadoResponseDTO curso = cursoOfertadoService.findById(id);
        return Response.ok(ApiResponse.success("Curso ofertado obtenido", curso)).build();
    }

    @GET
    @Path("/periodo/{periodoId}")
    @Operation(summary = "Buscar cursos por período académico")
    public Response findByPeriodoAcademico(@PathParam("periodoId") Long periodoId) {
        List<CursoOfertadoResponseDTO> cursos = cursoOfertadoService.findByPeriodoAcademico(periodoId);
        return Response.ok(ApiResponse.success("Cursos ofertados obtenidos", cursos)).build();
    }

    @GET
    @Path("/profesor/{profesorId}")
    @Operation(summary = "Buscar cursos por profesor")
    public Response findByProfesor(@PathParam("profesorId") Long profesorId) {
        List<CursoOfertadoResponseDTO> cursos = cursoOfertadoService.findByProfesor(profesorId);
        return Response.ok(ApiResponse.success("Cursos ofertados obtenidos", cursos)).build();
    }

    @GET
    @Path("/plan-curso/{planCursoId}")
    @Operation(summary = "Buscar cursos por plan-curso")
    public Response findByPlanCurso(@PathParam("planCursoId") Long planCursoId) {
        List<CursoOfertadoResponseDTO> cursos = cursoOfertadoService.findByPlanCurso(planCursoId);
        return Response.ok(ApiResponse.success("Cursos ofertados obtenidos", cursos)).build();
    }

    @GET
    @Path("/con-vacantes/periodo/{periodoId}")
    @Operation(summary = "Buscar cursos con vacantes por período")
    public Response findConVacantesByPeriodo(@PathParam("periodoId") Long periodoId) {
        List<CursoOfertadoResponseDTO> cursos = cursoOfertadoService.findConVacantesByPeriodo(periodoId);
        return Response.ok(ApiResponse.success("Cursos con vacantes obtenidos", cursos)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear curso ofertado")
    public Response create(@Valid CursoOfertadoRequestDTO requestDTO) {

        // Convertir DTO a Command
        CrearCursoOfertadoCommand command = new CrearCursoOfertadoCommand(
                requestDTO.getPlanCursoId(),
                requestDTO.getPeriodoAcademicoId(),
                requestDTO.getProfesorId(),
                requestDTO.getLocalizacionId(),
                requestDTO.getModalidadId(),
                requestDTO.getCodigoSeccion(),
                requestDTO.getCapacidadMaxima(),
                requestDTO.getEstado());

        // Ejecutar Use Case
        CursoOfertado cursoOfertado = crearCursoOfertadoUseCase.execute(command);

        // Convertir a DTO
        CursoOfertadoResponseDTO response = cursoOfertadoMapper.toResponseDTO(cursoOfertado);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Curso ofertado creado exitosamente", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar curso ofertado")
    public Response delete(@PathParam("id") Long id) {

        // Ejecutar Use Case
        eliminarCursoOfertadoUseCase.execute(id);

        return Response.ok(ApiResponse.success("Curso ofertado eliminado exitosamente", null)).build();
    }
}
