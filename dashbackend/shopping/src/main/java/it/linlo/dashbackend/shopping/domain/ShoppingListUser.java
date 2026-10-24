package it.linlo.dashbackend.shopping.domain;

import it.linlo.dashbackend.autenticazione.domain.AppUser;
import it.linlo.dashbackend.core.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "list_user", schema = "shopping")
public class ShoppingListUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    private ShoppingList list;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    protected ShoppingListUser() {
    }

    public ShoppingListUser(ShoppingList list, AppUser user) {
        this.list = list;
        this.user = user;
    }

    public ShoppingList getList() {
        return list;
    }

    public AppUser getUser() {
        return user;
    }

    public void changeReferences(ShoppingList list, AppUser user) {
        this.list = list;
        this.user = user;
    }
}
