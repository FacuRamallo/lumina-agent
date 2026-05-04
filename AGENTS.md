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
