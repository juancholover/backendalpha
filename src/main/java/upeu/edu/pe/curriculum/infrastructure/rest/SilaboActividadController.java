package upeu.edu.pe.curriculum.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.curriculum.application.dto.SilaboActividadRequestDTO;
import upeu.edu.pe.curriculum.application.dto.SilaboActividadResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.SilaboActividadMapper;
import upeu.edu.pe.curriculum.domain.commands.AgregarActividadUnidadCommand;
import upeu.edu.pe.curriculum.domain.entities.SilaboActividad;
import upeu.edu.pe.curriculum.domain.services.SilaboActividadService;
import upeu.edu.pe.curriculum.domain.usecases.AgregarActividadUnidadUseCase;
import upeu.edu.pe.curriculum.domain.usecases.EliminarActividadSilaboUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de Actividades de Sílabo.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/silabos-actividades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Actividades de Sílabo", description = "Gestión de actividades de aprendizaje y evaluación")
public class SilaboActividadController {

    // Use Cases para operaciones de escritura
    @Inject
    AgregarActividadUnidadUseCase agregarActividadUseCase;

    @Inject
    EliminarActividadSilaboUseCase eliminarActividadUseCase;

    // Service para operaciones de lectura
    @Inject
    SilaboActividadService silaboActividadService;

    @Inject
    SilaboActividadMapper silaboActividadMapper;

    @Context
    SecurityContext securityContext;

    private String obtenerUsuarioActual() {
        if (securityContext != null && securityContext.getUserPrincipal() != null) {
            return securityContext.getUserPrincipal().getName();
        }
        return "SYSTEM";
    }

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar actividad por ID")
    public Response buscarPorId(@PathParam("id") Long id) {
        SilaboActividadResponseDTO actividad = silaboActividadService.buscarPorId(id);
        return Response.ok(ApiResponse.success("Actividad encontrada", actividad)).build();
    }

    @GET
    @Path("/unidad/{unidadId}")
    @Operation(summary = "Listar actividades de una unidad")
    public Response listarPorUnidad(@PathParam("unidadId") Long unidadId) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.listarPorUnidad(unidadId);
        return Response.ok(ApiResponse.success("Actividades de la unidad", actividades)).build();
    }

    @GET
    @Path("/unidad/{unidadId}/tipo/{tipo}")
    @Operation(summary = "Listar actividades por tipo")
    public Response listarPorUnidadYTipo(
            @PathParam("unidadId") Long unidadId,
            @PathParam("tipo") String tipo) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.listarPorUnidadYTipo(unidadId, tipo);
        return Response.ok(ApiResponse.success("Actividades de tipo " + tipo, actividades)).build();
    }

    @GET
    @Path("/unidad/{unidadId}/sumativas")
    @Operation(summary = "Listar actividades sumativas")
    public Response listarSumativasPorUnidad(@PathParam("unidadId") Long unidadId) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.listarSumativasPorUnidad(unidadId);
        return Response.ok(ApiResponse.success("Actividades sumativas", actividades)).build();
    }

    @GET
    @Path("/unidad/{unidadId}/formativas")
    @Operation(summary = "Listar actividades formativas")
    public Response listarFormativasPorUnidad(@PathParam("unidadId") Long unidadId) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.listarFormativasPorUnidad(unidadId);
        return Response.ok(ApiResponse.success("Actividades formativas", actividades)).build();
    }

    @GET
    @Path("/silabo/{silaboId}/semana/{semana}")
    @Operation(summary = "Buscar actividades por semana")
    public Response buscarPorSemana(
            @PathParam("silaboId") Long silaboId,
            @PathParam("semana") Integer semana) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.buscarPorSemana(silaboId, semana);
        return Response.ok(ApiResponse.success("Actividades de la semana " + semana, actividades)).build();
    }

    @GET
    @Path("/unidad/{unidadId}/ponderacion-total")
    @Operation(summary = "Calcular ponderación total de una unidad")
    public Response calcularPonderacionTotalUnidad(@PathParam("unidadId") Long unidadId) {
        BigDecimal total = silaboActividadService.calcularPonderacionTotalUnidad(unidadId);
        return Response.ok(ApiResponse.success("Ponderación: " + total + "%", total)).build();
    }

    @GET
    @Path("/silabo/{silaboId}/ponderacion-total")
    @Operation(summary = "Calcular ponderación total del sílabo")
    public Response calcularPonderacionTotalSilabo(@PathParam("silaboId") Long silaboId) {
        BigDecimal total = silaboActividadService.calcularPonderacionTotalSilabo(silaboId);
        String msg = total.compareTo(new BigDecimal("100")) == 0
                ? "Sílabo completo (100%)"
                : "Advertencia: " + total + "% (debe ser 100%)";
        return Response.ok(ApiResponse.success(msg, total)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Path("/actividades")
    @Operation(summary = "Agregar actividad a unidad")
    @APIResponse(responseCode = "201", description = "Actividad agregada exitosamente")
    public Response agregar(@Valid SilaboActividadRequestDTO dto) {
        String usuario = obtenerUsuarioActual();

        AgregarActividadUnidadCommand command = new AgregarActividadUnidadCommand(
                dto.getUnidadId(),
                dto.getTipo(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getPonderacion(),
                dto.getSemanaProgramada(),
                dto.getInstrumentoEvaluacion(),
                dto.getIndicadores(),
                dto.getCriteriosEvaluacion());

        SilaboActividad actividad = agregarActividadUseCase.execute(command, usuario);
        SilaboActividadResponseDTO response = silaboActividadMapper.toResponseDTO(actividad);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Actividad agregada", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar actividad")
    public Response eliminar(@PathParam("id") Long id) {
        eliminarActividadUseCase.execute(id);
        return Response.ok(ApiResponse.success("Actividad eliminada", null)).build();
    }
}
