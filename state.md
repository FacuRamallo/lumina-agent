# Ledger-AI Blackboard State

This file serves as the central "Blackboard" for the multi-agent system. Agents read from and write to this file to share context, status, and intermediate artifacts.

## Current State
- **Active Milestone**: M2
- **Active Task**: 2.4
- **Current Phase**: QA Review
- **Assigned Agent**: Quality Gatekeeper

## Blackboard Memory
*Shared context, findings, and temporary data across agents.*

### Context
- Task 2.3 (System Prompt Engineering) is complete and merged. The system has `CategorizationPromptProvider` with a rich multi-section prompt, and `SpringAiCategorizationAdapter` for single-item categorization.
- The current pipeline: `IngestionOrchestrator` → `DomainProcessor` (maps + hashes each raw one-by-one) → logs. Categorization is NOT yet wired into the pipeline.
- `TransactionMapper.map()` hardcodes `Category.UNKNOWN`. This must be parameterized so the orchestrator can drive the category assignment.
- Task 2.4 wires batch LLM categorization into the ingestion pipeline via a new `CategorizationOrchestrator`.

### Architecture Decisions
- **`BatchCategorizationPort`** (application): `List<Category> categorize(List<String> descriptions)` — one LLM call per batch of N descriptions.
- **`SpringAiBatchCategorizationAdapter`** (infrastructure): instance vars = `chatClient` + `promptProvider`. Uses `BeanOutputConverter<List<CategoryResponse>>` (via `ParameterizedTypeReference`) constructed locally. Sends a numbered list of descriptions to the LLM, parses the returned JSON array.
- **`CategorizationOrchestrator`** (application): instance vars = `domainProcessor` + `batchCategorizationPort`. `BATCH_SIZE = 5` is `static final`. Flow: (1) extract descriptions from raws, (2) batch-categorize via port, (3) zip categories back to raws, (4) call `domainProcessor.process(raw, source, category)` for each, (5) return `List<ProcessedTransaction>`.
- **`TransactionMapper.map(RawTransaction, SourceSystem, Category)`**: accept category parameter — remove hardcoded `Category.UNKNOWN`.
- **`DomainProcessor.process(RawTransaction, SourceSystem, Category)`**: forward category to mapper.
- **`IngestionOrchestrator`**: swap `domainProcessor` for `categorizationOrchestrator` (still 2 instance vars: `parserService` + `categorizationOrchestrator`). Call `categorizationOrchestrator.process(rawTransactions, sourceSystem)`, then iterate and log.

### Handshakes & Handoffs
*Used to pass execution control between agents.*

#### Orchestrator (Lead Engineer) -> Execution Engineer
- **Directive**:
    Implement Task 2.4 — Batch Inference Logic.

    **Step 1 — Parameterize Category in domain/application layer (no Spring annotations):**

    1. `src/main/java/com/facundo/lumina/domain/service/TransactionMapper.java` *(MODIFY)*
       - Change signature: `public Transaction map(RawTransaction raw, SourceSystem source, Category category)`
       - Replace `createDescription(raw)` to pass the `category` parameter instead of hardcoding `Category.UNKNOWN`.
       - `createDescription` becomes: `private TransactionDescription createDescription(RawTransaction raw, Category category)`

    2. `src/main/java/com/facundo/lumina/application/DomainProcessor.java` *(MODIFY)*
       - Change signature: `public ProcessedTransaction process(RawTransaction raw, SourceSystem sourceSystem, Category category)`
       - Forward `category` to `mapper.map(raw, sourceSystem, category)`.
       - Import `com.facundo.lumina.domain.Category`.

    **Step 2 — New port in application layer:**

    3. `src/main/java/com/facundo/lumina/application/BatchCategorizationPort.java` *(CREATE)*
       - Pure interface. No framework imports.
       - `List<Category> categorize(List<String> descriptions)`

    **Step 3 — New use case orchestrator in application layer:**

    4. `src/main/java/com/facundo/lumina/application/CategorizationOrchestrator.java` *(CREATE)*
       - `@Service` annotation.
       - Exactly 2 instance variables: `domainProcessor` (DomainProcessor) and `batchCategorizationPort` (BatchCategorizationPort).
       - `private static final int BATCH_SIZE = 5`
       - Public method: `List<ProcessedTransaction> process(List<RawTransaction> raws, SourceSystem sourceSystem)`
         - Extract descriptions: map each `RawTransaction` to its `raw.getDescription()` string → `List<String> descriptions`
         - Categorize in batches: partition `descriptions` into sub-lists of `BATCH_SIZE`, call `batchCategorizationPort.categorize(batch)` per sub-list, flatten results back into a single `List<Category> categories` (same order as input `raws`)
         - Zip: for each index `i`, call `domainProcessor.process(raws.get(i), sourceSystem, categories.get(i))` → collect into `List<ProcessedTransaction>`
         - Return the list
       - Use `IntStream` or a simple for-loop — no else keyword.

    **Step 4 — New infrastructure adapter:**

    5. `src/main/java/com/facundo/lumina/infrastructure/agent/SpringAiBatchCategorizationAdapter.java` *(CREATE)*
       - Implements `BatchCategorizationPort`.
       - `@Service` annotation.
       - Exactly 2 instance variables: `chatClient` (ChatClient) and `promptProvider` (CategorizationPromptProvider).
       - Private record: `record CategoryResponse(Category category) {}`
       - `categorize(List<String> descriptions)`:
         - Create a `BeanOutputConverter<List<CategoryResponse>>` locally using `new BeanOutputConverter<>(new ParameterizedTypeReference<List<CategoryResponse>>() {})`
         - Build user message: a numbered list of descriptions + `converter.getFormat()`:
           ```
           Categorize each of the following transaction descriptions IN ORDER.
           Return a JSON array with exactly {N} objects in the SAME ORDER as the input.
           1. DESC1
           2. DESC2
           ...
           {format instruction}
           ```
         - Call: `chatClient.prompt().system(promptProvider.prompt()).user(userMessage).call().content()`
         - Parse: `converter.convert(response)` → `List<CategoryResponse>`
         - Map: stream → `CategoryResponse::category` → `List<Category>`

    **Step 5 — Update IngestionOrchestrator:**

    6. `src/main/java/com/facundo/lumina/application/IngestionOrchestrator.java` *(MODIFY)*
       - Replace instance var `domainProcessor` (DomainProcessor) with `categorizationOrchestrator` (CategorizationOrchestrator).
       - Constructor: `(ParserService parserService, CategorizationOrchestrator categorizationOrchestrator)`
       - `process(String sourceType, InputStream inputStream)`:
         - `List<RawTransaction> rawTransactions = parserService.parse(sourceType, inputStream)`
         - `SourceSystem sourceSystem = new SourceSystem(sourceType)`
         - `List<ProcessedTransaction> results = categorizationOrchestrator.process(rawTransactions, sourceSystem)`
         - Iterate `results` and call `logProcessedTransaction(processed)` for each.

    **Step 6 — Update existing tests:**

    7. `src/test/java/com/facundo/lumina/domain/service/TransactionMapperTest.java` *(MODIFY)*
       - Update `mapper.map(raw, source)` call to `mapper.map(raw, source, Category.GROCERIES)`.
       - Import `com.facundo.lumina.domain.Category`.

    8. `src/test/java/com/facundo/lumina/application/IngestionOrchestratorTest.java` *(MODIFY)*
       - Replace `DomainProcessor` wiring with a mock `CategorizationOrchestrator`.
       - Mock `categorizationOrchestrator.process(anyList(), any())` to return an empty list.
       - Update `IngestionOrchestrator` constructor call accordingly.
       - Import Mockito (`mock`, `when`, `any`, `anyList`).

    **Step 7 — New tests:**

    9. `src/test/java/com/facundo/lumina/infrastructure/agent/SpringAiBatchCategorizationAdapterTest.java` *(CREATE)*
       - Mock `ChatClient` chain and `CategorizationPromptProvider`.
       - Test: `categorize(List.of("MERCADONA", "NETFLIX"))` with LLM returning `[{"category":"GROCERIES"},{"category":"ENTERTAINMENT"}]`
         → assert result equals `[Category.GROCERIES, Category.ENTERTAINMENT]`.
       - Test: `categorize(List.of("XYZ UNKNOWN"))` with LLM returning `[{"category":"UNKNOWN"}]`
         → assert result equals `[Category.UNKNOWN]`.

    10. `src/test/java/com/facundo/lumina/application/CategorizationOrchestratorTest.java` *(CREATE)*
        - Mock `DomainProcessor` and `BatchCategorizationPort`.
        - Test batching: provide 7 raws; mock `batchCategorizationPort.categorize(anyList())` to return appropriate categories for each batch call (first call returns 5, second returns 2).
        - Assert `domainProcessor.process()` is called exactly 7 times with the correct category for each.
        - Test single-batch: 3 raws → 1 batch call → 3 ProcessedTransactions returned.

    **Constraints:**
    - Strict Hexagonal Architecture: `BatchCategorizationPort` and `CategorizationOrchestrator` are `application`; adapter is `infrastructure`; no Spring annotations in `application` (except `@Service` on `CategorizationOrchestrator` which is acceptable as an application service).
    - Object Calisthenics: max 2 instance vars per class; no `else`; no abbreviations; small methods.
    - `ParameterizedTypeReference` import: `org.springframework.core.ParameterizedTypeReference`.

- **Expected Artifacts**:
    - `BatchCategorizationPort.java` (new)
    - `SpringAiBatchCategorizationAdapter.java` (new)
    - `CategorizationOrchestrator.java` (new)
    - `TransactionMapper.java` (modified)
    - `DomainProcessor.java` (modified)
    - `IngestionOrchestrator.java` (modified)
    - `TransactionMapperTest.java` (modified)
    - `IngestionOrchestratorTest.java` (modified)
    - `SpringAiBatchCategorizationAdapterTest.java` (new)
    - `CategorizationOrchestratorTest.java` (new)

#### Execution Engineer -> Quality Gatekeeper
- **Artifacts Delivered**:
    - `src/main/java/com/facundo/lumina/application/BatchCategorizationPort.java` — New port. Pure interface, no framework imports. Single method `List<Category> categorize(List<String> descriptions)`.
    - `src/main/java/com/facundo/lumina/infrastructure/agent/SpringAiBatchCategorizationAdapter.java` — New adapter. `@Service`. 2 instance vars (`chatClient`, `promptProvider`). `BeanOutputConverter<List<CategoryResponse>>` via `ParameterizedTypeReference` constructed locally. Builds a numbered user message, calls LLM, streams response to `List<Category>`.
    - `src/main/java/com/facundo/lumina/application/CategorizationOrchestrator.java` — New use case. `@Service`. 2 instance vars (`domainProcessor`, `batchCategorizationPort`). `BATCH_SIZE = 5` static final. Three private single-purpose methods: `extractDescriptions`, `categorizeInBatches`, `buildProcessedTransactions`. No else. No abbreviations.
    - `src/main/java/com/facundo/lumina/domain/service/TransactionMapper.java` — Modified. `map()` now accepts `Category category` parameter; removed hardcoded `Category.UNKNOWN`.
    - `src/main/java/com/facundo/lumina/application/DomainProcessor.java` — Modified. `process()` now accepts `Category category` and forwards it to the mapper.
    - `src/main/java/com/facundo/lumina/application/IngestionOrchestrator.java` — Modified. Swapped `domainProcessor` dep for `categorizationOrchestrator`; calls `categorizationOrchestrator.process(rawTransactions, sourceSystem)` and iterates results to log.
    - `src/test/java/com/facundo/lumina/domain/service/TransactionMapperTest.java` — Updated. Passes `Category.GROCERIES` to new `map()` signature.
    - `src/test/java/com/facundo/lumina/application/IngestionOrchestratorTest.java` — Updated. Mocks `CategorizationOrchestrator` returning empty list; removes concrete `DomainProcessor` wiring.
    - `src/test/java/com/facundo/lumina/application/CategorizationOrchestratorTest.java` — New. 2 tests: single-batch (3 raws → 1 LLM call) and multi-batch (7 raws → 2 LLM calls with BATCH_SIZE=5). Verifies correct category assignment per transaction.
    - `src/test/java/com/facundo/lumina/infrastructure/agent/SpringAiBatchCategorizationAdapterTest.java` — New. 2 tests: multi-description batch returning correct ordered categories; single unknown description.
- **Testing Instructions**: Run `./gradlew test` from project root.
- **Proof of Work**: `BUILD SUCCESSFUL in 3s` — all tasks executed: compileJava, compileTestJava, test (5 actionable tasks, no failures).

#### Quality Gatekeeper -> Orchestrator
- **Approval Status**: ✅ Approved
- **Feedback/Fixes**: N/A — all DoD criteria met.

##### Traceability Report
| Directive Item | Artifact | Verdict |
|---|---|---|
| `BatchCategorizationPort` in `application`, pure interface | `BatchCategorizationPort.java` | ✅ No framework imports; single `List<Category> categorize(List<String>)` |
| `SpringAiBatchCategorizationAdapter` — `@Service`, 2 instance vars, local converter | `SpringAiBatchCategorizationAdapter.java` | ✅ `chatClient` + `promptProvider`; `BeanOutputConverter` local via `ParameterizedTypeReference` |
| `CategorizationOrchestrator` — 2 instance vars, `BATCH_SIZE=5`, batching logic | `CategorizationOrchestrator.java` | ✅ `domainProcessor` + `batchCategorizationPort`; 3 private single-purpose methods; no else |
| `TransactionMapper.map()` accepts `Category` | `TransactionMapper.java` | ✅ Signature updated; `Category.UNKNOWN` hardcode removed |
| `DomainProcessor.process()` accepts `Category` | `DomainProcessor.java` | ✅ Forwarded to mapper |
| `IngestionOrchestrator` uses `categorizationOrchestrator` | `IngestionOrchestrator.java` | ✅ 2 instance vars: `parserService` + `categorizationOrchestrator` |
| `TransactionMapperTest` updated | `TransactionMapperTest.java` | ✅ Passes `Category.GROCERIES` to new signature |
| `IngestionOrchestratorTest` updated with mock | `IngestionOrchestratorTest.java` | ✅ `CategorizationOrchestrator` mocked; returns empty list |
| `CategorizationOrchestratorTest` — 2 tests | `CategorizationOrchestratorTest.java` | ✅ Verifies 1 LLM call for 3 raws; 2 LLM calls for 7 raws (BATCH_SIZE=5) |
| `SpringAiBatchCategorizationAdapterTest` — 2 tests | `SpringAiBatchCategorizationAdapterTest.java` | ✅ Multi-item ordered result; single UNKNOWN description |

##### Global DoD Checklist
- [x] Code compiles without errors — `BUILD SUCCESSFUL`
- [x] All tests pass — 0 failures
- [x] **Hexagonal Architecture** — `BatchCategorizationPort` and `CategorizationOrchestrator` in `application`; adapter in `infrastructure`; zero framework imports in `domain`
- [x] **SOLID** — SRP across all 3 new classes; DIP via ports; ISP (minimal interfaces)
- [x] **Object Calisthenics** — all classes ≤2 instance vars; no `else`; no abbreviations; all methods small and single-purpose; `BATCH_SIZE` is `static final`
- [x] **Clean Code** — intent-revealing names; private method decomposition; no unnecessary comments
- [x] **No scope creep** — exactly the 10 specified artifacts created/modified; no unrelated changes
- [x] No regression — all pre-existing tests still pass

## Blockers / Issues
- None.

