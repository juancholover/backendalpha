package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.academic.application.dto.EvaluacionNotaRequestDTO;
import upeu.edu.pe.academic.application.dto.EvaluacionNotaResponseDTO;
import upeu.edu.pe.academic.domain.services.EvaluacionNotaService;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/v1/evaluacion-notas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvaluacionNotaController {

    @Inject
    EvaluacionNotaService notaService;

    @GET
    @Path("/matricula/{matriculaId}")
    public Response findByMatricula(@PathParam("matriculaId") Long matriculaId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findByMatricula(matriculaId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/criterio/{criterioId}")
    public Response findByCriterio(@PathParam("criterioId") Long criterioId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findByCriterio(criterioId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/seccion/{seccionId}/pendientes")
    public Response findPendientesBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findPendientesBySeccion(seccionId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/seccion/{seccionId}/calificadas")
    public Response findCalificadasBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findCalificadasBySeccion(seccionId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/seccion/{seccionId}")
    public Response findByEstudianteAndSeccion(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findByEstudianteAndSeccion(estudianteId, seccionId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/seccion/{seccionId}/recuperaciones")
    public Response findConRecuperacionBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findConRecuperacionBySeccion(seccionId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/seccion/{seccionId}/desaprobadas")
    public Response findDesaprobadasBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findDesaprobadasBySeccion(seccionId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        EvaluacionNotaResponseDTO nota = notaService.findById(id);
        return Response.ok(nota).build();
    }

    @GET
    @Path("/matricula/{matriculaId}/calificadas/count")
    public Response countCalificadasByMatricula(@PathParam("matriculaId") Long matriculaId) {
        long count = notaService.countCalificadasByMatricula(matriculaId);
        return Response.ok().entity("{\"count\": " + count + "}").build();
    }

    @GET
    @Path("/criterio/{criterioId}/promedio")
    public Response getPromedioNotasByCriterio(@PathParam("criterioId") Long criterioId) {
        BigDecimal promedio = notaService.getPromedioNotasByCriterio(criterioId);
        return Response.ok().entity("{\"promedio\": " + promedio + "}").build();
    }

    @POST
    public Response create(@Valid EvaluacionNotaRequestDTO requestDTO) {
        EvaluacionNotaResponseDTO nota = notaService.create(requestDTO);
        return Response.status(Response.Status.CREATED).entity(nota).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid EvaluacionNotaRequestDTO requestDTO) {
        EvaluacionNotaResponseDTO nota = notaService.update(id, requestDTO);
        return Response.ok(nota).build();
    }

    @PUT
    @Path("/{id}/registrar-nota")
    public Response registrarNota(@PathParam("id") Long id, @QueryParam("nota") BigDecimal nota) {
        EvaluacionNotaResponseDTO resultado = notaService.registrarNota(id, nota);
        return Response.ok(resultado).build();
    }

    @PUT
    @Path("/{id}/registrar-recuperacion")
    public Response registrarRecuperacion(
            @PathParam("id") Long id,
            @QueryParam("notaRecuperacion") BigDecimal notaRecuperacion) {
        EvaluacionNotaResponseDTO resultado = notaService.registrarRecuperacion(id, notaRecuperacion);
        return Response.ok(resultado).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        notaService.delete(id);
        return Response.noContent().build();
    }
}
