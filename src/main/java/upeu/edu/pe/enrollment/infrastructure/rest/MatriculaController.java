package upeu.edu.pe.enrollment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.enrollment.application.dto.MatriculaRequestDTO;
import upeu.edu.pe.enrollment.application.dto.MatriculaResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.MatriculaMapper;
import upeu.edu.pe.enrollment.domain.commands.MatricularEstudianteCommand;
import upeu.edu.pe.enrollment.domain.entities.Matricula;
import upeu.edu.pe.enrollment.domain.services.MatriculaService;
import upeu.edu.pe.enrollment.domain.usecases.MatricularEstudianteUseCase;
import upeu.edu.pe.enrollment.domain.usecases.RetirarMatriculaUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de matrículas.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/matriculas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Matrículas", description = "Gestión de matrículas de estudiantes")
public class MatriculaController {

    // Use Cases para operaciones de escritura
    @Inject
    MatricularEstudianteUseCase matricularEstudianteUseCase;

    @Inject
    RetirarMatriculaUseCase retirarMatriculaUseCase;

    // Service para operaciones de lectura
    @Inject
    MatriculaService matriculaService;

    @Inject
    MatriculaMapper matriculaMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/estudiante/{estudianteId}")
    @Operation(summary = "Buscar matrículas por estudiante")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findByEstudiante(estudianteId);
        return Response.ok(ApiResponse.success("Matrículas obtenidas", matriculas)).build();
    }

    @GET
    @Path("/seccion/{seccionId}")
    @Operation(summary = "Buscar matrículas por sección")
    public Response findBySeccion(@PathParam("seccionId") Long seccionId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findBySeccion(seccionId);
        return Response.ok(ApiResponse.success("Matrículas obtenidas", matriculas)).build();
    }

    @GET
    @Path("/periodo/{periodoId}")
    @Operation(summary = "Buscar matrículas por período académico")
    public Response findByPeriodoAcademico(@PathParam("periodoId") Long periodoId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findByPeriodoAcademico(periodoId);
        return Response.ok(ApiResponse.success("Matrículas obtenidas", matriculas)).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/periodo/{periodoId}")
    @Operation(summary = "Buscar matrículas por estudiante y período")
    public Response findByEstudianteAndPeriodo(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("periodoId") Long periodoId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findByEstudianteAndPeriodo(estudianteId, periodoId);
        return Response.ok(ApiResponse.success("Matrículas obtenidas", matriculas)).build();
    }

    @GET
    @Path("/estado/{estadoMatricula}")
    @Operation(summary = "Buscar matrículas por estado")
    public Response findByEstadoMatricula(@PathParam("estadoMatricula") String estadoMatricula) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findByEstadoMatricula(estadoMatricula);
        return Response.ok(ApiResponse.success("Matrículas obtenidas", matriculas)).build();
    }

    @GET
    @Path("/activas/periodo/{periodoId}")
    @Operation(summary = "Buscar matrículas activas por período")
    public Response findMatriculasActivas(@PathParam("periodoId") Long periodoId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findMatriculasActivas(periodoId);
        return Response.ok(ApiResponse.success("Matrículas activas obtenidas", matriculas)).build();
    }

    @GET
    @Path("/aprobadas/estudiante/{estudianteId}")
    @Operation(summary = "Buscar matrículas aprobadas por estudiante")
    public Response findMatriculasAprobadas(@PathParam("estudianteId") Long estudianteId) {
        List<MatriculaResponseDTO> matriculas = matriculaService.findMatriculasAprobadas(estudianteId);
        return Response.ok(ApiResponse.success("Matrículas aprobadas obtenidas", matriculas)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar matrícula por ID")
    public Response findById(@PathParam("id") Long id) {
        MatriculaResponseDTO matricula = matriculaService.findById(id);
        return Response.ok(ApiResponse.success("Matrícula obtenida", matricula)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Matricular estudiante")
    public Response create(@Valid MatriculaRequestDTO requestDTO) {

        // Convertir DTO a Command
        MatricularEstudianteCommand command = new MatricularEstudianteCommand(
                requestDTO.getEstudianteId(),
                requestDTO.getSeccionId(),
                requestDTO.getTipoMatricula(),
                null // observaciones
        );

        // Ejecutar Use Case
        Matricula matricula = matricularEstudianteUseCase.execute(command);

        // Convertir a DTO
        MatriculaResponseDTO response = matriculaMapper.toResponseDTO(matricula);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Matrícula creada exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}/retirar")
    @Operation(summary = "Retirar matrícula")
    public Response retirar(@PathParam("id") Long id) {

        // Ejecutar Use Case
        Matricula matricula = retirarMatriculaUseCase.execute(id);

        // Convertir a DTO
        MatriculaResponseDTO response = matriculaMapper.toResponseDTO(matricula);

        return Response.ok(ApiResponse.success("Estudiante retirado exitosamente", response)).build();
    }
}
