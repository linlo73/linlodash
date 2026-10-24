package it.linlo.dashbackend.shopping.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShoppingListUserRepository extends JpaRepository<ShoppingListUser, UUID> {
}
