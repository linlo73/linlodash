package it.linlo.dashbackend.shopping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Endpoint di esempio: sostituiscili con la logica reale dell'applicazione. */
@RestController
@RequestMapping("/api/shopping")
@Tag(name = "Shopping")
public class HelloController {

    private final MessageSource messages;

    public HelloController(MessageSource messages) {
        this.messages = messages;
    }

    @GetMapping("/hello")
    @Operation(summary = "Richiede un JWT valido")
    public Map<String, String> hello(Authentication authentication) {
        return Map.of("app", "shopping", "message",
                messages.getMessage("greeting", new Object[]{authentication.getName()}, LocaleContextHolder.getLocale()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Richiede il ruolo ADMIN")
    public Map<String, String> admin(Authentication authentication) {
        return Map.of("app", "shopping", "message",
                messages.getMessage("admin.greeting", new Object[]{authentication.getName()}, LocaleContextHolder.getLocale()));
    }
}
