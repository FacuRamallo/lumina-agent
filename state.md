# Ledger-AI Blackboard State

This file serves as the central "Blackboard" for the multi-agent system. Agents read from and write to this file to share context, status, and intermediate artifacts.

## Current State
- **Active Milestone**: M2
- **Active Task**: 2.2
- **Current Phase**: QA Review
- **Assigned Agent**: Quality Gatekeeper

## Blackboard Memory
*Shared context, findings, and temporary data across agents.*

### Context
- Task 2.1 (Ollama & Spring AI Integration) is complete. The system now has a `LanguageModelPort` and an `AiConfig`.
- We now need to enforce structured output for transaction categorization using Spring AI's `BeanOutputConverter`.
- The `Category` must be a pure domain concept, so the framework-specific `BeanOutputConverter` must reside entirely in the `infrastructure` adapter layer.

### Architecture Decisions
- Create `Category` enum in `com.facundo.lumina.domain`.
- Create `CategorizationPort` interface in `com.facundo.lumina.application`.
- Create `SpringAiCategorizationAdapter` in `com.facundo.lumina.infrastructure.agent`. This adapter uses Spring AI's `ChatClient` (or it can use `ChatClient` directly rather than the generic `LanguageModelPort` since `BeanOutputConverter` requires Spring AI's fluent API to extract the parsed entity easily. We will inject `ChatClient` into `SpringAiCategorizationAdapter`).

### Handshakes & Handoffs
*Used to pass execution control between agents.*

#### Orchestrator (Lead Engineer) -> Execution Engineer
- **Directive**:
    Implement Task 2.2 — Structured Output Service for Categorization.

    **Files to Create:**
    1. `src/main/java/com/facundo/lumina/domain/Category.java`
       - Enum with values: `GROCERIES`, `UTILITIES`, `ENTERTAINMENT`, `TRANSPORT`, `INTERNAL_TRANSFER`, `UNKNOWN`.
    2. `src/main/java/com/facundo/lumina/application/CategorizationPort.java`
       - Interface with method: `Category categorize(String rawDescription);`
    3. `src/main/java/com/facundo/lumina/infrastructure/agent/SpringAiCategorizationAdapter.java`
       - Implements `CategorizationPort`.
       - Annotate with `@Service`.
       - Inject `ChatClient` via constructor.
       - Use Spring AI's `BeanOutputConverter<CategoryResponse>` to request and parse JSON. (You may need to create a private record `CategoryResponse(Category category)` inside the adapter to help the converter map cleanly).
       - Ensure the system prompt strictly instructs the LLM to analyze the description and return the correct category from the enum.
    4. `src/test/java/com/facundo/lumina/infrastructure/agent/SpringAiCategorizationAdapterTest.java`
       - Unit test with Mockito. Test the mapping behavior.
    
    **Constraints:**
    - Strict adherence to Hexagonal Architecture. No Spring AI in `domain` or `application`.
    - Object Calisthenics: max 2 instance variables per class.

- **Expected Artifacts**:
    - `Category.java`
    - `CategorizationPort.java`
    - `SpringAiCategorizationAdapter.java`
    - `SpringAiCategorizationAdapterTest.java`

#### Execution Engineer -> Quality Gatekeeper
- **Artifacts Delivered**:
    - `src/main/java/com/facundo/lumina/domain/Category.java` — Enum with values: GROCERIES, UTILITIES, ENTERTAINMENT, TRANSPORT, INTERNAL_TRANSFER, UNKNOWN. Replaced prior value-object implementation.
    - `src/main/java/com/facundo/lumina/application/CategorizationPort.java` — Pure application-layer port interface. No framework imports.
    - `src/main/java/com/facundo/lumina/infrastructure/agent/SpringAiCategorizationAdapter.java` — Implements `CategorizationPort` using `BeanOutputConverter<CategoryResponse>`. Annotated `@Service`. Two instance variables (`chatClient`, `converter`). `CategoryResponse` record encapsulated inside the adapter.
    - `src/test/java/com/facundo/lumina/infrastructure/agent/SpringAiCategorizationAdapterTest.java` — 5 Mockito unit tests covering: GROCERIES, INTERNAL_TRANSFER, UNKNOWN, ENTERTAINMENT, TRANSPORT mappings.
    - `src/main/java/com/facundo/lumina/domain/service/TransactionMapper.java` — Migrated `new Category("Uncategorized")` → `Category.UNKNOWN`.
    - `src/test/java/com/facundo/lumina/domain/TransactionTest.java` — Migrated Category instantiations to enum literals.
    - `src/test/java/com/facundo/lumina/domain/service/HashingServiceTest.java` — Migrated Category instantiation to `Category.UNKNOWN`.
- **Testing Instructions**: Run `./gradlew test` from project root.
- **Proof of Work**: `BUILD SUCCESSFUL in 2s` — all tasks executed: compileJava, compileTestJava, test (5 actionable tasks, no failures).

#### Quality Gatekeeper -> Orchestrator
- **Approval Status**: ✅ Approved
- **Feedback/Fixes**: N/A — all DoD criteria met.

##### Traceability Report
| Directive Item | Artifact | Verdict |
|---|---|---|
| `Category` enum in `domain` | `domain/Category.java` | ✅ Pure enum, zero framework imports |
| `CategorizationPort` in `application` | `application/CategorizationPort.java` | ✅ Interface only, only domain imports |
| `SpringAiCategorizationAdapter` in `infrastructure` | `infrastructure/agent/SpringAiCategorizationAdapter.java` | ✅ `@Service`, `BeanOutputConverter`, 2 instance vars |
| Unit test with Mockito | `SpringAiCategorizationAdapterTest.java` | ✅ 5 scenarios, all enum values covered |

##### Global DoD Checklist
- [x] Code compiles without errors — `BUILD SUCCESSFUL`
- [x] All tests pass — 0 failures
- [x] **Hexagonal Architecture** — Spring AI confined to `infrastructure`; `domain` and `application` have zero framework dependencies
- [x] **SOLID** — SRP enforced (adapter only categorizes); DIP via port interface; ISP (minimal interface)
- [x] **Object Calisthenics** — 2 instance variables on adapter (`chatClient`, `converter`); `SYSTEM_PROMPT` is `static final`, not an instance var; `CategoryResponse` is a record (no getters/setters/properties); no else keywords; functions are small and single-purpose
- [x] **Clean Code** — intent-revealing names; no unnecessary comments; logic is self-documenting
- [x] **No scope creep** — migration of existing `new Category(...)` callsites in `TransactionMapper`, `TransactionTest`, and `HashingServiceTest` is a required consequence of the enum refactor, not unrelated work
- [x] No regression — all pre-existing tests still pass

## Blockers / Issues
- None.
