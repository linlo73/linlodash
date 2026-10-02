# syntax=docker/dockerfile:1

### Build stage: compila l'intero progetto multi-modulo, ma produce solo il jar di shopping
# Usa un'immagine con Gradle già incluso: nel progetto non è ancora presente il Gradle Wrapper
# (generalo con "gradle wrapper" e poi, se preferisci, sostituisci questo stage con ./gradlew).
FROM gradle:8.14-jdk21 AS build
WORKDIR /workspace

# Copiati prima solo i file di build: se il codice cambia ma le dipendenze no, questo layer resta in cache
COPY settings.gradle.kts build.gradle.kts ./
COPY core/build.gradle.kts core/build.gradle.kts
COPY auth/build.gradle.kts auth/build.gradle.kts
COPY shopping/build.gradle.kts shopping/build.gradle.kts


# Ora il codice sorgente
COPY core core
COPY auth auth
COPY shopping shopping

RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle :shopping:bootJar -x test --no-daemon

### Run stage: solo il jar, nessun tool di build nell'immagine finale
FROM eclipse-temurin:21-jre AS run
WORKDIR /app

RUN useradd --system --create-home appuser
USER appuser

COPY --from=build /workspace/shopping/build/libs/shopping-*.jar shopping.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "shopping.jar"]
