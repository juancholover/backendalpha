package upeu.edu.pe.curriculum.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.curriculum.application.dto.SilaboRequestDTO;
import upeu.edu.pe.curriculum.application.dto.SilaboResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.SilaboMapper;
import upeu.edu.pe.curriculum.domain.commands.ActualizarSilaboCommand;
import upeu.edu.pe.curriculum.domain.commands.CrearSilaboCommand;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.services.SilaboService;
import upeu.edu.pe.curriculum.domain.usecases.*;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de Sílabos.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/silabos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sílabos", description = "Gestión de sílabos oficiales con workflow de aprobación")
public class SilaboController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearSilaboUseCase crearUseCase;

    @Inject
    ActualizarSilaboUseCase actualizarUseCase;

    @Inject
    AprobarSilaboUseCase aprobarUseCase;

    @Inject
    RechazarSilaboUseCase rechazarUseCase;

    @Inject
    ActivarSilaboUseCase activarUseCase;

    @Inject
    EnviarSilaboRevisionUseCase enviarRevisionUseCase;

    @Inject
    EliminarSilaboUseCase eliminarUseCase;

    @Inject
    upeu.edu.pe.curriculum.application.usecases.GenerarPdfSilaboUseCase generarPdfUseCase;

    @Inject
    upeu.edu.pe.curriculum.application.usecases.GenerarDocxSilaboUseCase generarDocxUseCase;

    // Service para operaciones de lectura
    @Inject
    SilaboService silaboService;

    @Inject
    SilaboMapper silaboMapper;

    @Inject
    upeu.edu.pe.curriculum.domain.services.SilaboCalidadService calidadService;

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
    @Operation(summary = "Buscar sílabo por ID")
    public Response buscarPorId(@PathParam("id") Long id) {
        SilaboResponseDTO silabo = silaboService.buscarPorId(id);
        return Response.ok(ApiResponse.success("Sílabo encontrado", silabo)).build();
    }

    @GET
    @Path("/curso/{cursoId}/vigente")
    @Operation(summary = "Buscar sílabo vigente de un curso")
    public Response buscarVigentePorCurso(
            @PathParam("cursoId") Long cursoId,
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.buscarVigentePorCurso(cursoId, universidadId);
        return Response.ok(ApiResponse.success("Sílabo vigente del curso", silabos)).build();
    }

    @GET
    @Path("/curso/{cursoId}")
    @Operation(summary = "Listar todas las versiones de sílabo de un curso")
    public Response buscarPorCurso(
            @PathParam("cursoId") Long cursoId,
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.buscarPorCurso(cursoId, universidadId);
        return Response.ok(ApiResponse.success("Versiones del sílabo", silabos)).build();
    }

    @GET
    @Path("/anio/{anio}")
    @Operation(summary = "Listar sílabos por año académico")
    public Response listarPorAnio(
            @PathParam("anio") String anioAcademico,
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.listarPorAnio(anioAcademico, universidadId);
        return Response.ok(ApiResponse.success("Sílabos del año " + anioAcademico, silabos)).build();
    }

    @GET
    @Path("/estado/{estado}")
    @Operation(summary = "Listar sílabos por estado")
    public Response listarPorEstado(
            @PathParam("estado") String estado,
            @QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.listarPorEstado(estado, universidadId);
        return Response.ok(ApiResponse.success("Sílabos en estado " + estado, silabos)).build();
    }

    @GET
    @Path("/pendientes-aprobacion")
    @Operation(summary = "Listar sílabos pendientes de aprobación")
    public Response listarPendientesAprobacion(@QueryParam("universidadId") Long universidadId) {
        List<SilaboResponseDTO> silabos = silaboService.listarPendientesAprobacion(universidadId);
        return Response.ok(ApiResponse.success("Sílabos pendientes de aprobación", silabos)).build();
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Estadísticas de sílabos")
    public Response obtenerEstadisticas(@QueryParam("universidadId") Long universidadId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("BORRADOR", silaboService.contarPorEstado("BORRADOR", universidadId));
        stats.put("EN_REVISION", silaboService.contarPorEstado("EN_REVISION", universidadId));
        stats.put("APROBADO", silaboService.contarPorEstado("APROBADO", universidadId));
        stats.put("VIGENTE", silaboService.contarPorEstado("VIGENTE", universidadId));
        stats.put("OBSOLETO", silaboService.contarPorEstado("OBSOLETO", universidadId));
        return Response.ok(ApiResponse.success("Estadísticas de sílabos", stats)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear sílabo")
    @APIResponse(responseCode = "201", description = "Sílabo creado exitosamente")
    public Response crear(@Valid SilaboRequestDTO dto) {
        String usuario = obtenerUsuarioActual();

        CrearSilaboCommand command = new CrearSilaboCommand(
                dto.getCursoId(),
                dto.getAnioAcademico(),
                dto.getCompetencias(),
                dto.getSumilla(),
                dto.getBibliografia(),
                dto.getMetodologia(),
                dto.getRecursosDidacticos());

        Silabo silabo = crearUseCase.execute(command, usuario);
        SilaboResponseDTO response = silaboMapper.toResponseDTO(silabo);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Sílabo creado en estado BORRADOR", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar sílabo")
    public Response actualizar(@PathParam("id") Long id, @Valid SilaboRequestDTO dto) {
        String usuario = obtenerUsuarioActual();

        ActualizarSilaboCommand command = new ActualizarSilaboCommand(
                id,
                dto.getCompetencias(),
                dto.getSumilla(),
                dto.getBibliografia(),
                dto.getMetodologia(),
                dto.getRecursosDidacticos(),
                dto.getObservaciones());

        Silabo silabo = actualizarUseCase.execute(command, usuario);
        SilaboResponseDTO response = silaboMapper.toResponseDTO(silabo);

        return Response.ok(ApiResponse.success("Sílabo actualizado", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar sílabo")
    public Response eliminar(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Sílabo eliminado", null)).build();
    }

    // ==================== WORKFLOW ====================

    @POST
    @Path("/{id}/enviar-revision")
    @Operation(summary = "Enviar sílabo a revisión")
    public Response enviarARevision(@PathParam("id") Long id) {
        String usuario = obtenerUsuarioActual();
        Silabo silabo = enviarRevisionUseCase.execute(id, usuario);
        SilaboResponseDTO response = silaboMapper.toResponseDTO(silabo);
        return Response.ok(ApiResponse.success("Sílabo enviado a revisión", response)).build();
    }

    @POST
    @Path("/{id}/aprobar")
    @Operation(summary = "Aprobar sílabo")
    public Response aprobar(@PathParam("id") Long id, @QueryParam("observaciones") String observaciones) {
        String usuario = obtenerUsuarioActual();
        
        // Validar calidad antes de aprobar
        if (!calidadService.cumpleEstandarCalidad(id)) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("El sílabo no cumple con el estándar de calidad mínimo (>= 80/100). " +
                    "Debe evaluarse y aprobar la evaluación de calidad primero.", null))
                .build();
        }
        
        Silabo silabo = aprobarUseCase.execute(id, usuario, observaciones);
        SilaboResponseDTO response = silaboMapper.toResponseDTO(silabo);
        return Response.ok(ApiResponse.success("Sílabo aprobado", response)).build();
    }

    @POST
    @Path("/{id}/rechazar")
    @Operation(summary = "Rechazar sílabo")
    public Response rechazar(@PathParam("id") Long id, @QueryParam("motivo") String motivoRechazo) {
        String usuario = obtenerUsuarioActual();
        Silabo silabo = rechazarUseCase.execute(id, usuario, motivoRechazo);
        SilaboResponseDTO response = silaboMapper.toResponseDTO(silabo);
        return Response.ok(ApiResponse.success("Sílabo rechazado", response)).build();
    }

    @POST
    @Path("/{id}/activar")
    @Operation(summary = "Activar sílabo")
    public Response activar(@PathParam("id") Long id) {
        String usuario = obtenerUsuarioActual();
        Silabo silabo = activarUseCase.execute(id, usuario);
        SilaboResponseDTO response = silaboMapper.toResponseDTO(silabo);
        return Response.ok(ApiResponse.success("Sílabo activado", response)).build();
    }

    // ==================== DESCARGA DE DOCUMENTOS ====================

    @GET
    @Path("/{id}/descargar")
    @Produces({"application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"})
    @Operation(summary = "Descargar sílabo en PDF o DOCX")
    @APIResponse(responseCode = "200", description = "Documento generado correctamente")
    @APIResponse(responseCode = "400", description = "Formato inválido")
    @APIResponse(responseCode = "404", description = "Sílabo no encontrado")
    @APIResponse(responseCode = "500", description = "Error al generar documento")
    public Response descargar(
            @PathParam("id") Long id,
            @QueryParam("formato") @DefaultValue("pdf") String formato) {
        
        try {
            // Validar formato
            if (!formato.equalsIgnoreCase("pdf") && !formato.equalsIgnoreCase("docx")) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Formato inválido. Use 'pdf' o 'docx'", null))
                    .build();
            }

            byte[] documento;
            String contentType;
            String nombreArchivo;

            // Ejecutar Use Case según formato
            if (formato.equalsIgnoreCase("pdf")) {
                documento = generarPdfUseCase.ejecutar(id);
                contentType = "application/pdf";
                nombreArchivo = "silabo-" + id + ".pdf";
            } else {
                documento = generarDocxUseCase.ejecutar(id);
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                nombreArchivo = "silabo-" + id + ".docx";
            }

            // Retornar documento
            return Response.ok(documento)
                .type(contentType)
                .header("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"")
                .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error(e.getMessage(), null))
                .build();
        } catch (upeu.edu.pe.shared.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error(e.getMessage(), null))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error("Error al generar documento: " + e.getMessage(), null))
                .build();
        }
    }
}
