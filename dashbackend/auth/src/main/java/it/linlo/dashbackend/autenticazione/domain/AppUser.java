package it.linlo.dashbackend.autenticazione.domain;

import it.linlo.dashbackend.core.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

// "user" è parola riservata su Postgres, quindi tabella app_user
@Entity
@Table(name = "app_user", schema = "public")
public class AppUser extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "app_user_role", schema = "public", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    protected AppUser() {
    }

    public AppUser(String username, String email, String passwordHash, Set<Role> roles) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = new HashSet<>(roles);
    }

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        setCreatedBy(getId());
        setUpdatedBy(getId());
    }

    @PreUpdate
    @Override
    protected void onUpdate() {
        super.onUpdate();
        setUpdatedBy(getId());
    }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Set<Role> getRoles() { return roles; }
}
