package upeu.edu.pe.assessment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.assessment.application.dto.AsistenciaAlumnoRequestDTO;
import upeu.edu.pe.assessment.application.dto.AsistenciaAlumnoResponseDTO;
import upeu.edu.pe.assessment.application.mapper.AsistenciaAlumnoMapper;
import upeu.edu.pe.assessment.domain.commands.RegistrarAsistenciaCommand;
import upeu.edu.pe.assessment.domain.entities.AsistenciaAlumno;
import upeu.edu.pe.assessment.domain.services.AsistenciaAlumnoService;
import upeu.edu.pe.assessment.domain.usecases.RegistrarAsistenciaUseCase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gestión de asistencia de estudiantes.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA y estadísticas delegadas al Service
 * 
 * @author Sistema UPeU
 */
@Path("/api/v1/asistencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Asistencia", description = "Gestión de asistencia de estudiantes")
public class AsistenciaAlumnoController {

    // Use Cases para operaciones de escritura
    @Inject
    RegistrarAsistenciaUseCase registrarAsistenciaUseCase;

    // Service para operaciones de lectura
    @Inject
    AsistenciaAlumnoService asistenciaService;

    @Inject
    AsistenciaAlumnoMapper mapper;

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Registrar asistencia", description = "Registra la asistencia de un estudiante a una clase")
    public Response registrarAsistencia(@Valid AsistenciaAlumnoRequestDTO requestDTO) {

        // Convertir DTO a Command
        RegistrarAsistenciaCommand command = new RegistrarAsistenciaCommand(
                requestDTO.getEstudianteId(),
                requestDTO.getHorarioId(),
                requestDTO.getFechaClase(),
                requestDTO.getEstado(),
                requestDTO.getObservaciones(),
                requestDTO.getMinutosTardanza());

        // Ejecutar Use Case
        AsistenciaAlumno asistencia = registrarAsistenciaUseCase.execute(command);

        // Convertir a DTO de respuesta
        AsistenciaAlumnoResponseDTO response = mapper.toResponseDTO(asistencia);

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/masiva")
    @Operation(summary = "Registrar asistencia masiva", description = "Registra asistencia de múltiples estudiantes a la vez")
    public Response registrarAsistenciaMasiva(List<AsistenciaAlumnoRequestDTO> requests) {

        List<AsistenciaAlumnoResponseDTO> respuestas = requests.stream()
                .map(requestDTO -> {
                    RegistrarAsistenciaCommand command = new RegistrarAsistenciaCommand(
                            requestDTO.getEstudianteId(),
                            requestDTO.getHorarioId(),
                            requestDTO.getFechaClase(),
                            requestDTO.getEstado(),
                            requestDTO.getObservaciones(),
                            requestDTO.getMinutosTardanza());
                    AsistenciaAlumno asistencia = registrarAsistenciaUseCase.execute(command);
                    return mapper.toResponseDTO(asistencia);
                })
                .toList();

        return Response.status(Response.Status.CREATED).entity(respuestas).build();
    }

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener asistencia por ID", description = "Obtiene los detalles de un registro de asistencia")
    public Response findById(@PathParam("id") Long id) {
        AsistenciaAlumnoResponseDTO asistencia = asistenciaService.findById(id);
        return Response.ok(asistencia).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/seccion/{seccionId}")
    @Operation(summary = "Listar asistencias por estudiante y sección", description = "Obtiene todas las asistencias de un estudiante en una sección")
    public Response findByEstudianteAndSeccion(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("seccionId") Long seccionId) {
        List<AsistenciaAlumnoResponseDTO> asistencias = asistenciaService.findByEstudianteAndSeccion(estudianteId,
                seccionId);
        return Response.ok(asistencias).build();
    }

    @GET
    @Path("/horario/{horarioId}/fecha/{fecha}")
    @Operation(summary = "Listar asistencias por horario y fecha", description = "Obtiene las asistencias de un horario en una fecha específica")
    public Response findByHorarioAndFecha(
            @PathParam("horarioId") Long horarioId,
            @PathParam("fecha") LocalDate fecha) {
        List<AsistenciaAlumnoResponseDTO> asistencias = asistenciaService.findByHorarioAndFecha(horarioId, fecha);
        return Response.ok(asistencias).build();
    }

    @GET
    @Path("/seccion/{seccionId}/rango")
    @Operation(summary = "Listar asistencias por rango de fechas", description = "Obtiene asistencias de una sección en un rango de fechas")
    public Response findBySeccionAndFechaRange(
            @PathParam("seccionId") Long seccionId,
            @QueryParam("fechaInicio") LocalDate fechaInicio,
            @QueryParam("fechaFin") LocalDate fechaFin) {
        List<AsistenciaAlumnoResponseDTO> asistencias = asistenciaService.findBySeccionAndFechaRange(seccionId,
                fechaInicio, fechaFin);
        return Response.ok(asistencias).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/seccion/{seccionId}/ausencias")
    @Operation(summary = "Listar ausencias", description = "Obtiene las ausencias de un estudiante en una sección")
    public Response findAusenciasByEstudianteAndSeccion(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("seccionId") Long seccionId) {
        List<AsistenciaAlumnoResponseDTO> ausencias = asistenciaService
                .findAusenciasByEstudianteAndSeccion(estudianteId, seccionId);
        return Response.ok(ausencias).build();
    }

    // =====================================================
    // OPERACIONES DE ESTADÍSTICAS
    // =====================================================

    @GET
    @Path("/estudiante/{estudianteId}/seccion/{seccionId}/porcentaje")
    @Operation(summary = "Calcular porcentaje de asistencia", description = "Calcula el porcentaje de asistencia de un estudiante")
    public Response calcularPorcentajeAsistencia(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("seccionId") Long seccionId) {
        BigDecimal porcentaje = asistenciaService.calcularPorcentajeAsistencia(estudianteId, seccionId);
        boolean enRiesgo = asistenciaService.estaEnRiesgoInhabilitacion(estudianteId, seccionId);

        return Response.ok()
                .entity("{\"porcentaje\": " + porcentaje + ", \"enRiesgo\": " + enRiesgo + "}")
                .build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/seccion/{seccionId}/resumen")
    @Operation(summary = "Obtener resumen de asistencia", description = "Obtiene un resumen completo de asistencia del estudiante")
    public Response getResumenAsistencia(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("seccionId") Long seccionId) {
        AsistenciaAlumnoService.ResumenAsistencia resumen = asistenciaService.getResumenAsistencia(estudianteId,
                seccionId);
        return Response.ok(resumen).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}/seccion/{seccionId}/riesgo")
    @Operation(summary = "Verificar riesgo de inhabilitación", description = "Verifica si el estudiante está en riesgo de inhabilitación por asistencia")
    public Response verificarRiesgoInhabilitacion(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("seccionId") Long seccionId) {
        boolean enRiesgo = asistenciaService.estaEnRiesgoInhabilitacion(estudianteId, seccionId);
        BigDecimal porcentaje = asistenciaService.calcularPorcentajeAsistencia(estudianteId, seccionId);

        String mensaje = enRiesgo
                ? "ALERTA: El estudiante tiene " + porcentaje
                        + "% de asistencia. Riesgo de inhabilitación (mínimo 70%)."
                : "El estudiante tiene " + porcentaje + "% de asistencia. Estado normal.";

        return Response.ok()
                .entity("{\"enRiesgo\": " + enRiesgo + ", \"porcentaje\": " + porcentaje + ", \"mensaje\": \"" + mensaje
                        + "\"}")
                .build();
    }
}
