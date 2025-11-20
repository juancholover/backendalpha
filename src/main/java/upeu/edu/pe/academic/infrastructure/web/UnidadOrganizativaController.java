package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
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
    UnidadOrganizativaService unidadOrganizativaService;

    @GET
    public ApiResponse<List<UnidadOrganizativaResponseDTO>> getAll() {
        return ApiResponse.success("Unidades organizativas obtenidas exitosamente", unidadOrganizativaService.findAll());
    }

    @GET
    @Path("/{id}")
    public ApiResponse<UnidadOrganizativaResponseDTO> getById(@PathParam("id") Long id) {
        return ApiResponse.success("Unidad organizativa obtenida exitosamente", unidadOrganizativaService.findById(id));
    }

    @GET
    @Path("/tipo/{tipoId}")
    public ApiResponse<List<UnidadOrganizativaResponseDTO>> getByTipo(@PathParam("tipoId") Long tipoId) {
        return ApiResponse.success("Unidades organizativas por tipo obtenidas exitosamente", unidadOrganizativaService.findByTipoDeUnidad(tipoId));
    }

    @GET
    @Path("/padre/{unidadPadreId}")
    public ApiResponse<List<UnidadOrganizativaResponseDTO>> getByUnidadPadre(@PathParam("unidadPadreId") Long unidadPadreId) {
        return ApiResponse.success("Unidades organizativas hijas obtenidas exitosamente", unidadOrganizativaService.findByUnidadPadre(unidadPadreId));
    }

    @POST
    public ApiResponse<UnidadOrganizativaResponseDTO> create(UnidadOrganizativaRequestDTO requestDTO) {
        return ApiResponse.success("Unidad organizativa creada exitosamente", unidadOrganizativaService.create(requestDTO));
    }

    @PUT
    @Path("/{id}")
    public ApiResponse<UnidadOrganizativaResponseDTO> update(@PathParam("id") Long id, UnidadOrganizativaRequestDTO requestDTO) {
        return ApiResponse.success("Unidad organizativa actualizada exitosamente", unidadOrganizativaService.update(id, requestDTO));
    }

    @DELETE
    @Path("/{id}")
    public ApiResponse<Void> delete(@PathParam("id") Long id) {
        unidadOrganizativaService.delete(id);
        return ApiResponse.success("Unidad organizativa eliminada exitosamente");
    }
}
