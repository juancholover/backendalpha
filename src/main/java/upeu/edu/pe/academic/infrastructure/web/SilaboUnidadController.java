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
import upeu.edu.pe.academic.application.dto.SilaboUnidadRequestDTO;
import upeu.edu.pe.academic.application.dto.SilaboUnidadResponseDTO;
import upeu.edu.pe.academic.domain.services.SilaboUnidadService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de Unidades de Sílabo
 * 
 * Endpoints:
 * - POST   /api/v1/silabos/unidades                     - Agregar unidad a sílabo
 * - GET    /api/v1/silabos/unidades/{id}                - Buscar unidad por ID
 * - PUT    /api/v1/silabos/unidades/{id}                - Actualizar unidad
 * - DELETE /api/v1/silabos/unidades/{id}                - Eliminar unidad
 * - GET    /api/v1/silabos/{silaboId}/unidades          - Listar unidades de un sílabo
 * - GET    /api/v1/silabos/{silaboId}/unidades/{numero} - Buscar unidad específica
 * - GET    /api/v1/silabos/{silaboId}/unidades/semana/{semana} - Unidades que incluyen una semana
 */
@Path("/api/v1/silabos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Unidades de Sílabo", description = "Gestión de unidades didácticas dentro de sílabos")
public class SilaboUnidadController {

    @Inject
    SilaboUnidadService silaboUnidadService;

    @Context
    SecurityContext securityContext;

    private String obtenerUsuarioActual() {
        if (securityContext != null && securityContext.getUserPrincipal() != null) {
            return securityContext.getUserPrincipal().getName();
        }
        return "SYSTEM";
    }

    @POST
    @Path("/unidades")
    @Operation(summary = "Agregar unidad a sílabo", 
               description = "Crea una nueva unidad didáctica en un sílabo. El sílabo debe estar en estado modificable.")
    @APIResponse(responseCode = "201", description = "Unidad agregada exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos o sílabo no modificable")
    @APIResponse(responseCode = "409", description = "Ya existe una unidad con ese número")
    public Response agregar(@Valid SilaboUnidadRequestDTO dto) {
        String usuario = obtenerUsuarioActual();
        SilaboUnidadResponseDTO unidad = silaboUnidadService.agregar(dto, usuario);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Unidad agregada al sílabo exitosamente", unidad))
                .build();
    }

    @GET
    @Path("/unidades/{id}")
    @Operation(summary = "Buscar unidad por ID", description = "Obtiene una unidad por su ID con todas sus actividades")
    @APIResponse(responseCode = "200", description = "Unidad encontrada")
    @APIResponse(responseCode = "404", description = "Unidad no encontrada")
    public Response buscarPorId(
            @Parameter(description = "ID de la unidad") 
            @PathParam("id") Long id) {
        SilaboUnidadResponseDTO unidad = silaboUnidadService.buscarPorId(id);
        return Response.ok(ApiResponse.success("Unidad encontrada", unidad)).build();
    }

    @PUT
    @Path("/unidades/{id}")
    @Operation(summary = "Actualizar unidad", 
               description = "Actualiza una unidad existente. El sílabo debe estar en estado modificable.")
    @APIResponse(responseCode = "200", description = "Unidad actualizada exitosamente")
    @APIResponse(responseCode = "404", description = "Unidad no encontrada")
    @APIResponse(responseCode = "409", description = "Sílabo no modificable")
    public Response actualizar(
            @Parameter(description = "ID de la unidad") 
            @PathParam("id") Long id,
            @Valid SilaboUnidadRequestDTO dto) {
        String usuario = obtenerUsuarioActual();
        SilaboUnidadResponseDTO unidad = silaboUnidadService.actualizar(id, dto, usuario);
        return Response.ok(ApiResponse.success("Unidad actualizada exitosamente", unidad)).build();
    }

    @DELETE
    @Path("/unidades/{id}")
    @Operation(summary = "Eliminar unidad", 
               description = "Elimina (lógicamente) una unidad. El sílabo debe estar en estado modificable.")
    @APIResponse(responseCode = "200", description = "Unidad eliminada exitosamente")
    @APIResponse(responseCode = "404", description = "Unidad no encontrada")
    @APIResponse(responseCode = "409", description = "Sílabo no modificable")
    public Response eliminar(
            @Parameter(description = "ID de la unidad") 
            @PathParam("id") Long id) {
        silaboUnidadService.eliminar(id);
        return Response.ok(ApiResponse.success("Unidad eliminada exitosamente")).build();
    }

    @GET
    @Path("/{silaboId}/unidades")
    @Operation(summary = "Listar unidades de un sílabo", 
               description = "Obtiene todas las unidades de un sílabo ordenadas por número")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarPorSilabo(
            @Parameter(description = "ID del sílabo") 
            @PathParam("silaboId") Long silaboId) {
        List<SilaboUnidadResponseDTO> unidades = silaboUnidadService.listarPorSilabo(silaboId);
        return Response.ok(ApiResponse.success("Unidades del sílabo", unidades)).build();
    }

    @GET
    @Path("/{silaboId}/unidades/{numero}")
    @Operation(summary = "Buscar unidad por número", 
               description = "Obtiene una unidad específica de un sílabo por su número")
    @APIResponse(responseCode = "200", description = "Unidad encontrada")
    @APIResponse(responseCode = "404", description = "Unidad no encontrada")
    public Response buscarPorSilaboYNumero(
            @Parameter(description = "ID del sílabo") 
            @PathParam("silaboId") Long silaboId,
            @Parameter(description = "Número de la unidad") 
            @PathParam("numero") Integer numeroUnidad) {
        SilaboUnidadResponseDTO unidad = silaboUnidadService.buscarPorSilaboYNumero(silaboId, numeroUnidad);
        return Response.ok(ApiResponse.success("Unidad " + numeroUnidad + " del sílabo", unidad)).build();
    }

    @GET
    @Path("/{silaboId}/unidades/semana/{semana}")
    @Operation(summary = "Buscar unidades por semana", 
               description = "Obtiene las unidades que incluyen una semana específica")
    @APIResponse(responseCode = "200", description = "Unidades encontradas")
    public Response buscarPorSemana(
            @Parameter(description = "ID del sílabo") 
            @PathParam("silaboId") Long silaboId,
            @Parameter(description = "Número de semana (1-20)") 
            @PathParam("semana") Integer semana) {
        List<SilaboUnidadResponseDTO> unidades = silaboUnidadService.buscarPorSemana(silaboId, semana);
        return Response.ok(ApiResponse.success("Unidades de la semana " + semana, unidades)).build();
    }
}
