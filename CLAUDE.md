# CLAUDE.md - Kipu Core Architect Guide

Este archivo define las reglas de oro para la IA al trabajar en el repositorio de Kipu.

## 🛠 Comandos del Proyecto
- **Construir:** `./mvnw clean package`
- **Ejecutar:** `./mvnw spring-boot:run`
- **Testear:** `./mvnw test`
- **Formatear (Obligatorio):** `./mvnw spotless:apply`
- **Verificar Formato:** `./mvnw spotless:check`

## 🏗 Arquitectura y Diseño
Kipu utiliza **Arquitectura Hexagonal (Puertos y Adaptadores)** con **Domain-Driven Design (DDD)**.

### Reglas de Capas (Restricciones Estrictas)
1. **Domain (`<domain>/domain/`):** Lógica pura. Prohibido usar anotaciones de Frameworks (Spring, JPA, Jackson). Las interfaces de los repositorios viven aquí.
2. **Application (`<domain>/application/`):** Orquestación de Casos de Uso. No contiene lógica de persistencia.
3. **Infrastructure (`<domain>/infrastructure/`):** Implementaciones de adaptadores, entidades JPA y configuración de Spring.

### 📜 Reglas de Estilo y Código
- **Prohibido Imports Masivos:** Ningún import debe usar el comodín `.*`. Cada import debe ser explícito.
- **Límite de Inyección:** Ninguna clase puede tener más de **5 dependencias** (Constructor Injection). Si supera este límite, debe refactorizarse en servicios más pequeños.
- **Inyección por Constructor:** No usar `@Autowired` en campos. Usar constructores (preferiblemente `@RequiredArgsConstructor` de Lombok).
- **Inmutabilidad:** Usar `final` para campos de clase siempre que sea posible.
- **Tipado de Identificadores:** Usar siempre `java.util.UUID` para IDs de base de datos.
- **Manejo de Fechas:** Usar `java.time.OffsetDateTime` o `ZonedDateTime` para auditoría y `LocalDate` para eventos de calendario.

## 🗄 Persistencia y Datos
- **Migraciones:** No modificar archivos SQL existentes en `db/migration/`. Crear siempre una nueva versión `V[N]__descripcion.sql`.
- **Atributos Dinámicos:** El campo `dynamic_attributes` en `contacts` es de tipo `JSONB`. Para mapearlo en Java, usar la librería `hypersistence-utils` con `Map<String, Object>`.
- **Estrategia ddl-auto:** Debe estar siempre en `validate` en el entorno principal; Flyway es el único dueño del esquema.

## 🎯 Contexto de Bounded Contexts
- **common:** Utilidades transversales y excepciones base.
- **identity:** Gestión de `User` y `RefreshToken`. Base de seguridad JWT.
- **contacts:** Gestión de `Contact`, `UserTag` e `Interaction`. Depende de `identity` solo para el `owner_user_id`.
- **operations:** El motor proactivo. Contiene el `ScheduledWorker` que procesa `ContactEvent` y genera `Action`.

## 🛡 Seguridad
- Usar siempre `SecurityContextHolder` para obtener el usuario actual.
- Validar siempre que el `owner_user_id` del recurso coincida con el usuario autenticado (Multitenancy lógico).

## Logging Standard
- **Library:** Use Lombok `@Slf4j`.
- **Naming:** Follow the pattern `[Action] Message - Metadata: {}`.
- **Lifecycle:** Every Use Case must log its start and successful end.
    - Start: `log.info("[{}] Starting process with id: {}", className, id);`
    - End: `log.info("[{}] Process completed successfully for id: {}", className, id);`
- **Error Logging:** Always log the exception message and context in ERROR level.
    - `log.error("[{}] Error occurred during {}: {}", className, action, e.getMessage());`
- **Security/PII:** NEVER log sensitive data:
    - No passwords, no plain secrets, no full JWT tokens (only first 10 chars if needed).
    - Avoid logging emails in INFO level; use UserID instead.
- **Traceability:** In Adapters (DB/External APIs), use DEBUG level for raw payloads and INFO for execution status.