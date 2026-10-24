package it.linlo.dashbackend.shopping.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.flyway.shopping")
public record ShoppingFlywayProperties(
        // Definisce la proprietà "schema" con un valore di default se omessa
        @DefaultValue("shopping") String schema,
        @DefaultValue("shopping") String locationFolder
) {}
