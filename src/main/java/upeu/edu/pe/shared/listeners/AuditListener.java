// src/main/java/upeu/edu/pe/shared/listeners/AuditListener.java
package upeu.edu.pe.shared.listeners;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import upeu.edu.pe.shared.entities.AuditableEntity;

import java.time.LocalDateTime;

public class AuditListener {

    @PrePersist
    public void prePersist(AuditableEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        String currentUser = getCurrentUser();
        entity.setCreatedBy(currentUser);
        entity.setUpdatedBy(currentUser);
    }

    @PreUpdate
    public void preUpdate(AuditableEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(getCurrentUser());
    }

    private String getCurrentUser() {
        try {
            // Obtener el SecurityIdentity del contexto CDI
            SecurityIdentity securityIdentity = CDI.current().select(SecurityIdentity.class).get();
            
            if (securityIdentity != null && !securityIdentity.isAnonymous()) {
                // Retornar el email del usuario autenticado
                return securityIdentity.getPrincipal().getName();
            }
        } catch (Exception e) {
            // Si no hay contexto de seguridad o falla, usar "system"
        }
        
        return "system";
    }
}