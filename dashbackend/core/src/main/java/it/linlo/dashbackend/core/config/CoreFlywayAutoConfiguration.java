package it.linlo.dashbackend.core.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;

@AutoConfiguration
@EnableConfigurationProperties(CoreFlywayProperties.class)
// Mantiene la condizione di attivazione legata alla proprietà gestita dal record
@ConditionalOnProperty(name = "app.flyway.core.enabled", havingValue = "true", matchIfMissing = true)
public class CoreFlywayAutoConfiguration {

    @Bean(name = "flywayCore", initMethod = "migrate")
    public Flyway flywayCore(DataSource dataSource, CoreFlywayProperties properties) {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas(properties.schema()) // Legge il nome dello schema dal record tipizzato
                .locations("classpath:db/migration/" + properties.locationFolder())
                .table("flyway_schema_history")
                .baselineOnMigrate(true)
                .load();
    }
}
