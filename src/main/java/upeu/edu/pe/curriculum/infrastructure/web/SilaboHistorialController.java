package upeu.edu.pe.curriculum.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.curriculum.application.dto.SilaboHistorialDTO;
import upeu.edu.pe.curriculum.application.mapper.SilaboHistorialMapper;
import upeu.edu.pe.curriculum.domain.entities.SilaboHistorial;
import upeu.edu.pe.curriculum.domain.services.SilaboHistorialService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión del historial de cambios de sílabos.
 */
@Path("/api/v1/silabos/{silaboId}/historial")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sílabos - Historial", description = "Gestión del historial de cambios de sílabos")
@Slf4j
public class SilaboHistorialController {

    @Inject
    SilaboHistorialService historialService;

    @Inject
    SilaboHistorialMapper historialMapper;

    @Context
    SecurityContext securityContext;

    @GET
    @Operation(summary = "Obtener historial completo de un sílabo")
    @APIResponse(responseCode = "200", description = "Historial obtenido correctamente")
    public Response obtenerHistorial(
        @PathParam("silaboId") Long silaboId,
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("size") @DefaultValue("20") int size
    ) {
        List<SilaboHistorial> historial;
        
        if (page > 0 || size != 20) {
            historial = historialService.obtenerHistorialPaginado(silaboId, page, size);
        } else {
            historial = historialService.obtenerHistorial(silaboId);
        }
        
        List<SilaboHistorialDTO> response = historial.stream()
            .map(historialMapper::toDTO)
            .collect(Collectors.toList());
        
        return Response.ok(ApiResponse.success(
            "Historial obtenido correctamente", 
            response
        )).build();
    }

    @GET
    @Path("/accion/{accion}")
    @Operation(summary = "Obtener historial filtrado por tipo de acción")
    @APIResponse(responseCode = "200", description = "Historial filtrado correctamente")
    public Response obtenerHistorialPorAccion(
        @PathParam("silaboId") Long silaboId,
        @PathParam("accion") String accion
    ) {
        List<SilaboHistorial> historial = historialService.obtenerHistorialPorAccion(
            silaboId, 
            accion.toUpperCase()
        );
        
        List<SilaboHistorialDTO> response = historial.stream()
            .map(historialMapper::toDTO)
            .collect(Collectors.toList());
        
        return Response.ok(ApiResponse.success(
            "Historial filtrado por acción", 
            response
        )).build();
    }

    @GET
    @Path("/ultimo")
    @Operation(summary = "Obtener el último cambio registrado")
    @APIResponse(responseCode = "200", description = "Último cambio obtenido")
    public Response obtenerUltimoCambio(@PathParam("silaboId") Long silaboId) {
        SilaboHistorial ultimoCambio = historialService.obtenerUltimoCambio(silaboId);
        
        if (ultimoCambio == null) {
            return Response.ok(ApiResponse.success(
                "No hay cambios registrados", 
                null
            )).build();
        }
        
        SilaboHistorialDTO response = historialMapper.toDTO(ultimoCambio);
        
        return Response.ok(ApiResponse.success(
            "Último cambio obtenido", 
            response
        )).build();
    }

    @GET
    @Path("/estadisticas")
    @Operation(summary = "Obtener estadísticas del historial")
    @APIResponse(responseCode = "200", description = "Estadísticas obtenidas")
    public Response obtenerEstadisticas(@PathParam("silaboId") Long silaboId) {
        Map<String, Object> estadisticas = historialService.obtenerEstadisticas(silaboId);
        
        return Response.ok(ApiResponse.success(
            "Estadísticas del historial", 
            estadisticas
        )).build();
    }
}
