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
import upeu.edu.pe.curriculum.application.dto.SilaboUnidadRequestDTO;
import upeu.edu.pe.curriculum.application.dto.SilaboUnidadResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.SilaboUnidadMapper;
import upeu.edu.pe.curriculum.domain.commands.AgregarUnidadSilaboCommand;
import upeu.edu.pe.curriculum.domain.entities.SilaboUnidad;
import upeu.edu.pe.curriculum.domain.services.SilaboUnidadService;
import upeu.edu.pe.curriculum.domain.usecases.AgregarUnidadSilaboUseCase;
import upeu.edu.pe.curriculum.domain.usecases.EliminarUnidadSilaboUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de Unidades de Sílabo.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/silabos-unidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Unidades de Sílabo", description = "Gestión de unidades didácticas dentro de sílabos")
public class SilaboUnidadController {

    // Use Cases para operaciones de escritura
    @Inject
    AgregarUnidadSilaboUseCase agregarUnidadUseCase;

    @Inject
    EliminarUnidadSilaboUseCase eliminarUnidadUseCase;

    // Service para operaciones de lectura
    @Inject
    SilaboUnidadService silaboUnidadService;

    @Inject
    SilaboUnidadMapper silaboUnidadMapper;

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
    @Operation(summary = "Buscar unidad por ID")
    public Response buscarPorId(@PathParam("id") Long id) {
        SilaboUnidadResponseDTO unidad = silaboUnidadService.buscarPorId(id);
        return Response.ok(ApiResponse.success("Unidad encontrada", unidad)).build();
    }

    @GET
    @Path("/silabo/{silaboId}")
    @Operation(summary = "Listar unidades de un sílabo")
    public Response listarPorSilabo(@PathParam("silaboId") Long silaboId) {
        List<SilaboUnidadResponseDTO> unidades = silaboUnidadService.listarPorSilabo(silaboId);
        return Response.ok(ApiResponse.success("Unidades del sílabo", unidades)).build();
    }

    @GET
    @Path("/silabo/{silaboId}/numero/{numero}")
    @Operation(summary = "Buscar unidad por número")
    public Response buscarPorSilaboYNumero(
            @PathParam("silaboId") Long silaboId,
            @PathParam("numero") Integer numeroUnidad) {
        SilaboUnidadResponseDTO unidad = silaboUnidadService.buscarPorSilaboYNumero(silaboId, numeroUnidad);
        return Response.ok(ApiResponse.success("Unidad " + numeroUnidad, unidad)).build();
    }

    @GET
    @Path("/silabo/{silaboId}/semana/{semana}")
    @Operation(summary = "Buscar unidades por semana")
    public Response buscarPorSemana(
            @PathParam("silaboId") Long silaboId,
            @PathParam("semana") Integer semana) {
        List<SilaboUnidadResponseDTO> unidades = silaboUnidadService.buscarPorSemana(silaboId, semana);
        return Response.ok(ApiResponse.success("Unidades de la semana " + semana, unidades)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Path("/unidades")
    @Operation(summary = "Agregar unidad a sílabo")
    @APIResponse(responseCode = "201", description = "Unidad agregada exitosamente")
    public Response agregar(@Valid SilaboUnidadRequestDTO dto) {
        String usuario = obtenerUsuarioActual();

        AgregarUnidadSilaboCommand command = new AgregarUnidadSilaboCommand(
                dto.getSilaboId(),
                dto.getNumeroUnidad(),
                dto.getTitulo(),
                dto.getSemanaInicio(),
                dto.getSemanaFin(),
                dto.getContenidos(),
                dto.getLogroAprendizaje());

        SilaboUnidad unidad = agregarUnidadUseCase.execute(command, usuario);
        SilaboUnidadResponseDTO response = silaboUnidadMapper.toResponseDTO(unidad);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Unidad agregada", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar unidad")
    public Response eliminar(@PathParam("id") Long id) {
        eliminarUnidadUseCase.execute(id);
        return Response.ok(ApiResponse.success("Unidad eliminada", null)).build();
    }
}
