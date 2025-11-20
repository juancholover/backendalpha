package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.academic.application.dto.MatriculaRequestDTO;
import upeu.edu.pe.academic.application.dto.MatriculaResponseDTO;
import upeu.edu.pe.academic.domain.services.MatriculaService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/matriculas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MatriculaController {

    @Inject
    MatriculaService matriculaService;

    @GET
    @Path("/estudiante/{estudianteId}")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findByEstudiante(estudianteId);
        return Response.ok(ApiResponse.success("Matrículas por estudiante obtenidas exitosamente", matriculas)).build();
    }

    @GET
    @Path("/seccion/{seccionId}")
    public Response findBySeccion(@PathParam("seccionId") Long seccionId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findBySeccion(seccionId);
        return Response.ok(ApiResponse.success("Matrículas por sección obtenidas exitosamente", matriculas)).build();
    }

    @GET
    @Path("/periodo/{periodoId}")
    public Response findByPeriodoAcademico(@PathParam("periodoId") Long periodoId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findByPeriodoAcademico(periodoId);
        return Response.ok(ApiResponse.success("Matrículas por período académico obtenidas exitosamente", matriculas)).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/periodo/{periodoId}")
    public Response findByEstudianteAndPeriodo(
            @PathParam("estudianteId") Long estudianteId, 
            @PathParam("periodoId") Long periodoId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findByEstudianteAndPeriodo(estudianteId, periodoId);
        return Response.ok(ApiResponse.success("Matrículas del estudiante en el período obtenidas exitosamente", matriculas)).build();
    }

    @GET
    @Path("/estado/{estadoMatricula}")
    public Response findByEstadoMatricula(@PathParam("estadoMatricula") String estadoMatricula) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findByEstadoMatricula(estadoMatricula);
        return Response.ok(ApiResponse.success("Matrículas por estado obtenidas exitosamente", matriculas)).build();
    }

    @GET
    @Path("/activas/periodo/{periodoId}")
    public Response findMatriculasActivas(@PathParam("periodoId") Long periodoId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findMatriculasActivas(periodoId);
        return Response.ok(ApiResponse.success("Matrículas activas obtenidas exitosamente", matriculas)).build();
    }

    @GET
    @Path("/aprobadas/estudiante/{estudianteId}")
    public Response findMatriculasAprobadas(@PathParam("estudianteId") Long estudianteId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findMatriculasAprobadas(estudianteId);
        return Response.ok(ApiResponse.success("Matrículas aprobadas obtenidas exitosamente", matriculas)).build();
    }

    @GET
    @Path("/tipo/{tipoMatricula}/periodo/{periodoId}")
    public Response findByTipoMatricula(
            @PathParam("tipoMatricula") String tipoMatricula, 
            @PathParam("periodoId") Long periodoId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findByTipoMatricula(tipoMatricula, periodoId);
        return Response.ok(ApiResponse.success("Matrículas por tipo obtenidas exitosamente", matriculas)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        MatriculaResponseDTO matricula = matriculaService.findById(id);
        return Response.ok(ApiResponse.success("Matrícula obtenida exitosamente", matricula)).build();
    }

    @GET
    @Path("/buscar")
    public Response findByEstudianteAndSeccion(
            @QueryParam("estudianteId") Long estudianteId,
            @QueryParam("seccionId") Long seccionId) {
        MatriculaResponseDTO matricula = matriculaService.findByEstudianteAndSeccion(estudianteId, seccionId);
        return Response.ok(ApiResponse.success("Matrícula obtenida exitosamente", matricula)).build();
    }

    @POST
    public Response create(@Valid MatriculaRequestDTO requestDTO) {
        MatriculaResponseDTO matricula = matriculaService.create(requestDTO);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Matrícula creada exitosamente", matricula))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid MatriculaRequestDTO requestDTO) {
        MatriculaResponseDTO matricula = matriculaService.update(id, requestDTO);
        return Response.ok(ApiResponse.success("Matrícula actualizada exitosamente", matricula)).build();
    }

    @PUT
    @Path("/{id}/retirar")
    public Response retirar(@PathParam("id") Long id) {
        MatriculaResponseDTO matricula = matriculaService.retirar(id);
        return Response.ok(ApiResponse.success("Estudiante retirado exitosamente", matricula)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        matriculaService.delete(id);
        return Response.ok(ApiResponse.success("Matrícula eliminada exitosamente")).build();
    }
}
