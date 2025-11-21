package upeu.edu.pe.shared.listeners;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import upeu.edu.pe.shared.context.AuditContext;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.utils.NormalizeProcessor;

import java.time.LocalDateTime;

public class AuditListener {

    @PrePersist
    public void prePersist(AuditableEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        String user = resolveUser();
        entity.setCreatedBy(user);
        entity.setUpdatedBy(user);

        // Procesar anotaciones @Normalize antes de guardar
        NormalizeProcessor.processNormalizeAnnotations(entity);
    }

    @PreUpdate
    public void preUpdate(AuditableEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(resolveUser());

        // Procesar anotaciones @Normalize antes de actualizar
        NormalizeProcessor.processNormalizeAnnotations(entity);
    }

    private String resolveUser() {
        try {
            // 1. Intentar obtener usuario autenticado (JWT/OIDC)
            SecurityIdentity securityIdentity = CDI.current().select(SecurityIdentity.class).get();
            if (securityIdentity != null && !securityIdentity.isAnonymous()) {
                return securityIdentity.getPrincipal().getName();
            }

            // 2. Si no hay login web, intentar obtener del contexto manual (Jobs/Background)
            AuditContext auditContext = CDI.current().select(AuditContext.class).get();
            String contextUser = auditContext.getCurrentUser();
            if (contextUser != null && !contextUser.equals("system")) {
                return contextUser;
            }

        } catch (Exception e) {
            // Ignorar errores de contexto en arranque o tests
        }
        
        return "system";
    }
}