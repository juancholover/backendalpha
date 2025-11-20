package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.academic.application.dto.PlanAcademicoRequestDTO;
import upeu.edu.pe.academic.application.dto.PlanAcademicoResponseDTO;
import upeu.edu.pe.academic.domain.services.PlanAcademicoService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/planes-academicos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlanAcademicoController {

    @Inject
    PlanAcademicoService planService;

    @GET
    public Response findAll() {
        List<PlanAcademicoResponseDTO> planes = planService.findAll();
        return Response.ok(ApiResponse.success("Planes académicos obtenidos exitosamente", planes)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        PlanAcademicoResponseDTO plan = planService.findById(id);
        return Response.ok(ApiResponse.success("Plan académico obtenido exitosamente", plan)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    public Response findByCodigo(@PathParam("codigo") String codigo) {
        PlanAcademicoResponseDTO plan = planService.findByCodigo(codigo);
        return Response.ok(ApiResponse.success("Plan académico obtenido exitosamente", plan)).build();
    }

    @GET
    @Path("/programa/{programaId}")
    public Response findByProgramaAcademico(@PathParam("programaId") Long programaId) {
        List<PlanAcademicoResponseDTO> planes = planService.findByProgramaAcademico(programaId);
        return Response.ok(ApiResponse.success("Planes académicos por programa obtenidos exitosamente", planes)).build();
    }

    @GET
    @Path("/programa/{programaId}/vigentes")
    public Response findPlanesVigentes(@PathParam("programaId") Long programaId) {
        List<PlanAcademicoResponseDTO> planes = planService.findPlanesVigentes(programaId);
        return Response.ok(ApiResponse.success("Planes vigentes obtenidos exitosamente", planes)).build();
    }

    @GET
    @Path("/programa/{programaId}/vigente-actual")
    public Response findPlanVigenteActual(@PathParam("programaId") Long programaId) {
        PlanAcademicoResponseDTO plan = planService.findPlanVigenteActual(programaId);
        return Response.ok(ApiResponse.success("Plan vigente actual obtenido exitosamente", plan)).build();
    }

    @POST
    public Response create(@Valid PlanAcademicoRequestDTO requestDTO) {
        PlanAcademicoResponseDTO plan = planService.create(requestDTO);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Plan académico creado exitosamente", plan))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid PlanAcademicoRequestDTO requestDTO) {
        PlanAcademicoResponseDTO plan = planService.update(id, requestDTO);
        return Response.ok(ApiResponse.success("Plan académico actualizado exitosamente", plan)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        planService.delete(id);
        return Response.ok(ApiResponse.success("Plan académico eliminado exitosamente")).build();
    }
}
