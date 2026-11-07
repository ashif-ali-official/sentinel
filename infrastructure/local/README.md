# Local infrastructure and service profiles

Copy `.env.example` to `.env` and set unique local MySQL passwords. Keep `.env` out of version control.

Start local infrastructure from this directory:

```sh
docker compose --env-file .env up -d
```

Spring services use `application-local.yml` when `SPRING_PROFILES_ACTIVE=local`. The profile expects `MYSQL_PASSWORD`, matching the application password supplied to MySQL by Compose. In an IDE, set `SPRING_PROFILES_ACTIVE=local` and `MYSQL_PASSWORD` in each service's run configuration. From a terminal, export the values from this directory before launching a service:

```sh
set -a
source .env
set +a
SPRING_PROFILES_ACTIVE=local mvn -f ../../services/player-service/pom.xml spring-boot:run
```

Repeat the Maven command for each service. Local profile files provide localhost endpoints, local ports, and the local Cognito pool defaults. Production runs should omit the `local` profile and provide database, Kafka, Cognito, CORS, and service URL values through the deployment environment or secret manager.

The client reads its Vite configuration at build time. Copy `client/sentinel-ui/.env.example` to `client/sentinel-ui/.env.local` and set the Cognito values and the deployed service URLs before building the client. Vite variables are bundled into browser code; use them only for public configuration, never secrets.
