package upeu.edu.pe.assessment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.assessment.application.dto.EvaluacionNotaRequestDTO;
import upeu.edu.pe.assessment.application.dto.EvaluacionNotaResponseDTO;
import upeu.edu.pe.assessment.application.mapper.EvaluacionNotaMapper;
import upeu.edu.pe.assessment.domain.commands.RegistrarNotaCommand;
import upeu.edu.pe.assessment.domain.commands.RegistrarNotaRecuperacionCommand;
import upeu.edu.pe.assessment.domain.entities.EvaluacionNota;
import upeu.edu.pe.assessment.domain.services.EvaluacionNotaService;
import upeu.edu.pe.assessment.domain.usecases.CalcularPromedioFinalUseCase;
import upeu.edu.pe.assessment.domain.usecases.RegistrarNotaRecuperacionUseCase;
import upeu.edu.pe.assessment.domain.usecases.RegistrarNotaUseCase;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de notas de evaluación.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 * 
 * @author Sistema UPeU
 */
@Path("/api/v1/evaluacion-notas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Notas de Evaluación", description = "Gestión de calificaciones y notas")
public class EvaluacionNotaController {

    // Use Cases para operaciones de escritura
    @Inject
    RegistrarNotaUseCase registrarNotaUseCase;

    @Inject
    RegistrarNotaRecuperacionUseCase registrarRecuperacionUseCase;

    @Inject
    CalcularPromedioFinalUseCase calcularPromedioUseCase;

    // Service para operaciones de lectura
    @Inject
    EvaluacionNotaService notaService;

    @Inject
    EvaluacionNotaMapper mapper;

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Registrar nota", description = "Registra una nueva nota para un estudiante en un criterio")
    public Response registrarNota(@Valid EvaluacionNotaRequestDTO requestDTO) {

        // Convertir DTO a Command
        RegistrarNotaCommand command = new RegistrarNotaCommand(
                requestDTO.getMatriculaId(),
                requestDTO.getCriterioId(),
                requestDTO.getNota(),
                requestDTO.getObservacion(),
                requestDTO.getFechaEvaluacion());

        // Ejecutar Use Case
        EvaluacionNota nota = registrarNotaUseCase.execute(command);

        // Convertir a DTO de respuesta
        EvaluacionNotaResponseDTO response = mapper.toResponseDTO(nota);

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}/recuperacion")
    @Operation(summary = "Registrar nota de recuperación", description = "Registra una nota de recuperación para una evaluación existente")
    public Response registrarRecuperacion(
            @PathParam("id") Long notaId,
            @QueryParam("nota") BigDecimal notaRecuperacion,
            @QueryParam("observacion") String observacion) {

        // Convertir a Command
        RegistrarNotaRecuperacionCommand command = new RegistrarNotaRecuperacionCommand(
                notaId,
                notaRecuperacion,
                observacion);

        // Ejecutar Use Case
        EvaluacionNota nota = registrarRecuperacionUseCase.execute(command);

        // Convertir a DTO de respuesta
        EvaluacionNotaResponseDTO response = mapper.toResponseDTO(nota);

        return Response.ok(response).build();
    }

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener nota por ID", description = "Obtiene los detalles de una nota específica")
    public Response findById(@PathParam("id") Long id) {
        EvaluacionNotaResponseDTO nota = notaService.findById(id);
        return Response.ok(nota).build();
    }

    @GET
    @Path("/matricula/{matriculaId}")
    @Operation(summary = "Listar notas por matrícula", description = "Obtiene todas las notas de una matrícula")
    public Response findByMatricula(@PathParam("matriculaId") Long matriculaId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findByMatricula(matriculaId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/criterio/{criterioId}")
    @Operation(summary = "Listar notas por criterio", description = "Obtiene todas las notas de un criterio")
    public Response findByCriterio(@PathParam("criterioId") Long criterioId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findByCriterio(criterioId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/matricula/{matriculaId}/criterio/{criterioId}")
    @Operation(summary = "Obtener nota específica", description = "Obtiene la nota de una matrícula en un criterio específico")
    public Response findByMatriculaAndCriterio(
            @PathParam("matriculaId") Long matriculaId,
            @PathParam("criterioId") Long criterioId) {
        EvaluacionNotaResponseDTO nota = notaService.findByMatriculaAndCriterio(matriculaId, criterioId);
        return Response.ok(nota).build();
    }

    @GET
    @Path("/seccion/{seccionId}/pendientes")
    @Operation(summary = "Listar notas pendientes", description = "Obtiene notas pendientes de calificar en una sección")
    public Response findPendientesBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findPendientesBySeccion(seccionId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/seccion/{seccionId}/calificadas")
    @Operation(summary = "Listar notas calificadas", description = "Obtiene notas ya calificadas en una sección")
    public Response findCalificadasBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findCalificadasBySeccion(seccionId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/seccion/{seccionId}")
    @Operation(summary = "Listar notas por estudiante y sección", description = "Obtiene todas las notas de un estudiante en una sección")
    public Response findByEstudianteAndSeccion(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findByEstudianteAndSeccion(estudianteId, seccionId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/seccion/{seccionId}/con-recuperacion")
    @Operation(summary = "Listar notas con recuperación", description = "Obtiene notas que tienen nota de recuperación registrada")
    public Response findConRecuperacionBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findConRecuperacionBySeccion(seccionId);
        return Response.ok(notas).build();
    }

    @GET
    @Path("/seccion/{seccionId}/desaprobadas")
    @Operation(summary = "Listar notas desaprobadas", description = "Obtiene notas con calificación inferior a la mínima aprobatoria")
    public Response findDesaprobadasBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionNotaResponseDTO> notas = notaService.findDesaprobadasBySeccion(seccionId);
        return Response.ok(notas).build();
    }

    // =====================================================
    // OPERACIONES DE CÁLCULO (Use Cases)
    // =====================================================

    @GET
    @Path("/estudiante/{estudianteId}/seccion/{seccionId}/promedio")
    @Operation(summary = "Calcular promedio final", description = "Calcula el promedio ponderado del estudiante en la sección")
    public Response calcularPromedio(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("seccionId") Long seccionId) {

        // Ejecutar Use Case
        CalcularPromedioFinalUseCase.ResultadoPromedio resultado = calcularPromedioUseCase.execute(estudianteId,
                seccionId);

        return Response.ok(resultado).build();
    }

    // =====================================================
    // OPERACIONES DE ESTADÍSTICAS
    // =====================================================

    @GET
    @Path("/criterio/{criterioId}/promedio")
    @Operation(summary = "Obtener promedio de criterio", description = "Calcula el promedio de notas de un criterio")
    public Response getPromedioNotasByCriterio(@PathParam("criterioId") Long criterioId) {
        Double promedio = notaService.getPromedioNotasByCriterio(criterioId);
        return Response.ok().entity("{\"promedio\": " + promedio + "}").build();
    }

    @GET
    @Path("/matricula/{matriculaId}/count-calificadas")
    @Operation(summary = "Contar notas calificadas", description = "Cuenta las notas calificadas de una matrícula")
    public Response countCalificadasByMatricula(@PathParam("matriculaId") Long matriculaId) {
        long count = notaService.countCalificadasByMatricula(matriculaId);
        return Response.ok().entity("{\"count\": " + count + "}").build();
    }
}
