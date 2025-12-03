package upeu.edu.pe.academic.infrastructure.web;

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
import upeu.edu.pe.academic.application.dto.SilaboActividadRequestDTO;
import upeu.edu.pe.academic.application.dto.SilaboActividadResponseDTO;
import upeu.edu.pe.academic.domain.services.SilaboActividadService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de Actividades de Sílabo
 * 
 * Endpoints:
 * - POST   /api/v1/silabos/actividades                              - Agregar actividad a unidad
 * - GET    /api/v1/silabos/actividades/{id}                         - Buscar actividad por ID
 * - PUT    /api/v1/silabos/actividades/{id}                         - Actualizar actividad
 * - DELETE /api/v1/silabos/actividades/{id}                         - Eliminar actividad
 * - GET    /api/v1/silabos/unidades/{unidadId}/actividades          - Listar actividades de una unidad
 * - GET    /api/v1/silabos/unidades/{unidadId}/actividades/tipo/{tipo} - Filtrar por tipo
 * - GET    /api/v1/silabos/unidades/{unidadId}/actividades/sumativas - Solo sumativas
 * - GET    /api/v1/silabos/unidades/{unidadId}/actividades/formativas - Solo formativas
 * - GET    /api/v1/silabos/{silaboId}/actividades/semana/{semana}   - Actividades por semana
 * - GET    /api/v1/silabos/unidades/{unidadId}/ponderacion-total    - Suma de ponderaciones
 * - GET    /api/v1/silabos/{silaboId}/ponderacion-total             - Ponderación total del sílabo
 */
@Path("/api/v1/silabos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Actividades de Sílabo", description = "Gestión de actividades de aprendizaje y evaluación")
public class SilaboActividadController {

    @Inject
    SilaboActividadService silaboActividadService;

    @Context
    SecurityContext securityContext;

    private String obtenerUsuarioActual() {
        if (securityContext != null && securityContext.getUserPrincipal() != null) {
            return securityContext.getUserPrincipal().getName();
        }
        return "SYSTEM";
    }

    @POST
    @Path("/actividades")
    @Operation(summary = "Agregar actividad a unidad", 
               description = "Crea una nueva actividad en una unidad. Valida ponderación para actividades sumativas.")
    @APIResponse(responseCode = "201", description = "Actividad agregada exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos o sílabo no modificable")
    @APIResponse(responseCode = "409", description = "Ponderación excede el 100%")
    public Response agregar(@Valid SilaboActividadRequestDTO dto) {
        String usuario = obtenerUsuarioActual();
        SilaboActividadResponseDTO actividad = silaboActividadService.agregar(dto, usuario);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Actividad agregada a la unidad exitosamente", actividad))
                .build();
    }

    @GET
    @Path("/actividades/{id}")
    @Operation(summary = "Buscar actividad por ID", description = "Obtiene una actividad por su ID")
    @APIResponse(responseCode = "200", description = "Actividad encontrada")
    @APIResponse(responseCode = "404", description = "Actividad no encontrada")
    public Response buscarPorId(
            @Parameter(description = "ID de la actividad") 
            @PathParam("id") Long id) {
        SilaboActividadResponseDTO actividad = silaboActividadService.buscarPorId(id);
        return Response.ok(ApiResponse.success("Actividad encontrada", actividad)).build();
    }

    @PUT
    @Path("/actividades/{id}")
    @Operation(summary = "Actualizar actividad", 
               description = "Actualiza una actividad existente. El sílabo debe estar en estado modificable.")
    @APIResponse(responseCode = "200", description = "Actividad actualizada exitosamente")
    @APIResponse(responseCode = "404", description = "Actividad no encontrada")
    @APIResponse(responseCode = "409", description = "Sílabo no modificable")
    public Response actualizar(
            @Parameter(description = "ID de la actividad") 
            @PathParam("id") Long id,
            @Valid SilaboActividadRequestDTO dto) {
        String usuario = obtenerUsuarioActual();
        SilaboActividadResponseDTO actividad = silaboActividadService.actualizar(id, dto, usuario);
        return Response.ok(ApiResponse.success("Actividad actualizada exitosamente", actividad)).build();
    }

    @DELETE
    @Path("/actividades/{id}")
    @Operation(summary = "Eliminar actividad", 
               description = "Elimina (lógicamente) una actividad. El sílabo debe estar en estado modificable.")
    @APIResponse(responseCode = "200", description = "Actividad eliminada exitosamente")
    @APIResponse(responseCode = "404", description = "Actividad no encontrada")
    @APIResponse(responseCode = "409", description = "Sílabo no modificable")
    public Response eliminar(
            @Parameter(description = "ID de la actividad") 
            @PathParam("id") Long id) {
        silaboActividadService.eliminar(id);
        return Response.ok(ApiResponse.success("Actividad eliminada exitosamente")).build();
    }

    @GET
    @Path("/unidades/{unidadId}/actividades")
    @Operation(summary = "Listar actividades de una unidad", 
               description = "Obtiene todas las actividades de una unidad ordenadas por semana")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarPorUnidad(
            @Parameter(description = "ID de la unidad") 
            @PathParam("unidadId") Long unidadId) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.listarPorUnidad(unidadId);
        return Response.ok(ApiResponse.success("Actividades de la unidad", actividades)).build();
    }

    @GET
    @Path("/unidades/{unidadId}/actividades/tipo/{tipo}")
    @Operation(summary = "Listar actividades por tipo", 
               description = "Filtra actividades por tipo (FORMATIVA o SUMATIVA)")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarPorUnidadYTipo(
            @Parameter(description = "ID de la unidad") 
            @PathParam("unidadId") Long unidadId,
            @Parameter(description = "Tipo de actividad (FORMATIVA/SUMATIVA)") 
            @PathParam("tipo") String tipo) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.listarPorUnidadYTipo(unidadId, tipo);
        return Response.ok(ApiResponse.success("Actividades de tipo " + tipo, actividades)).build();
    }

    @GET
    @Path("/unidades/{unidadId}/actividades/sumativas")
    @Operation(summary = "Listar actividades sumativas", 
               description = "Obtiene solo las actividades sumativas (con ponderación) de una unidad")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarSumativasPorUnidad(
            @Parameter(description = "ID de la unidad") 
            @PathParam("unidadId") Long unidadId) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.listarSumativasPorUnidad(unidadId);
        return Response.ok(ApiResponse.success("Actividades sumativas de la unidad", actividades)).build();
    }

    @GET
    @Path("/unidades/{unidadId}/actividades/formativas")
    @Operation(summary = "Listar actividades formativas", 
               description = "Obtiene solo las actividades formativas (sin ponderación) de una unidad")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarFormativasPorUnidad(
            @Parameter(description = "ID de la unidad") 
            @PathParam("unidadId") Long unidadId) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.listarFormativasPorUnidad(unidadId);
        return Response.ok(ApiResponse.success("Actividades formativas de la unidad", actividades)).build();
    }

    @GET
    @Path("/{silaboId}/actividades/semana/{semana}")
    @Operation(summary = "Buscar actividades por semana", 
               description = "Obtiene actividades programadas para una semana específica")
    @APIResponse(responseCode = "200", description = "Actividades encontradas")
    public Response buscarPorSemana(
            @Parameter(description = "ID del sílabo") 
            @PathParam("silaboId") Long silaboId,
            @Parameter(description = "Número de semana (1-20)") 
            @PathParam("semana") Integer semana) {
        List<SilaboActividadResponseDTO> actividades = silaboActividadService.buscarPorSemana(silaboId, semana);
        return Response.ok(ApiResponse.success("Actividades de la semana " + semana, actividades)).build();
    }

    @GET
    @Path("/unidades/{unidadId}/ponderacion-total")
    @Operation(summary = "Calcular ponderación total de una unidad", 
               description = "Suma las ponderaciones de todas las actividades sumativas de una unidad")
    @APIResponse(responseCode = "200", description = "Cálculo exitoso")
    public Response calcularPonderacionTotalUnidad(
            @Parameter(description = "ID de la unidad") 
            @PathParam("unidadId") Long unidadId) {
        BigDecimal total = silaboActividadService.calcularPonderacionTotalUnidad(unidadId);
        return Response.ok(ApiResponse.success(
            "Ponderación total de la unidad: " + total + "%", 
            total
        )).build();
    }

    @GET
    @Path("/{silaboId}/ponderacion-total")
    @Operation(summary = "Calcular ponderación total del sílabo", 
               description = "Suma las ponderaciones de todas las actividades sumativas del sílabo (debe ser 100%)")
    @APIResponse(responseCode = "200", description = "Cálculo exitoso")
    public Response calcularPonderacionTotalSilabo(
            @Parameter(description = "ID del sílabo") 
            @PathParam("silaboId") Long silaboId) {
        BigDecimal total = silaboActividadService.calcularPonderacionTotalSilabo(silaboId);
        String mensaje = total.compareTo(new BigDecimal("100")) == 0 
            ? "El sílabo está completo (100%)" 
            : "Advertencia: El sílabo tiene " + total + "% de ponderación (debe ser 100%)";
        
        return Response.ok(ApiResponse.success(mensaje, total)).build();
    }
}
