package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaRequestDTO;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaResponseDTO;
import upeu.edu.pe.academic.domain.services.UnidadOrganizativaService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/unidades-organizativas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UnidadOrganizativaController {

    @Inject
    UnidadOrganizativaService unidadService;

    @GET
    public ApiResponse<List<UnidadOrganizativaResponseDTO>> getAll() {
        return ApiResponse.success("Unidades organizativas obtenidas exitosamente", unidadService.findAll());
    }

    @GET
    @Path("/{id}")
    public ApiResponse<UnidadOrganizativaResponseDTO> getById(@PathParam("id") Long id) {
        return ApiResponse.success("Unidad organizativa obtenida exitosamente", unidadService.findById(id));
    }

    @GET
    @Path("/universidad/{universidadId}")
    public ApiResponse<List<UnidadOrganizativaResponseDTO>> getByUniversidad(@PathParam("universidadId") Long universidadId) {
        return ApiResponse.success("Unidades organizativas por universidad obtenidas exitosamente", 
                unidadService.findByUniversidad(universidadId));
    }

    @GET
    @Path("/tipo-unidad/{tipoUnidadId}")
    public ApiResponse<List<UnidadOrganizativaResponseDTO>> getByTipoUnidad(@PathParam("tipoUnidadId") Long tipoUnidadId) {
        return ApiResponse.success("Unidades organizativas por tipo obtenidas exitosamente", 
                unidadService.findByTipoUnidad(tipoUnidadId));
    }

    @GET
    @Path("/raiz/universidad/{universidadId}")
    public ApiResponse<List<UnidadOrganizativaResponseDTO>> getRootUnidades(@PathParam("universidadId") Long universidadId) {
        return ApiResponse.success("Unidades raíz obtenidas exitosamente", 
                unidadService.findRootUnidades(universidadId));
    }

    @GET
    @Path("/hijas/{unidadPadreId}")
    public ApiResponse<List<UnidadOrganizativaResponseDTO>> getByUnidadPadre(@PathParam("unidadPadreId") Long unidadPadreId) {
        return ApiResponse.success("Unidades hijas obtenidas exitosamente", 
                unidadService.findByUnidadPadre(unidadPadreId));
    }

    @POST
    public ApiResponse<UnidadOrganizativaResponseDTO> create(@Valid UnidadOrganizativaRequestDTO requestDTO) {
        return ApiResponse.success("Unidad organizativa creada exitosamente", unidadService.create(requestDTO));
    }

    @PUT
    @Path("/{id}")
    public ApiResponse<UnidadOrganizativaResponseDTO> update(@PathParam("id") Long id, @Valid UnidadOrganizativaRequestDTO requestDTO) {
        return ApiResponse.success("Unidad organizativa actualizada exitosamente", 
                unidadService.update(id, requestDTO));
    }

    @DELETE
    @Path("/{id}")
    public ApiResponse<Void> delete(@PathParam("id") Long id) {
        unidadService.delete(id);
        return ApiResponse.success("Unidad organizativa eliminada exitosamente", null);
    }
}
