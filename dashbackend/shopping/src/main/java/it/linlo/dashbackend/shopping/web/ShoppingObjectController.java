package it.linlo.dashbackend.shopping.web;

import it.linlo.dashbackend.core.error.NotFoundException;
import it.linlo.dashbackend.shopping.domain.ShoppingObject;
import it.linlo.dashbackend.shopping.domain.ShoppingObjectRepository;
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
@RequestMapping("/api/shopping/objects")
@Tag(name = "Oggetti")
public class ShoppingObjectController {

    private final ShoppingObjectRepository objects;

    public ShoppingObjectController(ShoppingObjectRepository objects) {
        this.objects = objects;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ObjectResponse> findAll() {
        return objects.findAll().stream().map(ObjectResponse::from).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ObjectResponse findById(@PathVariable UUID id) {
        return ObjectResponse.from(findObject(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ObjectResponse create(@Valid @RequestBody NameRequest request) {
        return ObjectResponse.from(objects.save(new ShoppingObject(request.name())));
    }

    @PutMapping("/{id}")
    @Transactional
    public ObjectResponse update(@PathVariable UUID id, @Valid @RequestBody NameRequest request) {
        ShoppingObject object = findObject(id);
        object.rename(request.name());
        return ObjectResponse.from(objects.save(object));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void delete(@PathVariable UUID id) {
        objects.delete(findObject(id));
    }

    private ShoppingObject findObject(UUID id) {
        return objects.findById(id).orElseThrow(() -> new NotFoundException("shopping.object.not-found"));
    }

    public record NameRequest(
            @NotBlank(message = "{validation.notBlank}")
            @Size(max = 100, message = "{shopping.name.size}") String name) {
    }

    public record ObjectResponse(UUID id, String name, Instant createdAt, Instant updatedAt) {
        static ObjectResponse from(ShoppingObject object) {
            return new ObjectResponse(object.getId(), object.getName(), object.getCreatedAt(), object.getUpdatedAt());
        }
    }
}
