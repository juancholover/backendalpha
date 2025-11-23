package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.academic.application.dto.HorarioRequestDTO;
import upeu.edu.pe.academic.application.dto.HorarioResponseDTO;
import upeu.edu.pe.academic.domain.services.HorarioService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/horarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HorarioController {

    @Inject
    HorarioService horarioService;

    /**
     * GET /api/horarios?universidadId=1
     * Obtiene todos los horarios de una universidad
     */
    @GET
    public Response findByUniversidad(@QueryParam("universidadId") Long universidadId) {
        if (universidadId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro universidadId es obligatorio"))
                    .build();
        }

        List<HorarioResponseDTO> horarios = horarioService.findByUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Horarios recuperados exitosamente", horarios)).build();
    }

    /**
     * GET /api/horarios/{id}
     * Obtiene un horario por ID
     */
    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        HorarioResponseDTO horario = horarioService.findById(id);
        return Response.ok(ApiResponse.success("Horario recuperado exitosamente", horario)).build();
    }

    /**
     * GET /api/horarios/curso-ofertado/{cursoOfertadoId}
     * Obtiene todos los horarios de un curso ofertado
     */
    @GET
    @Path("/curso-ofertado/{cursoOfertadoId}")
    public Response findByCursoOfertado(@PathParam("cursoOfertadoId") Long cursoOfertadoId) {
        List<HorarioResponseDTO> horarios = horarioService.findByCursoOfertado(cursoOfertadoId);
        return Response.ok(ApiResponse.success("Horarios del curso recuperados exitosamente", horarios)).build();
    }

    /**
     * GET /api/horarios/estudiante/{estudianteId}
     * Obtiene el horario completo de un estudiante
     */
    @GET
    @Path("/estudiante/{estudianteId}")
    public Response findByEstudiante(@PathParam("estudianteId") Long estudianteId) {
        List<HorarioResponseDTO> horarios = horarioService.findByEstudiante(estudianteId);
        return Response.ok(ApiResponse.success("Horario del estudiante recuperado exitosamente", horarios)).build();
    }

    /**
     * GET /api/horarios/profesor/{profesorId}
     * Obtiene el horario de un profesor
     */
    @GET
    @Path("/profesor/{profesorId}")
    public Response findByProfesor(@PathParam("profesorId") Long profesorId) {
        List<HorarioResponseDTO> horarios = horarioService.findByProfesor(profesorId);
        return Response.ok(ApiResponse.success("Horario del profesor recuperado exitosamente", horarios)).build();
    }

    /**
     * GET /api/horarios/dia/{diaSemana}?universidadId=1
     * Obtiene horarios por día de la semana (1=Lunes...7=Domingo)
     */
    @GET
    @Path("/dia/{diaSemana}")
    public Response findByDiaSemana(@PathParam("diaSemana") Integer diaSemana,
                                    @QueryParam("universidadId") Long universidadId) {
        if (universidadId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro universidadId es obligatorio"))
                    .build();
        }

        List<HorarioResponseDTO> horarios = horarioService.findByDiaSemana(diaSemana, universidadId);
        return Response.ok(ApiResponse.success("Horarios del día recuperados exitosamente", horarios)).build();
    }

    /**
     * GET /api/horarios/localizacion/{localizacionId}
     * Obtiene horarios de una localización (aula)
     */
    @GET
    @Path("/localizacion/{localizacionId}")
    public Response findByLocalizacion(@PathParam("localizacionId") Long localizacionId) {
        List<HorarioResponseDTO> horarios = horarioService.findByLocalizacion(localizacionId);
        return Response.ok(ApiResponse.success("Horarios de la localización recuperados exitosamente", horarios)).build();
    }

    /**
     * POST /api/horarios
     * Crea un nuevo horario
     */
    @POST
    public Response create(@Valid HorarioRequestDTO dto) {
        HorarioResponseDTO horario = horarioService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Horario creado exitosamente", horario))
                .build();
    }

    /**
     * PUT /api/horarios/{id}
     * Actualiza un horario existente
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid HorarioRequestDTO dto) {
        HorarioResponseDTO horario = horarioService.update(id, dto);
        return Response.ok(ApiResponse.success("Horario actualizado exitosamente", horario)).build();
    }

    /**
     * DELETE /api/horarios/{id}
     * Elimina un horario (lógicamente)
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        horarioService.delete(id);
        return Response.ok(ApiResponse.success(null, "Horario eliminado exitosamente")).build();
    }

    /**
     * GET /api/horarios/validar-cruce?estudianteId=1&cursoOfertadoId=5
     * Valida si hay cruce de horarios antes de matricular
     */
    @GET
    @Path("/validar-cruce")
    public Response validarCruce(@QueryParam("estudianteId") Long estudianteId,
                                 @QueryParam("cursoOfertadoId") Long cursoOfertadoId) {
        if (estudianteId == null || cursoOfertadoId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Los parámetros estudianteId y cursoOfertadoId son obligatorios"))
                    .build();
        }

        boolean tieneCruce = horarioService.tieneCreceHorario(estudianteId, cursoOfertadoId);
        
        if (tieneCruce) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiResponse.error("El estudiante tiene cruce de horarios con este curso"))
                    .build();
        }

        return Response.ok(ApiResponse.success(null, "No hay cruce de horarios")).build();
    }
}
