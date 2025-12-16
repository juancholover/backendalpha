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

/**
 * Controlador REST para configuración de landing page (Admin).
 * Requiere autenticación.
 */
@Path("/api/v1/universidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Landing Page", description = "Configuración de landing page de universidades")
public class LandingConfigController {

    @Inject
    LandingConfigService landingConfigService;

    /**
     * Obtener configuración completa de landing.
     */
    @GET
    @Path("/{id}/configuracion/landing")
    @Operation(summary = "Obtener configuración de landing page")
    public Response getLandingConfig(@PathParam("id") Long universidadId) {
        try {
            LandingConfigDTO config = landingConfigService.getLandingConfig(universidadId);
            return Response.ok(ApiResponse.success("Configuración obtenida", config)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error("Error obteniendo configuración", e.getMessage()))
                    .build();
        }
    }

    /**
     * Actualizar configuración completa de landing.
     */
    @PUT
    @Path("/{id}/configuracion/landing")
    @Operation(summary = "Actualizar configuración completa de landing")
    public Response updateLandingConfig(
            @PathParam("id") Long universidadId,
            LandingConfigDTO config) {
        try {
            LandingConfigDTO updated = landingConfigService.updateLandingConfig(universidadId, config);
            return Response.ok(ApiResponse.success("Configuración actualizada", updated)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Error actualizando configuración", e.getMessage()))
                    .build();
        }
    }

    /**
     * Actualizar solo el header de landing.
     */
    @PUT
    @Path("/{id}/configuracion/landing/header")
    @Operation(summary = "Actualizar header de landing")
    public Response updateHeader(
            @PathParam("id") Long universidadId,
            LandingConfigDTO.HeaderConfig header) {
        try {
            LandingConfigDTO.HeaderConfig updated = landingConfigService.updateHeader(universidadId, header);
            return Response.ok(ApiResponse.success("Header actualizado", updated)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Error actualizando header", e.getMessage()))
                    .build();
        }
    }

    /**
     * Actualizar solo el hero de landing.
     */
    @PUT
    @Path("/{id}/configuracion/landing/hero")
    @Operation(summary = "Actualizar hero de landing")
    public Response updateHero(
            @PathParam("id") Long universidadId,
            LandingConfigDTO.HeroConfig hero) {
        try {
            LandingConfigDTO.HeroConfig updated = landingConfigService.updateHero(universidadId, hero);
            return Response.ok(ApiResponse.success("Hero actualizado", updated)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Error actualizando hero", e.getMessage()))
                    .build();
        }
    }

    /**
     * Actualizar solo el campus de landing.
     */
    @PUT
    @Path("/{id}/configuracion/landing/campus")
    @Operation(summary = "Actualizar campus de landing")
    public Response updateCampus(
            @PathParam("id") Long universidadId,
            LandingConfigDTO.CampusConfig campus) {
        try {
            LandingConfigDTO.CampusConfig updated = landingConfigService.updateCampus(universidadId, campus);
            return Response.ok(ApiResponse.success("Campus actualizado", updated)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Error actualizando campus", e.getMessage()))
                    .build();
        }
    }

    /**
     * Actualizar solo el footer de landing.
     */
    @PUT
    @Path("/{id}/configuracion/landing/footer")
    @Operation(summary = "Actualizar footer de landing")
    public Response updateFooter(
            @PathParam("id") Long universidadId,
            LandingConfigDTO.FooterConfig footer) {
        try {
            LandingConfigDTO.FooterConfig updated = landingConfigService.updateFooter(universidadId, footer);
            return Response.ok(ApiResponse.success("Footer actualizado", updated)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Error actualizando footer", e.getMessage()))
                    .build();
        }
    }
}
