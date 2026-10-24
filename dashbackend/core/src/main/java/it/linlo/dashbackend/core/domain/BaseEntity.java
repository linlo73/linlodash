package it.linlo.dashbackend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;
import java.util.UUID;

/** Base comune delle entità: id (IDENTITY, funziona su Postgres e MariaDB) e timestamp. */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(updatable = false)
    private UUID createdBy;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private UUID updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public UUID getCreatedBy() { return createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public UUID getUpdatedBy() { return updatedBy; }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }
}
