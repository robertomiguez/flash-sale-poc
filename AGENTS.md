# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/example/flashsale/` contains the application code.
- `controller/` exposes HTTP endpoints, `service/` holds business logic, `repository/` wraps persistence, `model/` contains JPA entities, and `dto/` defines request/response payloads.
- `src/main/resources/application.yml` holds runtime configuration.
- `src/test/java/com/example/flashsale/` contains unit and slice tests.
- `src/rest/` includes runnable REST examples for manual API checks.
- `docker-compose.yml` starts the local PostgreSQL, Redis, and RabbitMQ dependencies.

## Build, Test, and Development Commands
- `docker compose up -d` starts the required infrastructure locally.
- `SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run` runs the application with the dev profile.
- `mvn test` runs the full test suite.
- `mvn package` builds the runnable JAR under `target/`.

## Coding Style & Naming Conventions
- Use Java 25 and standard Spring Boot conventions.
- Keep indentation to 4 spaces and prefer clear, descriptive names.
- Use `PascalCase` for classes, `camelCase` for methods and fields, and `UPPER_SNAKE_CASE` for constants.
- Place controllers in `controller`, services in `service`, and persistence code in `repository`.
- Follow the existing Lombok and Spring patterns already used in the codebase; no formatter is enforced in the repo.

## Testing Guidelines
- Tests use `spring-boot-starter-test` and live under `src/test/java`.
- Name tests after the class under test, for example `OrderListenerTest`.
- Keep tests focused on observable behavior: order state changes, Redis stock updates, and message handling.
- Run `mvn test` before opening a pull request.

## Commit & Pull Request Guidelines
- Recent history uses short imperative commits and merge commits from feature branches, for example `Add demo script to README`.
- Keep commit messages concise and action-oriented.
- Pull requests should include a clear summary, the behavior changed, and any manual verification steps.
- If the change affects API behavior or demo flows, include sample requests or screenshots when useful.

## Configuration & Runtime Notes
- The app expects PostgreSQL, Redis, and RabbitMQ to be available locally.
- RabbitMQ resources use the `V2` names documented in `README.md` to avoid conflicts with older queues.
- Redis is the fast stock gate; PostgreSQL is the durable order store.
