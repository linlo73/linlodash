package it.linlo.dashbackend.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.flyway.core")
public record CoreFlywayProperties(
        // Abilita/disabilita il core flyway (default: true)
        @DefaultValue("true") boolean enabled,

        // Nome dello schema del core (default: schema_core)
        @DefaultValue("public") String schema,

        // Nome della cartella di migrazione del core (default: core)
        @DefaultValue("core") String locationFolder
) {}