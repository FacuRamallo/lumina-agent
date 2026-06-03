# Ledger-AI Blackboard State

This file serves as the central "Blackboard" for the multi-agent system. Agents read from and write to this file to share context, status, and intermediate artifacts.

## Current State
- **Active Milestone**: M3
- **Active Task**: 3.1
- **Current Phase**: QA Review
- **Assigned Agent**: Quality Gatekeeper

## Blackboard Memory
*Shared context, findings, and temporary data across agents.*

### Context
- Milestone 2 is complete. The ingestion pipeline now maps raw transactions, batches transaction descriptions (size 5), queries Ollama, and labels transactions accordingly.
- Task 3.1 focuses on setting up PGVector infrastructure. This database will serve as the semantic cache to store and retrieve transaction embeddings in subsequent tasks (Tasks 3.2 and 3.3).

### Architecture Decisions
- **Database Service**: Dockerized PostgreSQL instance using `pgvector/pgvector:pg16` to enable vector storage capabilities.
- **Version Control via Flyway**: Instead of relying on Hibernate or Spring AI's automatic runtime schema generation, we use Flyway to version control database migrations. This is a clean, production-grade practice.
- **DDL Migration Script**: We will create a `V1__init_pgvector.sql` script to load extensions (`vector`, `hstore`, `uuid-ossp`), define the `vector_store` table format (matching Spring AI schema), and establish an HNSW index.
- **JPA & Spring AI Properties**: Disable `initialize-schema: false` on Spring AI's pgvector properties, set datasource coordinates, and set `hibernate.ddl-auto: validate` so Hibernate only checks rather than alters the database schema.

### Handshakes & Handoffs
*Used to pass execution control between agents.*

#### Orchestrator (Lead Engineer) -> Execution Engineer
- **Directive**:
    Implement Task 3.1 — PGVector Infrastructure.

    **Step 1 — Update Docker Compose:**
    Modify `docker-compose.yml` to include a new database service:
    - Image: `pgvector/pgvector:pg16`
    - Container Name: `lumina_db`
    - Port forwarding: `5432:5432`
    - Env variables: `POSTGRES_DB=lumina`, `POSTGRES_USER=lumina`, `POSTGRES_PASSWORD=password`
    - Restart: `unless-stopped`

    **Step 2 — Add Build Dependencies:**
    Add these dependencies in `build.gradle.kts`:
    - `implementation("org.postgresql:postgresql")` (Postgres driver)
    - `implementation("org.springframework.ai:spring-ai-pgvector-store-spring-boot-starter")` (Spring AI PGVector store)
    - `implementation("org.springframework.boot:spring-boot-starter-data-jpa")` (Spring Boot JPA)
    - `implementation("org.flywaydb:flyway-core")` (Flyway core)
    - `runtimeOnly("org.flywaydb:flyway-database-postgresql")` (Flyway Postgres engine extension)

    **Step 3 — Disable Auto-Schema & Configure DataSource / JPA:**
    In `src/main/resources/application.yml`:
    - Add `spring.datasource` configuration.
    - Set `spring.ai.vectorstore.pgvector.initialize-schema: false`.
    - Set `spring.ai.vectorstore.pgvector` properties for dimensions (768), index-type (HNSW), and distance-type (COSINE_DISTANCE).
    - Set `spring.jpa.hibernate.ddl-auto: validate` and set database dialect `org.hibernate.dialect.PostgreSQLDialect`.

    **Step 4 — Create Flyway Initial Schema:**
    Create a new directory `src/main/resources/db/migration/` and place the file `V1__init_pgvector.sql` inside it:
    - Load extensions: `vector`, `hstore`, and `uuid-ossp`.
    - Create the table `vector_store` with columns: `id uuid DEFAULT uuid_generate_v4() PRIMARY KEY`, `content text`, `metadata json`, `embedding vector(768)`.
    - Add an index: `CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);`.

    **Step 5 — Verification:**
    - Run `./gradlew test` to verify existing tests pass.
    - Start the docker-compose service (`docker compose up -d db`), run `./gradlew bootRun`, and verify the database connects successfully and Flyway applies the initial migration.

- **Expected Artifacts**:
    - `docker-compose.yml` *(MODIFY)*
    - `build.gradle.kts` *(MODIFY)*
    - `src/main/resources/application.yml` *(MODIFY)*
    - `src/main/resources/db/migration/V1__init_pgvector.sql` *(NEW)*

#### Execution Engineer -> Quality Gatekeeper
- **Artifacts Delivered**:
    - `docker-compose.yml` *(MODIFY)* — added database container with `pgvector/pgvector:pg16` image on port 5432.
    - `build.gradle.kts` *(MODIFY)* — added postgresql, spring-boot-starter-data-jpa, pgvector store starter, and Flyway dependencies.
    - `src/main/resources/application.yml` *(MODIFY)* — configured connection pool, disabled spring ai auto-schema, configured validation-only DDL check, and set PGVector index parameters.
    - `src/main/resources/db/migration/V1__init_pgvector.sql` *(NEW)* — Flyway schema migration creating `vector` extensions and `vector_store` table structure with HNSW cosine distance index.
- **Testing Instructions**:
    1. Spin up the database container: `docker compose up -d db`
    2. Run `./gradlew test` to verify that existing unit tests pass without regression.
    3. Run `./gradlew bootRun` and confirm in logs that database connects and Flyway automatically runs the migration: `Successfully applied 1 migration to schema "public", now at version v1`.
    4. Confirm schema creation inside Postgres: `docker exec lumina_db psql -U lumina -d lumina -c "\d vector_store"`.

#### Quality Gatekeeper -> Orchestrator
- **Approval Status**: *[Pending]*
- **Feedback/Fixes**: *[Pending]*

## Blockers / Issues
- None.
