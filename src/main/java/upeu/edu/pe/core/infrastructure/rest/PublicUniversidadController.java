package upeu.edu.pe.core.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.core.application.dto.LandingConfigDTO;
import upeu.edu.pe.core.domain.services.LandingConfigService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST público para configuración de universidad.
 * NO requiere autenticación.
 */
@Path("/api/v1/public/universidad")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Public Config", description = "Configuración pública de la universidad (sin autenticación)")
public class PublicUniversidadController {

    @Inject
    LandingConfigService landingConfigService;

    /**
     * Obtener configuración completa de landing (público).
     */
    @GET
    @Path("/configuracion/landing")
    @Operation(summary = "Obtener configuración de landing page (público)")
    public Response getLandingConfig() {
        try {
            LandingConfigDTO config = landingConfigService.getDefaultLandingConfig();
            return Response.ok(ApiResponse.success("Configuración obtenida", config)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error obteniendo configuración", e.getMessage()))
                    .build();
        }
    }

    /**
     * Obtener solo header de landing (público).
     */
    @GET
    @Path("/configuracion/landing/header")
    @Operation(summary = "Obtener header de landing (público)")
    public Response getHeader() {
        try {
            LandingConfigDTO config = landingConfigService.getDefaultLandingConfig();
            return Response.ok(ApiResponse.success("Header obtenido", config.getHeader())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error obteniendo header", e.getMessage()))
                    .build();
        }
    }

    /**
     * Obtener solo hero de landing (público).
     */
    @GET
    @Path("/configuracion/landing/hero")
    @Operation(summary = "Obtener hero de landing (público)")
    public Response getHero() {
        try {
            LandingConfigDTO config = landingConfigService.getDefaultLandingConfig();
            return Response.ok(ApiResponse.success("Hero obtenido", config.getHero())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error obteniendo hero", e.getMessage()))
                    .build();
        }
    }

    /**
     * Obtener solo campus de landing (público).
     */
    @GET
    @Path("/configuracion/landing/campus")
    @Operation(summary = "Obtener campus de landing (público)")
    public Response getCampus() {
        try {
            LandingConfigDTO config = landingConfigService.getDefaultLandingConfig();
            return Response.ok(ApiResponse.success("Campus obtenido", config.getCampus())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error obteniendo campus", e.getMessage()))
                    .build();
        }
    }

    /**
     * Obtener solo footer de landing (público).
     */
    @GET
    @Path("/configuracion/landing/footer")
    @Operation(summary = "Obtener footer de landing (público)")
    public Response getFooter() {
        try {
            LandingConfigDTO config = landingConfigService.getDefaultLandingConfig();
            return Response.ok(ApiResponse.success("Footer obtenido", config.getFooter())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error obteniendo footer", e.getMessage()))
                    .build();
        }
    }

    /**
     * Obtener imágenes de configuración del sistema (fondos).
     */
    @GET
    @Path("/configuracion/imagenes")
    @Operation(summary = "Obtener imágenes de configuración del sistema")
    public Response getConfigImages() {
        try {
            // Leer configuración del sistema desde la BD
            var sistemaConfig = landingConfigService.getSistemaConfig();

            if (sistemaConfig != null && !sistemaConfig.isEmpty()) {
                // Convertir Map a lista de elementos
                var elementos = sistemaConfig.entrySet().stream()
                        .filter(entry -> entry.getValue() instanceof Map)
                        .map(entry -> {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> config = (Map<String, Object>) entry.getValue();
                            return Map.of(
                                    "id", config.getOrDefault("id", entry.getKey()),
                                    "url", config.getOrDefault("url", ""),
                                    "descripcion", config.getOrDefault("descripcion", "Elemento: " + entry.getKey()));
                        })
                        .toList();
                return Response.ok(ApiResponse.success("Imágenes de configuración", Map.of("elementos", elementos)))
                        .build();
            }

            // Fallback si no hay configuración
            var elementos = List.of(
                    Map.of(
                            "id", "pantalla_principal",
                            "url", "",
                            "descripcion", "Fondo del portal principal"),
                    Map.of(
                            "id", "log_url",
                            "url", "",
                            "descripcion", "Fondo de la pantalla de login"));
            return Response.ok(ApiResponse.success("Imágenes de configuración", Map.of("elementos", elementos)))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error obteniendo imágenes", e.getMessage()))
                    .build();
        }
    }

    /**
     * Obtener elemento específico de configuración.
     */
    @GET
    @Path("/configuracion/elemento/{elementoId}")
    @Operation(summary = "Obtener elemento de configuración específico")
    public Response getConfigElement(@PathParam("elementoId") String elementoId) {
        try {
            // Leer configuración del sistema desde la BD
            var sistemaConfig = landingConfigService.getSistemaConfig();

            if (sistemaConfig != null && sistemaConfig.containsKey(elementoId)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> elementoConfig = (Map<String, Object>) sistemaConfig.get(elementoId);
                Map<String, String> elemento = Map.of(
                        "id", String.valueOf(elementoConfig.getOrDefault("id", elementoId)),
                        "url", String.valueOf(elementoConfig.getOrDefault("url", "")),
                        "descripcion",
                        String.valueOf(elementoConfig.getOrDefault("descripcion", "Elemento: " + elementoId)));
                return Response.ok(ApiResponse.success("Elemento obtenido", elemento)).build();
            }

            // Fallback si no existe el elemento
            Map<String, String> elemento = Map.of(
                    "id", elementoId,
                    "url", "",
                    "descripcion", "Elemento de configuración: " + elementoId);
            return Response.ok(ApiResponse.success("Elemento obtenido", elemento)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error obteniendo elemento", e.getMessage()))
                    .build();
        }
    }
}
