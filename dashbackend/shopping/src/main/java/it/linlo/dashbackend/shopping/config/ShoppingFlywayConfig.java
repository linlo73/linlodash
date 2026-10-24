package it.linlo.dashbackend.shopping.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(ShoppingFlywayProperties.class)
public class ShoppingFlywayConfig {
    @Bean(initMethod = "migrate")
    @DependsOn("flywayCore")
    public Flyway flywayShopping(DataSource dataSource, ShoppingFlywayProperties properties) {
        return Flyway.configure()
                .dataSource(dataSource)
                // 1. Legge lo schema dal record tipizzato
                .schemas(properties.schema())
                // 2. Se vuoi, puoi rendere dinamico anche il path (es. db/migration/shopping)
                .locations("classpath:db/migration/" + properties.locationFolder())
                .table("flyway_schema_history")
                .baselineOnMigrate(true)
                .load();
    }
}
