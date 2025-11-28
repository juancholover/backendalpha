package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarUsuarioUseCase {

    private final AuthUsuarioRepository authUsuarioRepository;

    @Inject
    public BuscarUsuarioUseCase(AuthUsuarioRepository authUsuarioRepository) {
        this.authUsuarioRepository = authUsuarioRepository;
    }

    public AuthUsuario findById(Long id) {
        return authUsuarioRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }

    public AuthUsuario findByUsername(String username) {
        return authUsuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
    }

    public List<AuthUsuario> findAll() {
        return authUsuarioRepository.findAllUsuarios();
    }

    public long countByRol(Long rolId) {
        return authUsuarioRepository.countByRol(rolId);
    }
}
