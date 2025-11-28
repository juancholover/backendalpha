package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarUsuarioUseCase {

    private final AuthUsuarioRepository authUsuarioRepository;

    @Inject
    public EliminarUsuarioUseCase(AuthUsuarioRepository authUsuarioRepository) {
        this.authUsuarioRepository = authUsuarioRepository;
    }

    @Transactional
    public void execute(Long id) {
        AuthUsuario authUsuario = authUsuarioRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        authUsuario.setActive(false);
        authUsuarioRepository.persist(authUsuario);
    }
}
