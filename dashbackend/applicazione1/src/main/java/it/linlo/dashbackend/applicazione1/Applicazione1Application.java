package it.linlo.dashbackend.applicazione1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Scansione estesa a it.linlo.dashbackend per raccogliere componenti, entità e repository di core e auth
@SpringBootApplication(scanBasePackages = "it.linlo.dashbackend")
@EntityScan("it.linlo.dashbackend")
@EnableJpaRepositories("it.linlo.dashbackend")
public class Applicazione1Application {

    public static void main(String[] args) {
        SpringApplication.run(Applicazione1Application.class, args);
    }
}
