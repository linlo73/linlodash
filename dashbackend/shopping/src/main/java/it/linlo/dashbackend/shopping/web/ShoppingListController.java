package it.linlo.dashbackend.shopping.web;

import it.linlo.dashbackend.core.error.NotFoundException;
import it.linlo.dashbackend.shopping.domain.ShoppingList;
import it.linlo.dashbackend.shopping.domain.ShoppingListRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/api/shopping/lists")
@Tag(name = "Liste")
public class ShoppingListController {

    private final ShoppingListRepository lists;

    public ShoppingListController(ShoppingListRepository lists) {
        this.lists = lists;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ListResponse> findAll() {
        return lists.findAll().stream().map(ListResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ListResponse findById(@PathVariable UUID id) {
        return ListResponse.from(findList(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ListResponse create(@Valid @RequestBody NameRequest request) {
        return ListResponse.from(lists.save(new ShoppingList(request.name())));
    }

    @PutMapping("/{id}")
    @Transactional
    public ListResponse update(@PathVariable UUID id, @Valid @RequestBody NameRequest request) {
        ShoppingList list = findList(id);
        list.rename(request.name());
        return ListResponse.from(lists.save(list));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable UUID id) {
        lists.delete(findList(id));
    }

    private ShoppingList findList(UUID id) {
        return lists.findById(id).orElseThrow(() -> new NotFoundException("shopping.list.not-found"));
    }

    public record NameRequest(
            @NotBlank(message = "{validation.notBlank}")
            @Size(max = 100, message = "{shopping.name.size}") String name) {
    }

    public record ListResponse(UUID id, String name, Instant createdAt, Instant updatedAt) {
        static ListResponse from(ShoppingList list) {
            return new ListResponse(list.getId(), list.getName(), list.getCreatedAt(), list.getUpdatedAt());
        }
    }
}
