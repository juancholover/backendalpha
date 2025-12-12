package upeu.edu.pe.curriculum.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.curriculum.application.dto.SilaboCalidadDTO;
import upeu.edu.pe.curriculum.application.mapper.SilaboCalidadMapper;
import upeu.edu.pe.curriculum.domain.commands.EvaluarCalidadSilaboCommand;
import upeu.edu.pe.curriculum.domain.entities.SilaboCalidad;
import upeu.edu.pe.curriculum.domain.exceptions.CalidadInsuficienteException;
import upeu.edu.pe.curriculum.domain.exceptions.EvaluacionNoEncontradaException;
import upeu.edu.pe.curriculum.domain.services.SilaboCalidadService;
import upeu.edu.pe.curriculum.domain.usecases.EvaluarCalidadSilaboUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

/**
 * Controlador REST para la evaluación de calidad de sílabos
 */
@Path("/api/v1/silabos/{silaboId}/calidad")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Calidad de Sílabos", description = "Evaluación de calidad y aprobación de sílabos")
@Slf4j
public class SilaboCalidadController {

    @Inject
    EvaluarCalidadSilaboUseCase evaluarCalidadUseCase;

    @Inject
    SilaboCalidadService calidadService;

    @Inject
    SilaboCalidadMapper calidadMapper;

    @Context
    SecurityContext securityContext;

    /**
     * Evalúa la calidad de un sílabo
     */
    @POST
    @Path("/evaluar")
    @Operation(summary = "Evaluar calidad del sílabo", 
               description = "Realiza una evaluación completa de calidad del sílabo según 8 criterios. Requiere >= 80/100 para aprobación.")
    public Response evaluarCalidad(
            @PathParam("silaboId") Long silaboId,
            @QueryParam("forzar") @DefaultValue("false") Boolean forzarReevaluacion) {
        
        log.info("📊 Solicitud de evaluación de calidad para sílabo ID: {}", silaboId);

        try {
            String evaluador = securityContext.getUserPrincipal() != null 
                ? securityContext.getUserPrincipal().getName() 
                : "sistema";

            EvaluarCalidadSilaboCommand command = new EvaluarCalidadSilaboCommand(
                silaboId, 
                evaluador, 
                forzarReevaluacion
            );

            SilaboCalidad calidad = evaluarCalidadUseCase.execute(command);
            SilaboCalidadDTO dto = calidadMapper.toDTO(calidad);

            String mensaje = calidad.getAprobado() 
                ? "Evaluación completada: Sílabo aprobado (" + calidad.getPuntajeTotal() + "/100)"
                : "Evaluación completada: Requiere mejoras (" + calidad.getPuntajeTotal() + "/100)";

            return Response.ok(ApiResponse.success(mensaje, dto)).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error(e.getMessage(), null))
                .build();
        } catch (CalidadInsuficienteException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error(e.getMessage(), null))
                .build();
        }
    }

    /**
     * Obtiene la última evaluación de calidad
     */
    @GET
    @Path("/ultima")
    @Operation(summary = "Obtener última evaluación", 
               description = "Recupera la evaluación de calidad más reciente del sílabo")
    public Response obtenerUltimaEvaluacion(@PathParam("silaboId") Long silaboId) {
        log.info("📄 Consultando última evaluación para sílabo ID: {}", silaboId);

        try {
            SilaboCalidad calidad = calidadService.obtenerUltimaEvaluacion(silaboId);
            
            if (calidad == null) {
                throw new EvaluacionNoEncontradaException(silaboId);
            }

            SilaboCalidadDTO dto = calidadMapper.toDTO(calidad);
            return Response.ok(ApiResponse.success("Última evaluación encontrada", dto)).build();
            
        } catch (EvaluacionNoEncontradaException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error(e.getMessage(), null))
                .build();
        }
    }

    /**
     * Verifica si el sílabo cumple con el estándar de calidad
     */
    @GET
    @Path("/aprobado")
    @Operation(summary = "Verificar aprobación de calidad", 
               description = "Verifica si el sílabo cumple con el estándar mínimo de calidad (>= 80/100)")
    public Response verificarAprobacion(@PathParam("silaboId") Long silaboId) {
        log.info("✅ Verificando aprobación de calidad para sílabo ID: {}", silaboId);

        boolean aprobado = calidadService.cumpleEstandarCalidad(silaboId);
        AprobacionResponse response = new AprobacionResponse(silaboId, aprobado);
        
        return Response.ok(ApiResponse.success(response.mensaje, response)).build();
    }

    /**
     * Evalúa y requiere aprobación (lanza excepción si no cumple)
     */
    @POST
    @Path("/evaluar-requerida")
    @Operation(summary = "Evaluar con requerimiento de aprobación", 
               description = "Evalúa y lanza error HTTP 400 si no cumple el estándar >= 80/100")
    public Response evaluarConRequerimiento(@PathParam("silaboId") Long silaboId) {
        log.info("⚠️ Evaluación REQUERIDA para sílabo ID: {}", silaboId);

        try {
            String evaluador = securityContext.getUserPrincipal() != null 
                ? securityContext.getUserPrincipal().getName() 
                : "sistema";

            EvaluarCalidadSilaboCommand command = new EvaluarCalidadSilaboCommand(
                silaboId, 
                evaluador, 
                false
            );

            SilaboCalidad calidad = evaluarCalidadUseCase.executeOrThrow(command);
            SilaboCalidadDTO dto = calidadMapper.toDTO(calidad);

            return Response.ok(ApiResponse.success(
                "Evaluación aprobada exitosamente (" + calidad.getPuntajeTotal() + "/100)", 
                dto
            )).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error(e.getMessage(), null))
                .build();
        } catch (CalidadInsuficienteException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error(e.getMessage(), null))
                .build();
        }
    }

    /**
     * Clase interna para respuesta de aprobación
     */
    public static class AprobacionResponse {
        public Long silaboId;
        public Boolean aprobado;
        public String mensaje;

        public AprobacionResponse(Long silaboId, Boolean aprobado) {
            this.silaboId = silaboId;
            this.aprobado = aprobado;
            this.mensaje = aprobado 
                ? "El sílabo cumple con el estándar de calidad (>= 80/100)" 
                : "El sílabo NO cumple con el estándar de calidad mínimo (< 80/100)";
        }
    }
}
