# Ledger-AI Tasks Ledger

This ledger tracks the execution of all tasks in the project using EARS (Easy Approach to Requirements Syntax) notation and clear Definitions of Done (DoD).

## Global Definition of Done (DoD)
*These criteria apply to every task unless otherwise specified:*
- [ ] Code compiles and runs without errors.
- [ ] Unit tests pass (if applicable).
- [ ] Architecture aligns with Blackboard Pattern state.
- [ ] No regression on existing functionality.
- [ ] Documentation is updated in `docs/` if logic changes.
- [ ] QA Gatekeeper has reviewed and approved the artifact in `state.md`.

---

## Active Tasks

### Task 2.3 - System Prompt Engineering
- **Status**: In Progress 🔄
- **Assigned Agent**: Execution Engineer
- **Milestone**: M2
- **EARS**: **While** categorizing a transaction, **When** the LLM is invoked, **the system shall** use a structured, externalized system prompt that encodes a categorization taxonomy, vendor-specific rules, and an explicit INTERNAL_TRANSFER guardrail to prevent double-counting.
- **Task-Specific DoD**:
  - [ ] Extract the inline `SYSTEM_PROMPT` constant from `SpringAiCategorizationAdapter` into a dedicated, externalized `CategorizationPromptProvider` in the `infrastructure` layer.
  - [ ] The prompt provider must expose a single method (e.g., `String prompt()`) and be injected into the adapter via constructor — keeping the adapter at max 2 instance variables.
  - [ ] The system prompt must include: full category taxonomy with descriptions, explicit vendor/merchant rules (e.g., "Transferwise → INTERNAL_TRANSFER"), and the INTERNAL_TRANSFER double-counting guardrail.
  - [ ] Unit test `CategorizationPromptProviderTest` verifies the prompt contains all required taxonomy keywords and rules.
  - [ ] `SpringAiCategorizationAdapterTest` is updated to inject the prompt provider mock.
  - [ ] Hexagonal Architecture: no framework annotations in `domain` or `application`; prompt provider is `infrastructure`.
  - [ ] Object Calisthenics: max 2 instance variables per class; no else; no abbreviations; small methods.
  - [ ] All pre-existing tests still pass — `./gradlew test` produces `BUILD SUCCESSFUL`.

---

## Completed Tasks

### Task 2.2 - Structured Output Service
- **Status**: Done ✅
- **Assigned Agent**: Execution Engineer
- **Milestone**: M2
- **EARS**: **While** categorizing transactions, **When** the LLM is queried, **the system shall** use a structured output format (`BeanOutputConverter`) to ensure the response maps strictly to a predefined `Category` enum.
- **Task-Specific DoD**:
  - [x] Define `Category` enum in Domain layer.
  - [x] Define `CategorizationPort` in Application layer.
  - [x] Implement `SpringAiCategorizationAdapter` in Infrastructure layer using Spring AI's structured output.

---

### Task 2.1 - Ollama & Spring AI Integration
- **Status**: Done
- **Assigned Agent**: Execution Engineer
- **Milestone**: M2
- **EARS**: **While** the application is starting, **When** the AI components are initialized, **the system shall** configure a `ChatClient` connected to a local Ollama instance (e.g., `llama3:8b` or `mistral`).


### Task 0.1 - Scaffold Blackboard State
- **Status**: Done
- **Assigned Agent**: Lead Engineer
- **Milestone**: M0
- **EARS**: **While** in the project root, **When** the system is initialized, **the system shall** provide a `state.md` file and a `.agents/templates/state_template.md` blueprint.

### Task 0.2 - Create TASKS.md Ledger Template
- **Status**: Done
- **Assigned Agent**: Lead Engineer
- **Milestone**: M0
- **EARS**: **While** managing tasks, **When** a new requirement is added, **the system shall** record it in `TASKS.md` using EARS syntax and a global DoD.

### Task 0.3 - Configure Lead Engineer Agent
- **Status**: Done
- **Assigned Agent**: Lead Engineer
- **Milestone**: M0
- **EARS**: **While** orchestrating, **When** the Lead Engineer is activated, **the system shall** follow the logic defined in `.agents/lead-engineer/SKILL.md` and utilize the `state_template.md`.

### Task 0.4 - Configure Execution Engineer Agent
- **Status**: Done
- **Assigned Agent**: Lead Engineer
- **Milestone**: M0
- **EARS**: **While** implementing, **When** the Execution Engineer receives a directive, **the system shall** modify the codebase according to `.agents/execution-engineer/SKILL.md`.

### Task 0.5 - Configure Quality Gatekeeper Agent
- **Status**: Done
- **Assigned Agent**: Lead Engineer
- **Milestone**: M0
- **EARS**: **While** validating, **When** artifacts are delivered, **the system shall** execute the review protocol in `.agents/qa-gatekeeper/SKILL.md`.

### Task 1.1 - Source Strategy Implementation
- **Status**: Done
- **Assigned Agent**: Execution Engineer
- **Milestone**: M1
- **EARS**: **While** ingesting data from multiple file types, **When** a source system is provided (Bank/Pluxe/Sheets), **the system shall** select and execute the appropriate parsing strategy to normalize the output.

### Task 1.2 - Unified Domain Modeling
- **Status**: Done
- **Assigned Agent**: Execution Engineer
- **Milestone**: M1
- **EARS**: **While** processing normalized data, **When** a raw transaction is ingested, **the system shall** map it to a pure Domain Entity (`Transaction`) adhering to strict Object Calisthenics and Hexagonal Architecture rules.

### Task 1.2.1 - Domain Mapping Service
- **Status**: Done
- **Assigned Agent**: Execution Engineer
- **Milestone**: M1
- **EARS**: **While** processing ingested data, **When** RawTransactions are provided by the Ingestion Layer, **the system shall** map them to the Domain `Transaction` entity using a dedicated `TransactionMapper`.

### Task 1.3 - Deterministic Idempotency (Hashing)
- **Status**: Done
- **Assigned Agent**: Execution Engineer
- **Milestone**: M1
- **EARS**: **While** processing normalized domain transactions, **When** a transaction is ready to be persisted, **the system shall** generate a `deduplication_id` using a SHA-256 hash of (Date + Amount + RawDescription) to prevent duplicates.

### Task 1.4 - Ingestion Orchestrator
- **Status**: Done
- **Assigned Agent**: Execution Engineer
- **Milestone**: M1
- **EARS**: **While** the system is running, **When** the ingestion flow is triggered, **the system shall** orchestrate the full pipeline: Parse Sources -> Map to Domain -> Generate Deduplication ID.
