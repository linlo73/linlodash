plugins { `java-library` }

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    implementation("org.flywaydb:flyway-core")

    // Il driver viene scelto dall'URL JDBC: nessun codice da cambiare tra Postgres e MariaDB
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
}
