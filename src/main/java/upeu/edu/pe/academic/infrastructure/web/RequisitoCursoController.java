package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.academic.application.dto.RequisitoCursoRequestDTO;
import upeu.edu.pe.academic.application.dto.RequisitoCursoResponseDTO;
import upeu.edu.pe.academic.domain.services.RequisitoCursoService;

import java.util.List;

@Path("/api/v1/requisitos-curso")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RequisitoCursoController {

    @Inject
    RequisitoCursoService requisitoService;

    @GET
    @Path("/curso/{cursoId}")
    public Response findByCurso(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findByCurso(cursoId);
        return Response.ok(requisitos).build();
    }

    @GET
    @Path("/curso/{cursoId}/prerrequisitos")
    public Response findPrerequisitosByCurso(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findPrerequisitosByCurso(cursoId);
        return Response.ok(requisitos).build();
    }

    @GET
    @Path("/curso/{cursoId}/correquisitos")
    public Response findCorrequisitosByCurso(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findCorrequisitosByCurso(cursoId);
        return Response.ok(requisitos).build();
    }

    @GET
    @Path("/curso/{cursoId}/obligatorios")
    public Response findObligatoriosByCurso(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findObligatoriosByCurso(cursoId);
        return Response.ok(requisitos).build();
    }

    @GET
    @Path("/curso/{cursoId}/cascada")
    public Response findAllRequisitosCascada(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findAllRequisitosCascada(cursoId);
        return Response.ok(requisitos).build();
    }

    @GET
    @Path("/curso/{cursoId}/dependientes")
    public Response findCursosQueTienenComoRequisito(@PathParam("cursoId") Long cursoId) {
        List<RequisitoCursoResponseDTO> requisitos = requisitoService.findCursosQueTienenComoRequisito(cursoId);
        return Response.ok(requisitos).build();
    }

    @GET
    @Path("/curso/{cursoId}/count")
    public Response countByCurso(@PathParam("cursoId") Long cursoId) {
        long count = requisitoService.countByCurso(cursoId);
        return Response.ok().entity("{\"count\": " + count + "}").build();
    }

    @POST
    public Response create(@Valid RequisitoCursoRequestDTO requestDTO) {
        RequisitoCursoResponseDTO requisito = requisitoService.create(requestDTO);
        return Response.status(Response.Status.CREATED).entity(requisito).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid RequisitoCursoRequestDTO requestDTO) {
        RequisitoCursoResponseDTO requisito = requisitoService.update(id, requestDTO);
        return Response.ok(requisito).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        requisitoService.delete(id);
        return Response.noContent().build();
    }
}
