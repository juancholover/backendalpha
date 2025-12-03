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
import upeu.edu.pe.academic.application.dto.SilaboRequestDTO;
import upeu.edu.pe.academic.application.dto.SilaboResponseDTO;
import upeu.edu.pe.academic.domain.commands.ActualizarSilaboCommand;
import upeu.edu.pe.academic.domain.services.SilaboService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de Sílabos
 * 
 * Endpoints principales:
 * - POST   /api/v1/silabos                              - Crear sílabo
 * - GET    /api/v1/silabos/{id}                         - Buscar por ID
 * - PUT    /api/v1/silabos/{id}                         - Actualizar sílabo
 * - DELETE /api/v1/silabos/{id}                         - Eliminar sílabo
 * 
 * Endpoints de workflow:
 * - POST   /api/v1/silabos/{id}/enviar-revision         - Enviar a revisión
 * - POST   /api/v1/silabos/{id}/aprobar                 - Aprobar sílabo
 * - POST   /api/v1/silabos/{id}/rechazar                - Rechazar sílabo
 * - POST   /api/v1/silabos/{id}/activar                 - Activar sílabo
 * - POST   /api/v1/silabos/{id}/aprobar-y-activar      - Aprobar y activar en una operación
 * 
 * Endpoints de búsqueda:
 * - GET    /api/v1/silabos/curso/{cursoId}/vigente      - Sílabo vigente de un curso
 * - GET    /api/v1/silabos/curso/{cursoId}              - Todas las versiones de un curso
 * - GET    /api/v1/silabos/curso/{cursoId}/anio/{anio}  - Sílabo de un curso en un año
 * - GET    /api/v1/silabos/anio/{anio}                  - Sílabos por año académico
 * - GET    /api/v1/silabos/estado/{estado}              - Sílabos por estado
 * - GET    /api/v1/silabos/pendientes-aprobacion        - Sílabos pendientes de aprobación
 * - GET    /api/v1/silabos/aprobados/anio/{anio}        - Sílabos aprobados por año
 * - GET    /api/v1/silabos/stats                        - Estadísticas de sílabos
 */
@Path("/api/v1/silabos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sílabos", description = "Gestión de sílabos oficiales de cursos con workflow de aprobación")
public class SilaboController {

    @Inject
    SilaboService silaboService;

    @Context
    SecurityContext securityContext;

    private String obtenerUsuarioActual() {
        if (securityContext != null && securityContext.getUserPrincipal() != null) {
            return securityContext.getUserPrincipal().getName();
        }
        return "SYSTEM";
    }

    @POST
    @Operation(summary = "Crear sílabo", description = "Crea un nuevo sílabo en estado BORRADOR")
    @APIResponse(responseCode = "201", description = "Sílabo creado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos")
    @APIResponse(responseCode = "409", description = "Ya existe un sílabo para ese curso y año")
    public Response crear(@Valid SilaboRequestDTO dto) {
        String usuario = obtenerUsuarioActual();
        SilaboResponseDTO silabo = silaboService.crear(dto, usuario);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Sílabo creado exitosamente en estado BORRADOR", silabo))
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar sílabo por ID", description = "Obtiene un sílabo por su ID con todas sus unidades y actividades")
    @APIResponse(responseCode = "200", description = "Sílabo encontrado")
    @APIResponse(responseCode = "404", description = "Sílabo no encontrado")
    public Response buscarPorId(
            @Parameter(description = "ID del sílabo") 
            @PathParam("id") Long id) {
        SilaboResponseDTO silabo = silaboService.buscarPorId(id);
        return Response.ok(ApiResponse.success("Sílabo encontrado", silabo)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar sílabo", description = "Actualiza un sílabo en estado modificable (BORRADOR o EN_REVISION)")
    @APIResponse(responseCode = "200", description = "Sílabo actualizado exitosamente")
    @APIResponse(responseCode = "404", description = "Sílabo no encontrado")
    @APIResponse(responseCode = "409", description = "Sílabo no es modificable")
    public Response actualizar(
            @Parameter(description = "ID del sílabo") 
            @PathParam("id") Long id,
            @Valid SilaboRequestDTO dto) {
        String usuario = obtenerUsuarioActual();
        
        ActualizarSilaboCommand command = new ActualizarSilaboCommand(
            id,
            dto.getCompetencias(),
            dto.getSumilla(),
            dto.getBibliografia(),
            dto.getMetodologia(),
            dto.getRecursosDidacticos(),
            dto.getObservaciones()
        );
        
        SilaboResponseDTO silabo = silaboService.actualizar(id, command, usuario);
        return Response.ok(ApiResponse.success("Sílabo actualizado exitosamente", silabo)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar sílabo", description = "Elimina (lógicamente) un sílabo en estado BORRADOR")
    @APIResponse(responseCode = "200", description = "Sílabo eliminado exitosamente")
    @APIResponse(responseCode = "404", description = "Sílabo no encontrado")
    @APIResponse(responseCode = "409", description = "Solo se pueden eliminar sílabos en BORRADOR")
    public Response eliminar(
            @Parameter(description = "ID del sílabo") 
            @PathParam("id") Long id) {
        silaboService.eliminar(id);
        return Response.ok(ApiResponse.success("Sílabo eliminado exitosamente")).build();
    }

    // ==================== ENDPOINTS DE WORKFLOW ====================

    @POST
    @Path("/{id}/enviar-revision")
    @Operation(summary = "Enviar sílabo a revisión", 
               description = "Cambia estado de BORRADOR a EN_REVISION. Valida que esté completo antes de enviar.")
    @APIResponse(responseCode = "200", description = "Sílabo enviado a revisión exitosamente")
    @APIResponse(responseCode = "400", description = "Sílabo incompleto o ya está en revisión")
    public Response enviarARevision(
            @Parameter(description = "ID del sílabo") 
            @PathParam("id") Long id) {
        String usuario = obtenerUsuarioActual();
        SilaboResponseDTO silabo = silaboService.enviarARevision(id, usuario);
        return Response.ok(ApiResponse.success("Sílabo enviado a revisión exitosamente", silabo)).build();
    }

    @POST
    @Path("/{id}/aprobar")
    @Operation(summary = "Aprobar sílabo", 
               description = "Cambia estado de EN_REVISION a APROBADO")
    @APIResponse(responseCode = "200", description = "Sílabo aprobado exitosamente")
    @APIResponse(responseCode = "400", description = "Sílabo no está en revisión")
    public Response aprobar(
            @Parameter(description = "ID del sílabo") 
            @PathParam("id") Long id,
            @Parameter(description = "Observaciones del aprobador (opcional)") 
            @QueryParam("observaciones") String observaciones) {
        String usuario = obtenerUsuarioActual();
        SilaboResponseDTO silabo = silaboService.aprobar(id, usuario, observaciones);
        return Response.ok(ApiResponse.success("Sílabo aprobado exitosamente", silabo)).build();
    }

    @POST
    @Path("/{id}/rechazar")
    @Operation(summary = "Rechazar sílabo", 
               description = "Cambia estado de EN_REVISION a BORRADOR. Requiere motivo de rechazo.")
    @APIResponse(responseCode = "200", description = "Sílabo rechazado exitosamente")
    @APIResponse(responseCode = "400", description = "Sílabo no está en revisión o falta motivo")
    public Response rechazar(
            @Parameter(description = "ID del sílabo") 
            @PathParam("id") Long id,
            @Parameter(description = "Motivo del rechazo (requerido)", required = true) 
            @QueryParam("motivo") String motivoRechazo) {
        String usuario = obtenerUsuarioActual();
        SilaboResponseDTO silabo = silaboService.rechazar(id, usuario, motivoRechazo);
        return Response.ok(ApiResponse.success("Sílabo rechazado. Regresó a estado BORRADOR", silabo)).build();
    }

    @POST
    @Path("/{id}/activar")
    @Operation(summary = "Activar sílabo", 
               description = "Cambia estado de APROBADO a VIGENTE. Marca como OBSOLETO otros sílabos vigentes del mismo curso.")
    @APIResponse(responseCode = "200", description = "Sílabo activado exitosamente")
    @APIResponse(responseCode = "400", description = "Sílabo no está aprobado")
    public Response activar(
            @Parameter(description = "ID del sílabo") 
            @PathParam("id") Long id) {
        String usuario = obtenerUsuarioActual();
        SilaboResponseDTO silabo = silaboService.activar(id, usuario);
        return Response.ok(ApiResponse.success("Sílabo activado como versión vigente", silabo)).build();
    }

    @POST
    @Path("/{id}/aprobar-y-activar")
    @Operation(summary = "Aprobar y activar sílabo", 
               description = "Proceso completo: EN_REVISION → APROBADO → VIGENTE en una sola transacción")
    @APIResponse(responseCode = "200", description = "Sílabo aprobado y activado exitosamente")
    @APIResponse(responseCode = "400", description = "Sílabo no está en revisión")
    public Response aprobarYActivar(
            @Parameter(description = "ID del sílabo") 
            @PathParam("id") Long id,
            @Parameter(description = "Observaciones del aprobador (opcional)") 
            @QueryParam("observaciones") String observaciones) {
        String usuario = obtenerUsuarioActual();
        SilaboResponseDTO silabo = silaboService.aprobarYActivar(id, usuario, observaciones);
        return Response.ok(ApiResponse.success("Sílabo aprobado y activado exitosamente", silabo)).build();
    }

    // ==================== ENDPOINTS DE BÚSQUEDA ====================

    @GET
    @Path("/curso/{cursoId}/vigente")
    @Operation(summary = "Buscar sílabo vigente de un curso", 
               description = "Obtiene el sílabo actualmente vigente de un curso")
    @APIResponse(responseCode = "200", description = "Sílabo vigente encontrado")
    @APIResponse(responseCode = "404", description = "No hay sílabo vigente para este curso")
    public Response buscarVigentePorCurso(
            @Parameter(description = "ID del curso") 
            @PathParam("cursoId") Long cursoId,
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.buscarVigentePorCurso(cursoId, universidadId);
        return Response.ok(ApiResponse.success("Sílabo vigente del curso", silabos)).build();
    }

    @GET
    @Path("/curso/{cursoId}")
    @Operation(summary = "Listar todas las versiones de sílabo de un curso", 
               description = "Obtiene todas las versiones históricas de sílabo de un curso")
    @APIResponse(responseCode = "200", description = "Versiones obtenidas exitosamente")
    public Response buscarPorCurso(
            @Parameter(description = "ID del curso") 
            @PathParam("cursoId") Long cursoId,
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.buscarPorCurso(cursoId, universidadId);
        return Response.ok(ApiResponse.success("Versiones del sílabo del curso", silabos)).build();
    }

    @GET
    @Path("/curso/{cursoId}/anio/{anio}")
    @Operation(summary = "Buscar sílabo de un curso en un año específico", 
               description = "Obtiene el sílabo de un curso para un año académico específico")
    @APIResponse(responseCode = "200", description = "Sílabo encontrado")
    @APIResponse(responseCode = "404", description = "No existe sílabo para ese curso y año")
    public Response buscarPorCursoYAnio(
            @Parameter(description = "ID del curso") 
            @PathParam("cursoId") Long cursoId,
            @Parameter(description = "Año académico (ej: 2025)") 
            @PathParam("anio") String anioAcademico,
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        SilaboResponseDTO silabo = silaboService.buscarPorCursoYAnio(cursoId, anioAcademico, universidadId);
        return Response.ok(ApiResponse.success("Sílabo del curso para el año " + anioAcademico, silabo)).build();
    }

    @GET
    @Path("/anio/{anio}")
    @Operation(summary = "Listar sílabos por año académico", 
               description = "Obtiene todos los sílabos de un año académico")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarPorAnio(
            @Parameter(description = "Año académico (ej: 2025)") 
            @PathParam("anio") String anioAcademico,
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.listarPorAnio(anioAcademico, universidadId);
        return Response.ok(ApiResponse.success("Sílabos del año " + anioAcademico, silabos)).build();
    }

    @GET
    @Path("/estado/{estado}")
    @Operation(summary = "Listar sílabos por estado", 
               description = "Filtra sílabos por estado (BORRADOR, EN_REVISION, APROBADO, VIGENTE, OBSOLETO)")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarPorEstado(
            @Parameter(description = "Estado del sílabo") 
            @PathParam("estado") String estado,
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.listarPorEstado(estado, universidadId);
        return Response.ok(ApiResponse.success("Sílabos en estado " + estado, silabos)).build();
    }

    @GET
    @Path("/pendientes-aprobacion")
    @Operation(summary = "Listar sílabos pendientes de aprobación", 
               description = "Obtiene todos los sílabos en estado EN_REVISION")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarPendientesAprobacion(
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.listarPendientesAprobacion(universidadId);
        return Response.ok(ApiResponse.success("Sílabos pendientes de aprobación", silabos)).build();
    }

    @GET
    @Path("/aprobados/anio/{anio}")
    @Operation(summary = "Listar sílabos aprobados por año", 
               description = "Obtiene sílabos aprobados o vigentes de un año académico")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response listarAprobadosPorAnio(
            @Parameter(description = "Año académico") 
            @PathParam("anio") String anioAcademico,
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.listarAprobadosPorAnio(anioAcademico, universidadId);
        return Response.ok(ApiResponse.success("Sílabos aprobados del año " + anioAcademico, silabos)).build();
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Estadísticas de sílabos", 
               description = "Obtiene contadores de sílabos por estado")
    @APIResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    public Response obtenerEstadisticas(
            @Parameter(description = "ID de la universidad") 
            @QueryParam("universidadId") Long universidadId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("BORRADOR", silaboService.contarPorEstado("BORRADOR", universidadId));
        stats.put("EN_REVISION", silaboService.contarPorEstado("EN_REVISION", universidadId));
        stats.put("APROBADO", silaboService.contarPorEstado("APROBADO", universidadId));
        stats.put("VIGENTE", silaboService.contarPorEstado("VIGENTE", universidadId));
        stats.put("OBSOLETO", silaboService.contarPorEstado("OBSOLETO", universidadId));
        
        return Response.ok(ApiResponse.success("Estadísticas de sílabos", stats)).build();
    }
}
