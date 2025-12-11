package upeu.edu.pe.enrollment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.enrollment.application.dto.HorarioResponseDTO;
import upeu.edu.pe.enrollment.domain.services.HorarioService;
import upeu.edu.pe.enrollment.domain.usecases.EliminarHorarioUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para horarios.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/horarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Horarios", description = "Gestión de horarios de cursos ofertados")
public class HorarioController {

    // Use Cases para operaciones de escritura
    @Inject
    EliminarHorarioUseCase eliminarUseCase;

    // Service para operaciones de lectura
    @Inject
    HorarioService horarioService;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar horarios por universidad")
    public Response findByUniversidad(@QueryParam("universidadId") Long universidadId) {
        if (universidadId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro universidadId es obligatorio"))
                    .build();
        }
        List<HorarioResponseDTO> horarios = horarioService.findByUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Horarios obtenidos", horarios)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar horario por ID")
    public Response findById(@PathParam("id") Long id) {
        HorarioResponseDTO horario = horarioService.findById(id);
        return Response.ok(ApiResponse.success("Horario encontrado", horario)).build();
    }

    @GET
    @Path("/curso-ofertado/{cursoOfertadoId}")
    @Operation(summary = "Listar horarios de un curso ofertado")
    public Response findByCursoOfertado(@PathParam("cursoOfertadoId") Long cursoOfertadoId) {
        List<HorarioResponseDTO> horarios = horarioService.findByCursoOfertado(cursoOfertadoId);
        return Response.ok(ApiResponse.success("Horarios del curso", horarios)).build();
    }

    @GET
    @Path("/estudiante/{estudianteId}")
    @Operation(summary = "Listar horarios de un estudiante")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<HorarioResponseDTO> horarios = horarioService.findByEstudiante(estudianteId);
        return Response.ok(ApiResponse.success("Horario del estudiante", horarios)).build();
    }

    @GET
    @Path("/profesor/{profesorId}")
    @Operation(summary = "Listar horarios de un profesor")
    public Response findByProfesor(@PathParam("profesorId") Long profesorId) {
        List<HorarioResponseDTO> horarios = horarioService.findByProfesor(profesorId);
        return Response.ok(ApiResponse.success("Horario del profesor", horarios)).build();
    }

    @GET
    @Path("/dia/{diaSemana}")
    @Operation(summary = "Listar horarios por día de la semana")
    public Response findByDiaSemana(
            @PathParam("diaSemana") Integer diaSemana,
            @QueryParam("universidadId") Long universidadId) {
        if (universidadId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro universidadId es obligatorio"))
                    .build();
        }
        List<HorarioResponseDTO> horarios = horarioService.findByDiaSemana(diaSemana, universidadId);
        return Response.ok(ApiResponse.success("Horarios del día", horarios)).build();
    }

    @GET
    @Path("/localizacion/{localizacionId}")
    @Operation(summary = "Listar horarios de una localización")
    public Response findByLocalizacion(@PathParam("localizacionId") Long localizacionId) {
        List<HorarioResponseDTO> horarios = horarioService.findByLocalizacion(localizacionId);
        return Response.ok(ApiResponse.success("Horarios de la localización", horarios)).build();
    }

    @GET
    @Path("/validar-cruce")
    @Operation(summary = "Validar cruce de horarios")
    public Response validarCruce(
            @QueryParam("estudianteId") Long estudianteId,
            @QueryParam("cursoOfertadoId") Long cursoOfertadoId) {
        if (estudianteId == null || cursoOfertadoId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("estudianteId y cursoOfertadoId son obligatorios"))
                    .build();
        }

        boolean tieneCruce = horarioService.tieneCreceHorario(estudianteId, cursoOfertadoId);

        if (tieneCruce) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiResponse.error("Cruce de horarios detectado"))
                    .build();
        }

        return Response.ok(ApiResponse.success("No hay cruce de horarios", null)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar horario")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Horario eliminado", null)).build();
    }
}
