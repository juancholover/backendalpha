package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;
import upeu.edu.pe.people.domain.repositories.TipoAutoridadRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;

@ApplicationScoped
public class CrearTipoAutoridadUseCase {

    @Inject
    TipoAutoridadRepository tipoAutoridadRepository;

    @Transactional
    public TipoAutoridad execute(String codigo, String nombre, String descripcion, Integer nivelJerarquia) {
        // Validar nombre único
        if (tipoAutoridadRepository.existsByNombre(nombre)) {
            throw new BusinessException("Ya existe un tipo de autoridad con el nombre: " + nombre);
        }

        TipoAutoridad tipo = new TipoAutoridad();
        tipo.setCodigo(codigo);
        tipo.setNombre(nombre);
        tipo.setDescripcion(descripcion);
        tipo.setNivelJerarquia(nivelJerarquia);

        tipoAutoridadRepository.persist(tipo);
        return tipo;
    }
}
