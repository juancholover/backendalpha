package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import upeu.edu.pe.academic.application.dto.LocalizacionRequestDTO;
import upeu.edu.pe.academic.application.dto.LocalizacionResponseDTO;
import upeu.edu.pe.academic.domain.services.LocalizacionService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/localizaciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocalizacionController {

    @Inject
    LocalizacionService localizacionService;

    @GET
    public ApiResponse<List<LocalizacionResponseDTO>> getAll() {
        return ApiResponse.success("Localizaciones obtenidas exitosamente", localizacionService.findAll());
    }

    @GET
    @Path("/{id}")
    public ApiResponse<LocalizacionResponseDTO> getById(@PathParam("id") Long id) {
        return ApiResponse.success("Localización obtenida exitosamente", localizacionService.findById(id));
    }

    @GET
    @Path("/tipo/{tipoId}")
    public ApiResponse<List<LocalizacionResponseDTO>> getByTipoLocalizacion(@PathParam("tipoId") Long tipoId) {
        return ApiResponse.success("Localizaciones por tipo obtenidas exitosamente", localizacionService.findByTipoLocalizacion(tipoId));
    }

    @POST
    public ApiResponse<LocalizacionResponseDTO> create(@Valid LocalizacionRequestDTO requestDTO) {
        return ApiResponse.success("Localización creada exitosamente", localizacionService.create(requestDTO));
    }

    @PUT
    @Path("/{id}")
    public ApiResponse<LocalizacionResponseDTO> update(@PathParam("id") Long id, @Valid LocalizacionRequestDTO requestDTO) {
        return ApiResponse.success("Localización actualizada exitosamente", localizacionService.update(id, requestDTO));
    }

    @DELETE
    @Path("/{id}")
    public ApiResponse<Void> delete(@PathParam("id") Long id) {
        localizacionService.delete(id);
        return ApiResponse.success(null);
    }
}
