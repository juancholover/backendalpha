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
import upeu.edu.pe.curriculum.application.dto.*;
import upeu.edu.pe.curriculum.application.mapper.SilaboPlantillaMapper;
import upeu.edu.pe.curriculum.application.mapper.SilaboPublicacionMapper;
import upeu.edu.pe.curriculum.domain.entities.SilaboPlantilla;
import upeu.edu.pe.curriculum.domain.entities.SilaboPublicacion;
import upeu.edu.pe.curriculum.domain.repositories.SilaboPlantillaRepository;
import upeu.edu.pe.curriculum.domain.repositories.SilaboPublicacionRepository;
import upeu.edu.pe.curriculum.domain.usecases.*;
import upeu.edu.pe.curriculum.infrastructure.RequirePlantillaPermission;
import upeu.edu.pe.shared.exceptions.NotFoundException;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de Plantillas Multi-Campus.
 * 
 * Permite:
 * 1. Crear plantillas desde sílabos aprobados
 * 2. Publicar plantillas en múltiples campus
 * 3. Gestionar adaptaciones de campus
 */
@Path("/api/v1/plantillas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Plantillas Multi-Campus", description = "Gestión de plantillas de sílabos para publicación masiva")
public class SilaboPlantillaController {

    // Use Cases
    @Inject
    CrearPlantillaSilaboUseCase crearPlantillaUseCase;

    @Inject
    PublicarPlantillaEnCampusUseCase publicarPlantillaUseCase;

    @Inject
    AdaptarSilaboCampusUseCase adaptarSilaboCampusUseCase;

    // Repositories (para lectura)
    @Inject
    SilaboPlantillaRepository plantillaRepository;

    @Inject
    SilaboPublicacionRepository publicacionRepository;

    // Mappers
    @Inject
    SilaboPlantillaMapper plantillaMapper;

    @Inject
    SilaboPublicacionMapper publicacionMapper;

    @Context
    SecurityContext securityContext;

    /**
     * POST /api/v1/plantillas
     * Crea una plantilla desde un sílabo aprobado.
     * Solo rol sede_central.
     */
    @POST
    @RequirePlantillaPermission("plantilla:create")
    @Operation(summary = "Crear plantilla desde sílabo aprobado")
    @APIResponse(responseCode = "201", description = "Plantilla creada exitosamente")
    @APIResponse(responseCode = "400", description = "Sílabo no está aprobado o datos inválidos")
    @APIResponse(responseCode = "403", description = "Sin permisos (requiere rol sede_central)")
    public Response crearPlantilla(@Valid SilaboPlantillaRequestDTO request) {
        String usuario = obtenerUsuarioActual();

        SilaboPlantilla plantilla = crearPlantillaUseCase.execute(
            request.getSilaboId(),
            usuario,
            request.getFechaInicioVigencia(),
            request.getFechaFinVigencia(),
            request.getNivelFlexibilidad(),
            request.getNotas()
        );

        SilaboPlantillaResponseDTO response = plantillaMapper.toResponseDTO(plantilla);
        
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Plantilla creada exitosamente", response))
                .build();
    }

    /**
     * GET /api/v1/plantillas
     * Lista todas las plantillas activas.
     */
    @GET
    @RequirePlantillaPermission("plantilla:read")
    @Operation(summary = "Listar todas las plantillas activas")
    @APIResponse(responseCode = "200", description = "Lista de plantillas")
    public Response listarPlantillas(@QueryParam("estado") String estado) {
        List<SilaboPlantilla> plantillas;
        
        if (estado != null && !estado.isBlank()) {
            plantillas = plantillaRepository.findByEstado(estado);
        } else {
            plantillas = plantillaRepository.findAllActivas();
        }

        List<SilaboPlantillaResponseDTO> response = plantillas.stream()
                .map(plantillaMapper::toResponseDTO)
                .collect(Collectors.toList());

        return Response.ok(ApiResponse.success("Plantillas obtenidas exitosamente", response)).build();
    }

    /**
     * GET /api/v1/plantillas/{id}
     * Obtiene una plantilla por ID.
     */
    @GET
    @Path("/{id}")
    @RequirePlantillaPermission("plantilla:read")
    @Operation(summary = "Obtener plantilla por ID")
    @APIResponse(responseCode = "200", description = "Plantilla encontrada")
    @APIResponse(responseCode = "404", description = "Plantilla no encontrada")
    public Response obtenerPlantilla(@PathParam("id") Long id) {
        SilaboPlantilla plantilla = plantillaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Plantilla no encontrada con ID: " + id));

        SilaboPlantillaResponseDTO response = plantillaMapper.toResponseDTO(plantilla);

        return Response.ok(ApiResponse.success("Plantilla encontrada", response)).build();
    }

    /**
     * POST /api/v1/plantillas/{id}/publicar
     * Publica una plantilla en múltiples campus.
     * Solo rol sede_central.
     */
    @POST
    @Path("/{id}/publicar")
    @RequirePlantillaPermission("plantilla:publicar_campus")
    @Operation(summary = "Publicar plantilla en múltiples campus")
    @APIResponse(responseCode = "200", description = "Plantilla publicada exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos o plantilla no activa")
    @APIResponse(responseCode = "403", description = "Sin permisos (requiere rol sede_central)")
    @APIResponse(responseCode = "404", description = "Plantilla no encontrada")
    public Response publicarPlantilla(
            @PathParam("id") Long plantillaId,
            @Valid PublicarPlantillaRequestDTO request) {
        
        String usuario = obtenerUsuarioActual();

        List<SilaboPublicacion> publicaciones = publicarPlantillaUseCase.execute(
            plantillaId,
            request.getLocalizacionesIds(),
            request.getAnioAcademico(),
            usuario,
            request.getFechaInicioCampus(),
            request.getFechaFinCampus()
        );

        List<SilaboPublicacionResponseDTO> response = publicaciones.stream()
                .map(publicacionMapper::toResponseDTO)
                .collect(Collectors.toList());

        return Response.ok(ApiResponse.success(
                "Plantilla publicada en " + publicaciones.size() + " campus exitosamente", response)).build();
    }

    /**
     * GET /api/v1/plantillas/{id}/publicaciones
     * Lista todas las publicaciones de una plantilla.
     */
    @GET
    @Path("/{id}/publicaciones")
    @RequirePlantillaPermission("publicacion:read")
    @Operation(summary = "Listar publicaciones de una plantilla")
    @APIResponse(responseCode = "200", description = "Lista de publicaciones")
    @APIResponse(responseCode = "404", description = "Plantilla no encontrada")
    public Response listarPublicaciones(@PathParam("id") Long plantillaId) {
        // Verificar que la plantilla existe
        plantillaRepository.findByIdOptional(plantillaId)
                .orElseThrow(() -> new NotFoundException("Plantilla no encontrada con ID: " + plantillaId));

        List<SilaboPublicacion> publicaciones = publicacionRepository.findByPlantilla(plantillaId);

        List<SilaboPublicacionResponseDTO> response = publicaciones.stream()
                .map(publicacionMapper::toResponseDTO)
                .collect(Collectors.toList());

        return Response.ok(ApiResponse.success("Publicaciones obtenidas exitosamente", response)).build();
    }

    /**
     * PUT /api/v1/plantillas/publicaciones/{id}/adaptar
     * Marca una publicación como adaptada por el campus.
     * Solo rol campus_admin.
     */
    @PUT
    @Path("/publicaciones/{id}/adaptar")
    @RequirePlantillaPermission("publicacion:adaptar")
    @Operation(summary = "Marcar publicación como adaptada por campus")
    @APIResponse(responseCode = "200", description = "Publicación adaptada exitosamente")
    @APIResponse(responseCode = "400", description = "Publicación no está activa")
    @APIResponse(responseCode = "403", description = "Sin permisos (requiere rol campus_admin)")
    @APIResponse(responseCode = "404", description = "Publicación no encontrada")
    public Response adaptarPublicacion(
            @PathParam("id") Long publicacionId,
            @QueryParam("notas") String notas) {
        
        String usuario = obtenerUsuarioActual();

        SilaboPublicacion publicacion = adaptarSilaboCampusUseCase.execute(
            publicacionId,
            usuario,
            notas
        );

        SilaboPublicacionResponseDTO response = publicacionMapper.toResponseDTO(publicacion);

        return Response.ok(ApiResponse.success("Publicación adaptada exitosamente", response)).build();
    }

    /**
     * GET /api/v1/plantillas/publicaciones/campus/{campusId}
     * Lista publicaciones de un campus específico.
     */
    @GET
    @Path("/publicaciones/campus/{campusId}")
    @RequirePlantillaPermission("publicacion:read")
    @Operation(summary = "Listar publicaciones de un campus")
    @APIResponse(responseCode = "200", description = "Lista de publicaciones del campus")
    public Response listarPublicacionesPorCampus(
            @PathParam("campusId") Long campusId,
            @QueryParam("estado") String estado) {
        
        List<SilaboPublicacion> publicaciones;
        
        if ("ACTIVA".equalsIgnoreCase(estado)) {
            publicaciones = publicacionRepository.findActivasByLocalizacion(campusId);
        } else {
            publicaciones = publicacionRepository.findByLocalizacion(campusId);
        }

        List<SilaboPublicacionResponseDTO> response = publicaciones.stream()
                .map(publicacionMapper::toResponseDTO)
                .collect(Collectors.toList());

        return Response.ok(ApiResponse.success("Publicaciones del campus obtenidas exitosamente", response)).build();
    }

    /**
     * GET /api/v1/plantillas/publicaciones/pendientes-adaptacion/{campusId}
     * Lista publicaciones pendientes de adaptación en un campus.
     */
    @GET
    @Path("/publicaciones/pendientes-adaptacion/{campusId}")
    @RequirePlantillaPermission("publicacion:read")
    @Operation(summary = "Listar publicaciones pendientes de adaptación")
    @APIResponse(responseCode = "200", description = "Lista de publicaciones pendientes")
    public Response listarPendientesAdaptacion(@PathParam("campusId") Long campusId) {
        List<SilaboPublicacion> publicaciones = publicacionRepository.findPendientesAdaptacion(campusId);

        List<SilaboPublicacionResponseDTO> response = publicaciones.stream()
                .map(publicacionMapper::toResponseDTO)
                .collect(Collectors.toList());

        return Response.ok(ApiResponse.success(
                publicaciones.size() + " publicaciones pendientes de adaptación", response)).build();
    }

    /**
     * Obtiene el usuario actual desde el SecurityContext.
     * En producción, esto vendría del token JWT.
     */
    private String obtenerUsuarioActual() {
        if (securityContext != null && securityContext.getUserPrincipal() != null) {
            return securityContext.getUserPrincipal().getName();
        }
        return "sistema"; // Default para desarrollo/testing
    }
}
