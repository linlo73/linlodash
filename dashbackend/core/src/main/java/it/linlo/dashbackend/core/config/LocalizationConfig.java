package it.linlo.dashbackend.core.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalizationConfig {

    @Bean
    public MessageSource messageSource() {
        return new JsonMessageSource();
    }
}
