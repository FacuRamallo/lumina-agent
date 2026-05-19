# Lumina Agent — Multi-Agent System

## Overview
This repository implements a multi-agent architecture based on the **Blackboard Pattern** to orchestrate an AI-powered financial intelligence system. Three specialized agents collaborate through a shared state file (`state.md`) to plan, build, and validate features.

---

## Agents

### 1. Lead Engineer (Orchestrator)
**File**: `.agents/lead-engineer/SKILL.md`

**Activation**: Activate when you need to plan a new task, interpret specs, or orchestrate a handoff.

**Prompt Prefix**:
> "You are the **Lead Engineer**. Follow your SKILL.md at `.agents/lead-engineer/SKILL.md`. Your current task is:"

---

### 2. Execution Engineer
**File**: `.agents/execution-engineer/SKILL.md`

**Activation**: Activate when the Lead Engineer has filled out the directive in `state.md` and you need to implement a feature.

**Prompt Prefix**:
> "You are the **Execution Engineer**. Follow your SKILL.md at `.agents/execution-engineer/SKILL.md`. The current task directive is in `state.md`."

---

### 3. Quality Gatekeeper
**File**: `.agents/qa-gatekeeper/SKILL.md`

**Activation**: Activate after the Execution Engineer completes a task and updates `state.md` with delivered artifacts.

**Prompt Prefix**:
> "You are the **Quality Gatekeeper**. Follow your SKILL.md at `.agents/qa-gatekeeper/SKILL.md`. Review the artifacts listed in `state.md` against the DoD in `TASKS.md`."

---

## Key Files

| File | Purpose |
|------|---------|
| `state.md` | Blackboard — shared state, memory, and agent handoffs |
| `TASKS.md` | Task ledger — EARS requirements and Definitions of Done |
| `docs/milestones` | High-level milestones and project roadmap |
| `docs/specs` | Technical specifications and PRD |

---

## Development Guardrails (Global DoD)

To ensure high maintainability and robust code quality, the following paradigms are strictly enforced as part of the Definition of Done (DoD) for **every task**:

1. **Hexagonal Architecture (Ports & Adapters)**: Code must enforce strict boundaries and be structured exactly into three top-level packages:
   - `domain`: Contains entities, value objects, domain events, and core business logic. It must be completely isolated from external frameworks (e.g., Spring annotations, databases, external libraries).
   - `application`: Contains use cases, domain orchestrators, and application services that coordinate business flows without framework-specific implementation logic.
   - `infrastructure`: Contains adapters, configuration classes, external service integrations, tool integrations (e.g. CLI tools), and framework-specific code.
   Interfaces (Ports) and Implementations (Adapters) must be explicitly separated, with adapters residing in `infrastructure`.
2. **SOLID Principles**: All code must comply with Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion.
3. **Clean Code Principles**: Code must be self-documenting, readable, and intent-revealing. Keep functions small, use meaningful names, and avoid unnecessary comments.
4. **Object Calisthenics**: Strict object-oriented rules apply (e.g., Only one level of indentation per method, Don't use the ELSE keyword, Wrap all primitives and strings, First-class collections, One dot per line, Don't abbreviate, Keep entities small, No classes with >2 instance variables, No getters/setters/properties).

These guardrails are non-negotiable. The Lead Engineer must include them in task planning, the Execution Engineer must develop accordingly, and the Quality Gatekeeper must explicitly verify them before approval.

---

## Workflow

```
Lead Engineer
    │ (writes directive + DoD to state.md & TASKS.md)
    ▼
Execution Engineer
    │ (implements artifacts, updates state.md with deliverables)
    ▼
Quality Gatekeeper
    │ (reviews DoD, approves or rejects with feedback)
    ▼
Lead Engineer (next cycle or task escalation)
```

---

## Automation Rules

1. **Branch naming**: `feature/task-{id}-{short-description}` (e.g., `feature/task-0.1-scaffold-blackboard`)
2. **Commit convention**: `feat|fix|chore|refactor: {description}` (Conventional Commits)
3. **PR requirement**: Every task must have a corresponding PR linking to the task ID.
4. **State sync**: `state.md` must be updated at every agent handoff — never leave it stale.
5. **DoD gate**: No code is merged without QA Gatekeeper approval (Approved status in `state.md`).
