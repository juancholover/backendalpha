package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import upeu.edu.pe.academic.application.dto.AsistenciaAlumnoRequestDTO;
import upeu.edu.pe.academic.application.dto.AsistenciaAlumnoResponseDTO;
import upeu.edu.pe.academic.domain.services.AsistenciaAlumnoService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/api/asistencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AsistenciaAlumnoController {

    @Inject
    AsistenciaAlumnoService asistenciaService;

    @GET
    public ApiResponse<List<AsistenciaAlumnoResponseDTO>> getAll() {
        return ApiResponse.success("Asistencias obtenidas exitosamente", asistenciaService.findAll());
    }

    @GET
    @Path("/{id}")
    public ApiResponse<AsistenciaAlumnoResponseDTO> getById(@PathParam("id") Long id) {
        return ApiResponse.success("Asistencia obtenida exitosamente", asistenciaService.findById(id));
    }

    @GET
    @Path("/estudiante/{estudianteId}")
    public ApiResponse<List<AsistenciaAlumnoResponseDTO>> getByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        return ApiResponse.success("Asistencias del estudiante obtenidas exitosamente", 
                asistenciaService.findByEstudiante(estudianteId));
    }

    @GET
    @Path("/estudiante/{estudianteId}/rango")
    public ApiResponse<List<AsistenciaAlumnoResponseDTO>> getByEstudianteAndFechaRange(
            @PathParam("estudianteId") Long estudianteId,
            @QueryParam("fechaInicio") String fechaInicio,
            @QueryParam("fechaFin") String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        return ApiResponse.success("Asistencias del estudiante en el rango obtenidas exitosamente", 
                asistenciaService.findByEstudianteAndFechaRange(estudianteId, inicio, fin));
    }

    @GET
    @Path("/estudiante/{estudianteId}/fecha/{fecha}")
    public ApiResponse<List<AsistenciaAlumnoResponseDTO>> getByEstudianteAndFecha(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("fecha") String fecha) {
        LocalDate fechaClase = LocalDate.parse(fecha);
        return ApiResponse.success("Asistencias del estudiante en la fecha obtenidas exitosamente", 
                asistenciaService.findByEstudianteAndFecha(estudianteId, fechaClase));
    }

    @GET
    @Path("/horario/{horarioId}/fecha/{fecha}")
    public ApiResponse<List<AsistenciaAlumnoResponseDTO>> getByHorarioAndFecha(
            @PathParam("horarioId") Long horarioId,
            @PathParam("fecha") String fecha) {
        LocalDate fechaClase = LocalDate.parse(fecha);
        return ApiResponse.success("Asistencias del horario en la fecha obtenidas exitosamente", 
                asistenciaService.findByHorarioAndFecha(horarioId, fechaClase));
    }

    @GET
    @Path("/estadisticas/estudiante/{estudianteId}/horario/{horarioId}")
    public ApiResponse<Map<String, Object>> getEstadisticasAsistencia(
            @PathParam("estudianteId") Long estudianteId,
            @PathParam("horarioId") Long horarioId) {
        return ApiResponse.success("Estadísticas de asistencia obtenidas exitosamente", 
                asistenciaService.getEstadisticasAsistencia(estudianteId, horarioId));
    }

    @POST
    public ApiResponse<AsistenciaAlumnoResponseDTO> create(@Valid AsistenciaAlumnoRequestDTO requestDTO) {
        return ApiResponse.success("Asistencia registrada exitosamente", asistenciaService.create(requestDTO));
    }

    @PUT
    @Path("/{id}")
    public ApiResponse<AsistenciaAlumnoResponseDTO> update(@PathParam("id") Long id, @Valid AsistenciaAlumnoRequestDTO requestDTO) {
        return ApiResponse.success("Asistencia actualizada exitosamente", 
                asistenciaService.update(id, requestDTO));
    }

    @DELETE
    @Path("/{id}")
    public ApiResponse<Void> delete(@PathParam("id") Long id) {
        asistenciaService.delete(id);
        return ApiResponse.success("Asistencia eliminada exitosamente", null);
    }
}
