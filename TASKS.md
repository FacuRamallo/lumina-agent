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

---

### Task 1.3 - Deterministic Idempotency (Hashing)
- **Status**: To Do
- **Assigned Agent**: Execution Engineer
- **Milestone**: M1

#### EARS Requirement
* **While** processing normalized domain transactions 
* **When** a transaction is ready to be persisted 
* **The system shall** generate a `deduplication_id` using a SHA-256 hash of (Date + Amount + RawDescription) to prevent duplicates.

#### Task-Specific DoD
- [x] Implement `DeduplicationService` or utility class in `domain.service`.
- [x] Hash includes normalized Date, Amount, and RawDescription.
- [x] Unit tests verifying hash consistency and collision avoidance.
- [x] Adhere to Object Calisthenics (stateless, use `DeduplicationId` value object).

### Task [ID] - [Task Name]
- **Status**: [To Do | In Progress | In Review]
- **Assigned Agent**: [Agent Name]
- **Milestone**: [Milestone Reference]

#### EARS Requirement
* **While** [precondition] 
* **When** [trigger] 
* **The system shall** [system response]

#### Task-Specific DoD
- [ ] [Custom criteria...]

---

## Completed Tasks

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
