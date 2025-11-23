package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.academic.application.dto.CursoOfertadoRequestDTO;
import upeu.edu.pe.academic.application.dto.CursoOfertadoResponseDTO;
import upeu.edu.pe.academic.domain.services.CursoOfertadoService;

import java.util.List;

@Path("/api/v1/cursos-ofertados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CursoOfertadoController {

    @Inject
    CursoOfertadoService cursoOfertadoService;

    @GET
    @Path("/universidad/{universidadId}")
    public Response findByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<CursoOfertadoResponseDTO> cursosOfertados = cursoOfertadoService.findByUniversidad(universidadId);
        return Response.ok(cursosOfertados).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        CursoOfertadoResponseDTO cursoOfertado = cursoOfertadoService.findById(id);
        return Response.ok(cursoOfertado).build();
    }

    @GET
    @Path("/periodo/{periodoId}")
    public Response findByPeriodoAcademico(@PathParam("periodoId") Long periodoId) {
        List<CursoOfertadoResponseDTO> cursosOfertados = cursoOfertadoService.findByPeriodoAcademico(periodoId);
        return Response.ok(cursosOfertados).build();
    }

    @GET
    @Path("/plan/{planId}")
    public Response findByPlanAcademico(@PathParam("planId") Long planId) {
        List<CursoOfertadoResponseDTO> cursosOfertados = cursoOfertadoService.findByPlanAcademico(planId);
        return Response.ok(cursosOfertados).build();
    }

    @GET
    @Path("/plan-curso/{planCursoId}")
    public Response findByPlanCurso(@PathParam("planCursoId") Long planCursoId) {
        List<CursoOfertadoResponseDTO> cursosOfertados = cursoOfertadoService.findByPlanCurso(planCursoId);
        return Response.ok(cursosOfertados).build();
    }

    @GET
    @Path("/curso/{cursoId}")
    public Response findByCurso(@PathParam("cursoId") Long cursoId) {
        List<CursoOfertadoResponseDTO> cursosOfertados = cursoOfertadoService.findByCurso(cursoId);
        return Response.ok(cursosOfertados).build();
    }

    @GET
    @Path("/profesor/{profesorId}")
    public Response findByProfesor(@PathParam("profesorId") Long profesorId) {
        List<CursoOfertadoResponseDTO> cursosOfertados = cursoOfertadoService.findByProfesor(profesorId);
        return Response.ok(cursosOfertados).build();
    }

    @GET
    @Path("/periodo/{periodoId}/universidad/{universidadId}/abiertas")
    public Response findAbiertasByPeriodoAndUniversidad(
            @PathParam("periodoId") Long periodoId,
            @PathParam("universidadId") Long universidadId) {
        List<CursoOfertadoResponseDTO> cursosOfertados = cursoOfertadoService.findAbiertasByPeriodoAndUniversidad(periodoId, universidadId);
        return Response.ok(cursosOfertados).build();
    }

    @GET
    @Path("/periodo/{periodoId}/con-vacantes")
    public Response findConVacantesByPeriodo(@PathParam("periodoId") Long periodoId) {
        List<CursoOfertadoResponseDTO> cursosOfertados = cursoOfertadoService.findConVacantesByPeriodo(periodoId);
        return Response.ok(cursosOfertados).build();
    }

    @GET
    @Path("/modalidad/{modalidad}/periodo/{periodoId}")
    public Response findByModalidadAndPeriodo(
            @PathParam("modalidad") String modalidad,
            @PathParam("periodoId") Long periodoId) {
        List<CursoOfertadoResponseDTO> cursosOfertados = cursoOfertadoService.findByModalidadAndPeriodo(modalidad, periodoId);
        return Response.ok(cursosOfertados).build();
    }

    @POST
    public Response create(@Valid CursoOfertadoRequestDTO requestDTO) {
        CursoOfertadoResponseDTO cursoOfertado = cursoOfertadoService.create(requestDTO);
        return Response.status(Response.Status.CREATED).entity(cursoOfertado).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid CursoOfertadoRequestDTO requestDTO) {
        CursoOfertadoResponseDTO cursoOfertado = cursoOfertadoService.update(id, requestDTO);
        return Response.ok(cursoOfertado).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        cursoOfertadoService.delete(id);
        return Response.noContent().build();
    }
}
