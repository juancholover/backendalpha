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
import upeu.edu.pe.curriculum.domain.services.PlanCursoService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/v1/planes-cursos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Planes-Cursos", description = "Gestión de cursos asignados a planes académicos")
public class PlanCursoController {

    @Inject
    PlanCursoService planCursoService;

    @GET
    @Path("/plan/{planId}")
    @Operation(summary = "Listar todos los cursos de un plan académico")
    @APIResponse(responseCode = "200", description = "Lista de cursos del plan")
    public Response findByPlan(
            @Parameter(description = "ID del plan académico", required = true)
            @PathParam("planId") Long planId) {
        List<PlanCursoResponseDTO> cursos = planCursoService.findByPlanAcademico(planId);
        return Response.ok(ApiResponse.success("Cursos del plan listados exitosamente", cursos)).build();
    }

    @GET
    @Path("/plan/{planId}/ciclo/{ciclo}")
    @Operation(summary = "Listar cursos de un plan por ciclo")
    @APIResponse(responseCode = "200", description = "Lista de cursos del ciclo")
    public Response findByPlanAndCiclo(
            @Parameter(description = "ID del plan académico", required = true)
            @PathParam("planId") Long planId,
            @Parameter(description = "Número de ciclo", required = true)
            @PathParam("ciclo") Integer ciclo) {
        List<PlanCursoResponseDTO> cursos = planCursoService.findByPlanAcademicoAndCiclo(planId, ciclo);
        return Response.ok(ApiResponse.success("Cursos del ciclo listados exitosamente", cursos)).build();
    }

    @GET
    @Path("/plan/{planId}/obligatorios")
    @Operation(summary = "Listar cursos obligatorios de un plan")
    @APIResponse(responseCode = "200", description = "Lista de cursos obligatorios")
    public Response findObligatorios(
            @Parameter(description = "ID del plan académico", required = true)
            @PathParam("planId") Long planId) {
        List<PlanCursoResponseDTO> cursos = planCursoService.findObligatoriosByPlanAcademico(planId);
        return Response.ok(ApiResponse.success("Cursos obligatorios listados exitosamente", cursos)).build();
    }

    @GET
    @Path("/plan/{planId}/electivos")
    @Operation(summary = "Listar cursos electivos de un plan")
    @APIResponse(responseCode = "200", description = "Lista de cursos electivos")
    public Response findElectivos(
            @Parameter(description = "ID del plan académico", required = true)
            @PathParam("planId") Long planId) {
        List<PlanCursoResponseDTO> cursos = planCursoService.findElectivosByPlanAcademico(planId);
        return Response.ok(ApiResponse.success("Cursos electivos listados exitosamente", cursos)).build();
    }

    @GET
    @Path("/curso/{cursoId}")
    @Operation(summary = "Listar planes académicos que incluyen un curso")
    @APIResponse(responseCode = "200", description = "Lista de planes que contienen el curso")
    public Response findByCurso(
            @Parameter(description = "ID del curso", required = true)
            @PathParam("cursoId") Long cursoId) {
        List<PlanCursoResponseDTO> planes = planCursoService.findByCurso(cursoId);
        return Response.ok(ApiResponse.success("Planes que contienen el curso listados exitosamente", planes)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar asignación curso-plan por ID")
    @APIResponse(responseCode = "200", description = "Asignación encontrada")
    @APIResponse(responseCode = "404", description = "Asignación no encontrada")
    public Response findById(
            @Parameter(description = "ID de la asignación", required = true)
            @PathParam("id") Long id) {
        PlanCursoResponseDTO planCurso = planCursoService.findById(id);
        return Response.ok(ApiResponse.success("Asignación encontrada", planCurso)).build();
    }

    @POST
    @Operation(summary = "Asignar un curso a un plan académico")
    @APIResponse(responseCode = "201", description = "Curso asignado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos o curso ya existe en el plan")
    public Response create(@Valid PlanCursoRequestDTO requestDTO) {
        PlanCursoResponseDTO planCurso = planCursoService.create(requestDTO);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Curso asignado al plan exitosamente", planCurso))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar asignación curso-plan (créditos, ciclo, tipo)")
    @APIResponse(responseCode = "200", description = "Asignación actualizada")
    @APIResponse(responseCode = "404", description = "Asignación no encontrada")
    public Response update(
            @Parameter(description = "ID de la asignación", required = true)
            @PathParam("id") Long id,
            @Valid PlanCursoRequestDTO requestDTO) {
        PlanCursoResponseDTO planCurso = planCursoService.update(id, requestDTO);
        return Response.ok(ApiResponse.success("Asignación actualizada exitosamente", planCurso)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar asignación curso-plan (borrado lógico)")
    @APIResponse(responseCode = "200", description = "Asignación eliminada")
    @APIResponse(responseCode = "404", description = "Asignación no encontrada")
    public Response delete(
            @Parameter(description = "ID de la asignación", required = true)
            @PathParam("id") Long id) {
        planCursoService.delete(id);
        return Response.ok(ApiResponse.success("Asignación eliminada exitosamente")).build();
    }

    @GET
    @Path("/plan/{planId}/creditos-totales")
    @Operation(summary = "Calcular total de créditos de un plan académico")
    @APIResponse(responseCode = "200", description = "Total de créditos calculado")
    public Response calcularCreditosTotales(
            @Parameter(description = "ID del plan académico", required = true)
            @PathParam("planId") Long planId) {
        Integer creditosTotales = planCursoService.calcularCreditosTotales(planId);
        return Response.ok(ApiResponse.success("Créditos totales calculados", creditosTotales)).build();
    }
}
