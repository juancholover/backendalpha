package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.academic.application.dto.ProgramaAcademicoRequestDTO;
import upeu.edu.pe.academic.application.dto.ProgramaAcademicoResponseDTO;
import upeu.edu.pe.academic.domain.services.ProgramaAcademicoService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/programas-academicos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProgramaAcademicoController {

    @Inject
    ProgramaAcademicoService programaService;

    @GET
    public Response findAll() {
        List<ProgramaAcademicoResponseDTO> programas = programaService.findAll();
        return Response.ok(ApiResponse.success("Programas académicos obtenidos exitosamente", programas)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        ProgramaAcademicoResponseDTO programa = programaService.findById(id);
        return Response.ok(ApiResponse.success("Programa académico obtenido exitosamente", programa)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    public Response findByCodigo(@PathParam("codigo") String codigo) {
        ProgramaAcademicoResponseDTO programa = programaService.findByCodigo(codigo);
        return Response.ok(ApiResponse.success("Programa académico obtenido exitosamente", programa)).build();
    }

    @GET
    @Path("/unidad/{unidadId}")
    public Response findByUnidadOrganizativa(@PathParam("unidadId") Long unidadId) {
        List<ProgramaAcademicoResponseDTO> programas = programaService.findByUnidadOrganizativa(unidadId);
        return Response.ok(ApiResponse.success("Programas académicos por unidad obtenidos exitosamente", programas)).build();
    }

    @GET
    @Path("/nivel/{nivelAcademico}")
    public Response findByNivelAcademico(@PathParam("nivelAcademico") String nivelAcademico) {
        List<ProgramaAcademicoResponseDTO> programas = programaService.findByNivelAcademico(nivelAcademico);
        return Response.ok(ApiResponse.success("Programas académicos por nivel obtenidos exitosamente", programas)).build();
    }

    @GET
    @Path("/modalidad/{modalidad}")
    public Response findByModalidad(@PathParam("modalidad") String modalidad) {
        List<ProgramaAcademicoResponseDTO> programas = programaService.findByModalidad(modalidad);
        return Response.ok(ApiResponse.success("Programas académicos por modalidad obtenidos exitosamente", programas)).build();
    }

    @GET
    @Path("/activos")
    public Response findProgramasActivos() {
        List<ProgramaAcademicoResponseDTO> programas = programaService.findProgramasActivos();
        return Response.ok(ApiResponse.success("Programas académicos activos obtenidos exitosamente", programas)).build();
    }

    @POST
    public Response create(@Valid ProgramaAcademicoRequestDTO requestDTO) {
        ProgramaAcademicoResponseDTO programa = programaService.create(requestDTO);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Programa académico creado exitosamente", programa))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid ProgramaAcademicoRequestDTO requestDTO) {
        ProgramaAcademicoResponseDTO programa = programaService.update(id, requestDTO);
        return Response.ok(ApiResponse.success("Programa académico actualizado exitosamente", programa)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        programaService.delete(id);
        return Response.ok(ApiResponse.success("Programa académico eliminado exitosamente")).build();
    }
}
