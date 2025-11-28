package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class CambiarPasswordUseCase {

    private final AuthUsuarioRepository authUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Inject
    public CambiarPasswordUseCase(AuthUsuarioRepository authUsuarioRepository, PasswordEncoder passwordEncoder) {
        this.authUsuarioRepository = authUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(Long id, String currentPassword, String newPassword) {
        AuthUsuario authUsuario = authUsuarioRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (!passwordEncoder.matches(currentPassword, authUsuario.getPasswordHash())) {
            throw new NotAuthorizedException("La contraseña actual es incorrecta");
        }

        String hashedPassword = passwordEncoder.encode(newPassword);
        authUsuario.setPasswordHash(hashedPassword);
        authUsuarioRepository.persist(authUsuario);
    }
}
