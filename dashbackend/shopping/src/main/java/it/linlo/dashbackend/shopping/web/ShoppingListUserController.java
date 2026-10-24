package it.linlo.dashbackend.shopping.web;

import it.linlo.dashbackend.autenticazione.domain.AppUser;
import it.linlo.dashbackend.autenticazione.domain.AppUserRepository;
import it.linlo.dashbackend.core.error.NotFoundException;
import it.linlo.dashbackend.shopping.domain.ShoppingList;
import it.linlo.dashbackend.shopping.domain.ShoppingListRepository;
import it.linlo.dashbackend.shopping.domain.ShoppingListUser;
import it.linlo.dashbackend.shopping.domain.ShoppingListUserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shopping/list-users")
@Tag(name = "Utenti delle liste")
public class ShoppingListUserController {

    private final ShoppingListUserRepository links;
    private final ShoppingListRepository lists;
    private final AppUserRepository users;

    public ShoppingListUserController(ShoppingListUserRepository links, ShoppingListRepository lists,
                                      AppUserRepository users) {
        this.links = links;
        this.lists = lists;
        this.users = users;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<LinkResponse> findAll() {
        return links.findAll().stream().map(LinkResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public LinkResponse findById(@PathVariable UUID id) {
        return LinkResponse.from(findLink(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public LinkResponse create(@Valid @RequestBody LinkRequest request) {
        return LinkResponse.from(links.save(new ShoppingListUser(
                findList(request.listId()), findUser(request.userId()))));
    }

    @PutMapping("/{id}")
    @Transactional
    public LinkResponse update(@PathVariable UUID id, @Valid @RequestBody LinkRequest request) {
        ShoppingListUser link = findLink(id);
        link.changeReferences(findList(request.listId()), findUser(request.userId()));
        return LinkResponse.from(links.save(link));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable UUID id) {
        links.delete(findLink(id));
    }

    private ShoppingListUser findLink(UUID id) {
        return links.findById(id).orElseThrow(() -> new NotFoundException("shopping.list-user.not-found"));
    }

    private ShoppingList findList(UUID id) {
        return lists.findById(id).orElseThrow(() -> new NotFoundException("shopping.list.not-found"));
    }

    private AppUser findUser(UUID id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("shopping.user.not-found"));
    }

    public record LinkRequest(
            @NotNull(message = "{validation.notNull}") UUID listId,
            @NotNull(message = "{validation.notNull}") UUID userId) {
    }

    public record LinkResponse(UUID id, UUID listId, UUID userId, Instant createdAt, Instant updatedAt) {
        static LinkResponse from(ShoppingListUser link) {
            return new LinkResponse(link.getId(), link.getList().getId(), link.getUser().getId(),
                    link.getCreatedAt(), link.getUpdatedAt());
        }
    }
}
