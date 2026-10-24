package it.linlo.dashbackend.shopping.web;

import it.linlo.dashbackend.core.error.NotFoundException;
import it.linlo.dashbackend.shopping.domain.ShoppingList;
import it.linlo.dashbackend.shopping.domain.ShoppingListObject;
import it.linlo.dashbackend.shopping.domain.ShoppingListObjectRepository;
import it.linlo.dashbackend.shopping.domain.ShoppingListRepository;
import it.linlo.dashbackend.shopping.domain.ShoppingObject;
import it.linlo.dashbackend.shopping.domain.ShoppingObjectRepository;
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
@RequestMapping("/api/shopping/list-objects")
@Tag(name = "Oggetti nelle liste")
public class ShoppingListObjectController {

    private final ShoppingListObjectRepository links;
    private final ShoppingListRepository lists;
    private final ShoppingObjectRepository objects;

    public ShoppingListObjectController(ShoppingListObjectRepository links, ShoppingListRepository lists,
                                        ShoppingObjectRepository objects) {
        this.links = links;
        this.lists = lists;
        this.objects = objects;
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
        return LinkResponse.from(links.save(new ShoppingListObject(
                findList(request.listId()), findObject(request.objectId()))));
    }

    @PutMapping("/{id}")
    @Transactional
    public LinkResponse update(@PathVariable UUID id, @Valid @RequestBody LinkRequest request) {
        ShoppingListObject link = findLink(id);
        link.changeReferences(findList(request.listId()), findObject(request.objectId()));
        return LinkResponse.from(links.save(link));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable UUID id) {
        links.delete(findLink(id));
    }

    private ShoppingListObject findLink(UUID id) {
        return links.findById(id).orElseThrow(() -> new NotFoundException("shopping.list-object.not-found"));
    }

    private ShoppingList findList(UUID id) {
        return lists.findById(id).orElseThrow(() -> new NotFoundException("shopping.list.not-found"));
    }

    private ShoppingObject findObject(UUID id) {
        return objects.findById(id).orElseThrow(() -> new NotFoundException("shopping.object.not-found"));
    }

    public record LinkRequest(
            @NotNull(message = "{validation.notNull}") UUID listId,
            @NotNull(message = "{validation.notNull}") UUID objectId) {
    }

    public record LinkResponse(UUID id, UUID listId, UUID objectId, Instant createdAt, Instant updatedAt) {
        static LinkResponse from(ShoppingListObject link) {
            return new LinkResponse(link.getId(), link.getList().getId(), link.getObject().getId(),
                    link.getCreatedAt(), link.getUpdatedAt());
        }
    }
}
