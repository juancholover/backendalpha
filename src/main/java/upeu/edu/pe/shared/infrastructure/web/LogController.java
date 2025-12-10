package upeu.edu.pe.shared.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.shared.entities.SistemaLog;
import upeu.edu.pe.shared.repositories.SistemaLogRepository;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de logs del sistema.
 * RF232: Sistema de Backups y Logs
 */
@Path("/api/v1/sistema/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sistema - Logs", description = "Gestión de eventos y errores del sistema")
public class LogController {

    @Inject
    SistemaLogRepository logRepository;

    @GET
    @Operation(summary = "Listar todos los logs", 
               description = "Obtiene el historial completo de logs del sistema")
    @APIResponse(responseCode = "200", description = "Lista de logs obtenida exitosamente")
    public Response listarLogs(
            @Parameter(description = "Filtrar por nivel (INFO, WARNING, ERROR, CRITICAL)")
            @QueryParam("nivel") String nivel,
            @Parameter(description = "Filtrar por módulo")
            @QueryParam("modulo") String modulo,
            @Parameter(description = "Filtrar por usuario")
            @QueryParam("usuario") String usuario,
            @Parameter(description = "Límite de resultados")
            @QueryParam("limit") @DefaultValue("100") int limit) {
        
        List<SistemaLog> logs;
        
        if (nivel != null) {
            logs = logRepository.findByNivel(nivel);
        } else if (modulo != null) {
            logs = logRepository.findByModulo(modulo);
        } else if (usuario != null) {
            logs = logRepository.findByUsuario(usuario);
        } else {
            logs = logRepository.findAllActive();
        }
        
        // Limitar resultados
        if (logs.size() > limit) {
            logs = logs.subList(0, limit);
        }
        
        return Response.ok(ApiResponse.success("Logs obtenidos", logs)).build();
    }

    @GET
    @Path("/recientes")
    @Operation(summary = "Obtener logs recientes", 
               description = "Obtiene los logs de las últimas 24 horas")
    @APIResponse(responseCode = "200", description = "Logs recientes obtenidos")
    public Response obtenerLogRecientes() {
        List<SistemaLog> logs = logRepository.findRecientes();
        return Response.ok(ApiResponse.success("Logs recientes (últimas 24h)", logs)).build();
    }

    @GET
    @Path("/errores")
    @Operation(summary = "Obtener logs de errores", 
               description = "Obtiene todos los logs de nivel ERROR y CRITICAL")
    @APIResponse(responseCode = "200", description = "Logs de errores obtenidos")
    public Response obtenerErrores() {
        List<SistemaLog> errores = logRepository.findErrores();
        return Response.ok(ApiResponse.success("Logs de errores", errores)).build();
    }

    @GET
    @Path("/criticos")
    @Operation(summary = "Obtener logs críticos", 
               description = "Obtiene todos los logs de nivel CRITICAL")
    @APIResponse(responseCode = "200", description = "Logs críticos obtenidos")
    public Response obtenerCriticos() {
        List<SistemaLog> criticos = logRepository.findCriticos();
        return Response.ok(ApiResponse.success("Logs críticos", criticos)).build();
    }

    @GET
    @Path("/warnings")
    @Operation(summary = "Obtener advertencias", 
               description = "Obtiene todos los logs de nivel WARNING")
    @APIResponse(responseCode = "200", description = "Advertencias obtenidas")
    public Response obtenerWarnings() {
        List<SistemaLog> warnings = logRepository.findWarnings();
        return Response.ok(ApiResponse.success("Advertencias", warnings)).build();
    }

    @GET
    @Path("/estadisticas")
    @Operation(summary = "Obtener estadísticas de logs", 
               description = "Obtiene contadores de logs por nivel (INFO, WARNING, ERROR, CRITICAL)")
    @APIResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    public Response obtenerEstadisticas() {
        List<Object[]> estadisticas = logRepository.getEstadisticasPorNivel();
        
        Map<String, Long> resultado = new HashMap<>();
        for (Object[] stat : estadisticas) {
            String nivel = (String) stat[0];
            Long count = (Long) stat[1];
            resultado.put(nivel, count);
        }
        
        // Agregar contadores específicos
        long erroresRecientes = logRepository.countErroresRecientes();
        resultado.put("errores_recientes_24h", erroresRecientes);
        
        return Response.ok(ApiResponse.success("Estadísticas de logs", resultado)).build();
    }

    @GET
    @Path("/buscar")
    @Operation(summary = "Buscar logs por texto", 
               description = "Busca logs que contengan un texto específico en el mensaje")
    @APIResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos")
    public Response buscarPorTexto(
            @Parameter(description = "Texto a buscar en mensajes", required = true)
            @QueryParam("q") String texto) {
        
        if (texto == null || texto.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Parámetro 'q' es requerido"))
                .build();
        }
        
        List<SistemaLog> logs = logRepository.searchByMensaje(texto);
        return Response.ok(ApiResponse.success(
            String.format("Resultados de búsqueda para '%s'", texto), 
            logs
        )).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener detalle de log", 
               description = "Obtiene los detalles de un log específico")
    @APIResponse(responseCode = "200", description = "Log encontrado")
    @APIResponse(responseCode = "404", description = "Log no encontrado")
    public Response obtenerLog(@PathParam("id") Long id) {
        SistemaLog log = logRepository.findById(id);
        if (log == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error("Log no encontrado"))
                .build();
        }
        return Response.ok(ApiResponse.success("Log obtenido", log)).build();
    }
}
