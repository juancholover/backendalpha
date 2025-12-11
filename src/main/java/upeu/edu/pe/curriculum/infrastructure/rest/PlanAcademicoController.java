package upeu.edu.pe.curriculum.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.curriculum.application.dto.PlanAcademicoRequestDTO;
import upeu.edu.pe.curriculum.application.dto.PlanAcademicoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.PlanAcademicoMapper;
import upeu.edu.pe.curriculum.domain.commands.CrearPlanAcademicoCommand;
import upeu.edu.pe.curriculum.domain.entities.PlanAcademico;
import upeu.edu.pe.curriculum.domain.services.PlanAcademicoService;
import upeu.edu.pe.curriculum.domain.usecases.CrearPlanAcademicoUseCase;
import upeu.edu.pe.curriculum.domain.usecases.EliminarPlanAcademicoUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para planes académicos.
 */
@Path("/api/v1/planes-academicos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Planes Académicos", description = "Gestión de mallas curriculares")
public class PlanAcademicoController {

    @Inject
    CrearPlanAcademicoUseCase crearUseCase;

    @Inject
    EliminarPlanAcademicoUseCase eliminarUseCase;

    @Inject
    PlanAcademicoService planService;

    @Inject
    PlanAcademicoMapper planMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar todos los planes académicos")
    public Response findAll() {
        List<PlanAcademicoResponseDTO> planes = planService.findAll();
        return Response.ok(ApiResponse.success("Planes académicos obtenidos", planes)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar plan por ID")
    public Response findById(@PathParam("id") Long id) {
        PlanAcademicoResponseDTO plan = planService.findById(id);
        return Response.ok(ApiResponse.success("Plan académico obtenido", plan)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Operation(summary = "Buscar plan por código")
    public Response findByCodigo(@PathParam("codigo") String codigo) {
        PlanAcademicoResponseDTO plan = planService.findByCodigo(codigo);
        return Response.ok(ApiResponse.success("Plan académico obtenido", plan)).build();
    }

    @GET
    @Path("/programa/{programaId}")
    @Operation(summary = "Listar planes por programa")
    public Response findByProgramaAcademico(@PathParam("programaId") Long programaId) {
        List<PlanAcademicoResponseDTO> planes = planService.findByProgramaAcademico(programaId);
        return Response.ok(ApiResponse.success("Planes por programa obtenidos", planes)).build();
    }

    @GET
    @Path("/programa/{programaId}/vigentes")
    @Operation(summary = "Listar planes vigentes por programa")
    public Response findPlanesVigentes(@PathParam("programaId") Long programaId) {
        List<PlanAcademicoResponseDTO> planes = planService.findPlanesVigentes(programaId);
        return Response.ok(ApiResponse.success("Planes vigentes obtenidos", planes)).build();
    }

    @GET
    @Path("/programa/{programaId}/vigente-actual")
    @Operation(summary = "Obtener plan vigente actual")
    public Response findPlanVigenteActual(@PathParam("programaId") Long programaId) {
        PlanAcademicoResponseDTO plan = planService.findPlanVigenteActual(programaId);
        return Response.ok(ApiResponse.success("Plan vigente actual obtenido", plan)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear plan académico")
    public Response create(@Valid PlanAcademicoRequestDTO dto) {
        CrearPlanAcademicoCommand command = new CrearPlanAcademicoCommand(
                dto.getProgramaAcademicoId(),
                dto.getCodigo(),
                dto.getNombre(),
                dto.getFechaVigenciaInicio(),
                dto.getFechaVigenciaFin(),
                dto.getCreditosTotales(),
                dto.getDuracionSemestres(),
                dto.getEstado());

        PlanAcademico plan = crearUseCase.execute(command);
        PlanAcademicoResponseDTO response = planMapper.toResponseDTO(plan);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Plan académico creado", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar plan académico")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Plan académico eliminado", null)).build();
    }
}
