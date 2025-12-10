package upeu.edu.pe.shared.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.shared.entities.SistemaBackup;
import upeu.edu.pe.shared.services.BackupService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controller REST para gestión de backups del sistema.
 * RF232: Sistema de Backups y Logs
 */
@Path("/api/v1/sistema/backups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sistema - Backups", description = "Gestión de respaldos automáticos y manuales del sistema")
public class BackupController {

    @Inject
    BackupService backupService;

    @GET
    @Operation(summary = "Listar todos los backups", 
               description = "Obtiene el historial completo de backups del sistema")
    @APIResponse(responseCode = "200", description = "Lista de backups obtenida exitosamente")
    public Response listarBackups() {
        List<SistemaBackup> backups = backupService.listarBackups();
        return Response.ok(ApiResponse.success("Historial de backups obtenido", backups)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener detalle de backup", 
               description = "Obtiene los detalles de un backup específico")
    @APIResponse(responseCode = "200", description = "Backup encontrado")
    @APIResponse(responseCode = "404", description = "Backup no encontrado")
    public Response obtenerBackup(@PathParam("id") Long id) {
        SistemaBackup backup = backupService.obtenerBackup(id);
        if (backup == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error("Backup no encontrado"))
                .build();
        }
        return Response.ok(ApiResponse.success("Backup obtenido", backup)).build();
    }

    @GET
    @Path("/estadisticas")
    @Operation(summary = "Obtener estadísticas de backups", 
               description = "Obtiene métricas y estadísticas del sistema de backups (total, exitosos, errores, espacio usado)")
    @APIResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    public Response obtenerEstadisticas() {
        Object estadisticas = backupService.obtenerEstadisticas();
        return Response.ok(ApiResponse.success("Estadísticas de backups", estadisticas)).build();
    }

    @POST
    @Path("/ejecutar-automatico")
    @Operation(summary = "Ejecutar backup automático", 
               description = "Dispara manualmente un backup automático del sistema (horario nocturno 02:00)")
    @APIResponse(responseCode = "200", description = "Backup iniciado exitosamente")
    @APIResponse(responseCode = "500", description = "Error al iniciar backup")
    public Response ejecutarBackupAutomatico() {
        try {
            SistemaBackup backup = backupService.ejecutarBackupAutomatico();
            return Response.ok(ApiResponse.success(
                "Backup automático ejecutado. Estado: " + backup.getEstado(), 
                backup
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error("Error al ejecutar backup: " + e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/solicitar-manual")
    @Operation(summary = "Solicitar backup manual", 
               description = "Solicita un backup manual del sistema. Requiere permisos de administrador.")
    @APIResponse(responseCode = "200", description = "Backup manual iniciado exitosamente")
    @APIResponse(responseCode = "403", description = "Permisos insuficientes")
    @APIResponse(responseCode = "500", description = "Error al iniciar backup")
    public Response solicitarBackupManual(@Context SecurityContext securityContext) {
        try {
            // Obtener usuario autenticado
            String usuario = securityContext.getUserPrincipal() != null 
                ? securityContext.getUserPrincipal().getName() 
                : "admin_it";
            
            SistemaBackup backup = backupService.solicitarBackupManual(usuario);
            
            return Response.ok(ApiResponse.success(
                String.format("Backup manual solicitado por %s. Estado: %s", usuario, backup.getEstado()), 
                backup
            )).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error("Error al solicitar backup manual: " + e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/{id}/generar-ahora")
    @Operation(summary = "Generar backup ahora", 
               description = "Genera inmediatamente un nuevo backup (botón 'Generar Backup Ahora' de la UI)")
    @APIResponse(responseCode = "200", description = "Backup generado exitosamente")
    public Response generarBackupAhora(@Context SecurityContext securityContext) {
        String usuario = securityContext.getUserPrincipal() != null 
            ? securityContext.getUserPrincipal().getName() 
            : "admin";
        
        SistemaBackup backup = backupService.solicitarBackupManual(usuario);
        
        return Response.ok(ApiResponse.success(
            "Backup generado exitosamente", 
            backup
        )).build();
    }
}
