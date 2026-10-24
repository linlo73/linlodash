package it.linlo.dashbackend.shopping.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShoppingObjectRepository extends JpaRepository<ShoppingObject, UUID> {
}
