package it.linlo.dashbackend.core.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JsonMessageSource extends AbstractMessageSource {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 💡 CAMBIO CHIAVE: Usiamo String ("it", "en") invece di Locale come chiave!
    private final Map<String, JsonNode> messagesCache = new HashMap<>();

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        JsonNode rootNode = loadJsonForLocale(locale);
        if (rootNode == null) {
            return null;
        }

        // Naviga l'albero JSON separando sui punti
        String[] keys = code.split("\\.");
        JsonNode currentNode = rootNode;
        for (String key : keys) {
            currentNode = currentNode.path(key);
            if (currentNode.isMissingNode()) {
                return null;
            }
        }

        if (currentNode.isTextual()) {
            return new MessageFormat(currentNode.asText(), locale);
        }

        return null;
    }

    private JsonNode loadJsonForLocale(Locale locale) {
        // Estrae SEMPRE e SOLO la lingua di 2 lettere (es. "it" sia da "it" che da "it_IT")
        String langKey = locale.getLanguage().toLowerCase();

        return messagesCache.computeIfAbsent(langKey, lang -> {
            try {
                // Compone il percorso relativo (es. i18n/messages_it.json)
                String path = "i18n/messages_" + lang + ".json";
                Resource resource = new ClassPathResource(path);

                // Fallback sul file inglese se il file della lingua non esiste sul disco
                if (!resource.exists()) {
                    logger.warn("File di traduzione JSON non trovato: " + path + ". Fallback su 'en'.");
                    resource = new ClassPathResource("i18n/messages_en.json");
                }

                if (!resource.exists()) {
                    throw new IOException("Nessun file di traduzione trovato (mancano sia i18n/messages_" + lang + ".json che i18n/messages_en.json)");
                }

                // Legge il JSON e lo mette in cache
                try (InputStream is = resource.getInputStream()) {
                    return objectMapper.readTree(is);
                }

            } catch (IOException e) {
                logger.error("Impossibile caricare il file JSON di i18n per la lingua: " + lang, e);
                return null;
            }
        });
    }
}
