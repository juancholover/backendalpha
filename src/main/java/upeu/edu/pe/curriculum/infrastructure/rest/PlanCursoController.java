package upeu.edu.pe.curriculum.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.curriculum.application.dto.PlanCursoRequestDTO;
import upeu.edu.pe.curriculum.application.dto.PlanCursoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.PlanCursoMapper;
import upeu.edu.pe.curriculum.domain.commands.CrearPlanCursoCommand;
import upeu.edu.pe.curriculum.domain.entities.PlanCurso;
import upeu.edu.pe.curriculum.domain.services.PlanCursoService;
import upeu.edu.pe.curriculum.domain.usecases.CrearPlanCursoUseCase;
import upeu.edu.pe.curriculum.domain.usecases.EliminarPlanCursoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para planes-cursos.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/planes-cursos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Planes-Cursos", description = "Gestión de cursos asignados a planes académicos")
public class PlanCursoController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearPlanCursoUseCase crearUseCase;

    @Inject
    EliminarPlanCursoUseCase eliminarUseCase;

    // Service para operaciones de lectura
    @Inject
    PlanCursoService planCursoService;

    @Inject
    PlanCursoMapper planCursoMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/plan/{planId}")
    @Operation(summary = "Listar todos los cursos de un plan")
    public Response findByPlan(@PathParam("planId") Long planId) {
        List<PlanCursoResponseDTO> cursos = planCursoService.findByPlanAcademico(planId);
        return Response.ok(ApiResponse.success("Cursos del plan listados", cursos)).build();
    }

    @GET
    @Path("/plan/{planId}/ciclo/{ciclo}")
    @Operation(summary = "Listar cursos de un plan por ciclo")
    public Response findByPlanAndCiclo(
            @PathParam("planId") Long planId,
            @PathParam("ciclo") Integer ciclo) {
        List<PlanCursoResponseDTO> cursos = planCursoService.findByPlanAcademicoAndCiclo(planId, ciclo);
        return Response.ok(ApiResponse.success("Cursos del ciclo listados", cursos)).build();
    }

    @GET
    @Path("/plan/{planId}/obligatorios")
    @Operation(summary = "Listar cursos obligatorios de un plan")
    public Response findObligatorios(@PathParam("planId") Long planId) {
        List<PlanCursoResponseDTO> cursos = planCursoService.findObligatoriosByPlanAcademico(planId);
        return Response.ok(ApiResponse.success("Cursos obligatorios listados", cursos)).build();
    }

    @GET
    @Path("/plan/{planId}/electivos")
    @Operation(summary = "Listar cursos electivos de un plan")
    public Response findElectivos(@PathParam("planId") Long planId) {
        List<PlanCursoResponseDTO> cursos = planCursoService.findElectivosByPlanAcademico(planId);
        return Response.ok(ApiResponse.success("Cursos electivos listados", cursos)).build();
    }

    @GET
    @Path("/curso/{cursoId}")
    @Operation(summary = "Listar planes que incluyen un curso")
    public Response findByCurso(@PathParam("cursoId") Long cursoId) {
        List<PlanCursoResponseDTO> planes = planCursoService.findByCurso(cursoId);
        return Response.ok(ApiResponse.success("Planes que contienen el curso", planes)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar asignación por ID")
    public Response findById(@PathParam("id") Long id) {
        PlanCursoResponseDTO planCurso = planCursoService.findById(id);
        return Response.ok(ApiResponse.success("Asignación encontrada", planCurso)).build();
    }

    @GET
    @Path("/plan/{planId}/creditos-totales")
    @Operation(summary = "Calcular total de créditos de un plan")
    public Response calcularCreditosTotales(@PathParam("planId") Long planId) {
        Integer creditosTotales = planCursoService.calcularCreditosTotales(planId);
        return Response.ok(ApiResponse.success("Créditos totales calculados", creditosTotales)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Asignar un curso a un plan")
    @APIResponse(responseCode = "201", description = "Curso asignado exitosamente")
    public Response create(@Valid PlanCursoRequestDTO dto) {
        CrearPlanCursoCommand command = new CrearPlanCursoCommand(
                dto.getPlanAcademicoId(),
                dto.getCursoId(),
                dto.getCreditos(),
                dto.getCiclo(),
                dto.getTipoCurso(),
                dto.getEsObligatorio());

        PlanCurso planCurso = crearUseCase.execute(command);
        PlanCursoResponseDTO response = planCursoMapper.toResponseDTO(planCurso);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Curso asignado al plan", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar asignación curso-plan")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Asignación eliminada", null)).build();
    }
}
