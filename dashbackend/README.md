# dash-backend

Progetto multi-modulo Gradle, Java 21, Spring Boot 3.5.

| Modulo           | Tipo                         | Contenuto                                                                                                                                                                          |
|------------------|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `core`           | libreria                     | dipendenze comuni (web, JPA, validation, springdoc), driver Postgres/MariaDB, `BaseEntity`, gestione errori (`ApiError`, `GlobalExceptionHandler`), configurazione OpenAPI/Swagger |
| `auth`           | libreria                     | utenti (`AppUser`), registrazione/login, JWT (`JwtService`, filtro), Spring Security stateless                                                                                     |
| `applicazione1`  | app Spring Boot (porta 8081) | esempio di applicazione che usa `core` + `auth`                                                                                                                                    |
| `shopping`       | app Spring Boot (porta 8082) | idem                                                                                                                                                                               |

## Avvio

```bash
gradle wrapper --gradle-version 9.8.0   # una tantum, genera gradlew
docker compose up -d postgres           # oppure: mariadb
./gradlew test
./gradlew :applicazione1:bootRun
```

Swagger UI: http://localhost:8081/swagger-ui.html (usa "Authorize" con il token ricevuto da `/api/auth/login`).

## Deploy separato su VPS (Docker)

Per deployare/aggiornare/riavviare un'applicazione senza toccare l'altra — anche su due
VPS diversi — usa il compose dedicato di ciascuna, che include già il suo database e
una rete Docker propria:

```bash
# Sul VPS, prima volta:
cp shopping/.env.example shopping/.env   # poi modifica i valori (JWT_SECRET, ecc.)

# Deploy/aggiornamento di applicazione1 (non tocca shopping):
./deploy/deploy-shopping.sh
# equivalente a:
docker compose -f docker-compose.shopping.yml up -d --build
```

Stesso procedimento con `shopping` e `docker-compose.shopping.yml`. Ognuno di
questi due file è autosufficiente: puoi copiare la cartella del progetto su due VPS
diversi e lanciare su ciascuno solo il compose dell'app che ospita.

Note per la produzione:
- il database di ogni app non espone la porta sull'host (raggiungibile solo dal
  container dell'app); scommenta la riga `ports` nel compose solo per debug;
- per aggiornare dopo un `git pull`, rilanciare lo script di deploy ricostruisce
  l'immagine e riavvia solo quel servizio (`restart: unless-stopped` lo fa ripartire
  anche dopo un riavvio dell'host);
- se vuoi zero-downtime o TLS automatico, metti un reverse proxy (Caddy, Traefik o
  Nginx) davanti alle porte 8081/8082 — non è ancora incluso in questo setup.

## Avvio con Docker (microservizi separati, un DB ciascuno)

Ogni applicazione ha il proprio database Postgres e il proprio file di config esterna
(`applicazioneN/.env`, letto da docker-compose e mai copiato nell'immagine): sono due
servizi indipendenti, deployabili e scalabili separatamente.

```bash
cp .env.example .env                                # credenziali dei container Postgres
cp applicazione1/.env.example applicazione1/.env    # config esterna di applicazione1 (già pronta)
cp shopping/.env.example shopping/.env    # config esterna di shopping (già pronta)

docker compose up --build
```

- `applicazione1` → http://localhost:8081/swagger-ui.html (DB Postgres su `localhost:5433`)
- `shopping` → http://localhost:8082/swagger-ui.html (DB Postgres su `localhost:5434`)

Per sviluppare le app in locale con `./gradlew bootRun` e avere solo i database in Docker:

```bash
docker compose up postgres-app1 postgres-app2
```

### Cambio del nome del database PostgreSQL

`POSTGRES_DB` viene applicato dall'immagine PostgreSQL solo quando il volume è
vuoto. 

```bash
docker compose -f docker-compose.shopping.yml down
docker volume rm dashbackend_pgdata-shopping
docker compose -f docker-compose.shopping.yml up -d --build
```

Se i dati devono essere conservati, non eliminare il volume: creare invece il
database una sola volta nel container PostgreSQL:

```bash
docker exec -it pg-linlodash psql -U shopping -d postgres -c "CREATE DATABASE linlodash OWNER shopping;"
```

In questo caso aggiorna anche `shopping/.env` e riavvia solo il servizio
`shopping`; Flyway applicherà poi le migrazioni al nuovo database.

### Config esterna

Ogni `applicazioneN/.env` contiene `DB_URL`, `DB_USER`, `DB_PASSWORD`, `DDL_AUTO`, `JWT_SECRET`:
sono le uniche variabili che cambiano tra ambienti (dev/staging/prod) e non fanno mai parte
dell'immagine Docker, che è identica ovunque. In produzione:

- non committare i file `.env` reali (sono già in `.gitignore`; restano solo gli `.env.example`);
- passa i valori come variabili d'ambiente del sistema di orchestrazione (Kubernetes Secret,
  Docker Swarm secret, variabili del PaaS...) invece che con un file `.env` in chiaro;
- imposta un `JWT_SECRET` diverso e casuale per ciascuna applicazione — se coincide, un token
  emesso da `applicazione1` verrebbe accettato anche da `shopping`.

## Postgres o MariaDB

Si sceglie con variabili d'ambiente, senza toccare il codice (default: Postgres in locale):

```bash
# MariaDB
DB_URL=jdbc:mariadb://localhost:3306/linlodash ./gradlew :applicazione1:bootRun
```

Altre variabili: `DB_USER`, `DB_PASSWORD`, `DB_SCHEMA`, `JWT_SECRET`
(min. 32 caratteri, **da cambiare in produzione**) e `DDL_AUTO`.

## Formato errori

Tutte le risposte di errore (400, 401, 403, 404, 409, 500...) hanno la forma:

```json
{ "timestamp": "...", "status": 400, "error": "Bad Request", "message": "Errore di validazione", "path": "/api/auth/register", "details": ["email: must be a well-formed email address"] }
```

Per errori di dominio lancia `NotFoundException`, `ConflictException` o estendi `ApiException`.

## Messaggi e lingue

I messaggi sono definiti fuori dal codice in `config/messages.properties` e
`config/messages_en.properties`. L'applicazione sceglie la lingua dalla richiesta
`Accept-Language` (italiano predefinito); per aggiungerne una basta creare il relativo
file `config/messages_<lingua>.properties` con le stesse chiavi. I bundle nel modulo
`core` forniscono i messaggi di fallback.

Nei deploy Docker la cartella `config` viene montata in sola lettura; è possibile
indicare un altro basename con `MESSAGES_BASENAME` (per esempio
`file:/app/config/messages`). Le modifiche ai file vengono ricaricate automaticamente.

## Note

- I test usano H2 in memoria (`src/test/resources/application.yml` di ogni applicazione).
- Nuovo utente = ruolo `USER`. Per avere un `ADMIN` va assegnato a mano nel DB (tabella `app_user_roles`) o con un seed iniziale.
- Il database principale si chiama `linlodash`. Flyway mantiene le tabelle condivise
  del core nello schema `public`; ogni applicazione usa uno schema dedicato tramite
  `DB_SCHEMA` (`applicazione1` o `shopping`).
- In produzione: Flyway è attivo di default, usa le migrazioni in
  `core/src/main/resources/db/migration.core_d` e Hibernate usa `DDL_AUTO=validate`.
  Per disattivarlo temporaneamente si può impostare `FLYWAY_ENABLED=false`.
