package upeu.edu.pe.shared.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditListener.class) // Delegamos toda la lógica al Listener
public abstract class AuditableEntity {

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}