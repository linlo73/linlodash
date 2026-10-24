package it.linlo.dashbackend.shopping.domain;

import it.linlo.dashbackend.core.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "list_object", schema = "shopping")
public class ShoppingListObject extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    private ShoppingList list;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "object_id", nullable = false)
    private ShoppingObject object;

    protected ShoppingListObject() {
    }

    public ShoppingListObject(ShoppingList list, ShoppingObject object) {
        this.list = list;
        this.object = object;
    }

    public ShoppingList getList() {
        return list;
    }

    public ShoppingObject getObject() {
        return object;
    }

    public void changeReferences(ShoppingList list, ShoppingObject object) {
        this.list = list;
        this.object = object;
    }
}
