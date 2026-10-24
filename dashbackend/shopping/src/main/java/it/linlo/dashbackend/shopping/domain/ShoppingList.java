package it.linlo.dashbackend.shopping.domain;

import it.linlo.dashbackend.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "list", schema = "shopping")
public class ShoppingList extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    protected ShoppingList() {
    }

    public ShoppingList(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void rename(String name) {
        this.name = name;
    }
}
