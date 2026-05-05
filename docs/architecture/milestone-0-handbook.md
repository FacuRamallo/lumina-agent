# Lumina Agent: Milestone 0 Handbook

This document outlines the core fundamentals of the multi-agent architecture (Blackboard Pattern) and provides the strategic approach for scaling the system.

## 1. Core Fundamentals

### The Blackboard Pattern (`state.md`)
The "Blackboard" is the single source of truth for the project's current state. It eliminates "context drift" by ensuring every agent reads the same directive and posts to the same memory.
- **`state_template.md`**: Ensures that every task starts with a standardized structure, preventing agents from "hallucinating" their own protocols.

### Agent Personas (The "Triple Threat")
1.  **Lead Engineer (Orchestrator)**: Plans the architecture, breaks down tasks, and performs the final audit.
2.  **Execution Engineer (Builder)**: Implements code following **Atomic Execution** and **TDD** rules.
3.  **Quality Gatekeeper (Auditor)**: Enforces **Requirement Traceability** and **Zero Trust Security**.

### Governance Protocols
- **EARS Notation**: Standardized logic for requirements (**While/When/The System Shall**).
- **Zero Trust Security**: Global constraints in `.agents/rules/security.md` to prevent credential leaks and ensure data privacy.
- **Definition of Done (DoD)**: Both Global and Task-specific checklists that must be satisfied before closure.

---

## 2. Strategic Interaction Approach

### The "Vision vs. Execution" Model
The most efficient way to use this system is to delegate **Atomic Planning** to the agents.

#### ❌ The Low-Efficiency Way (Micromanagement)
*Writing a prompt for every file:*
> "Create a Java class named BankCsvParser, add a field for amount, add a method to parse CSV..."

#### ✅ The High-Efficiency Way (Orchestration)
*Providing a Goal and delegating the plan:*
> "Lead Engineer: We are moving to Milestone 1. Please initialize Task 1.1 in the Blackboard based on the `specs`. Define the Strategy Pattern implementation plan and assign it to the Execution Engineer."

### Why this works:
1.  **Consistency**: The Lead Engineer will read your `docs/specs` and `docs/milestones` directly, ensuring the code matches your original vision.
2.  **Autonomy**: You only need to verify the **Directive** in `state.md` before the Execution Engineer starts coding.
3.  **Reliability**: The QA Gatekeeper will catch any deviations from the original plan using the **Traceability Protocol**.

---

## 3. Practical Example: Starting Milestone 1

**Context**: You have defined the need for CSV Parsers in `docs/specs`.

**Step-by-Step Flow**:
1.  **Human**: "Lead Engineer, start Milestone 1, Task 1.1."
2.  **Lead Engineer**: 
    - Copies `state_template.md` to `state.md`.
    - Updates `TASKS.md` with Task 1.1 using EARS.
    - Writes a Directive in `state.md`: *"Implement BankCsvParser using the Strategy Pattern."*
3.  **Execution Engineer**:
    - Creates tests first (TDD).
    - Writes the Java implementation.
    - Posts "Proof of Work" to `state.md`.
4.  **Quality Gatekeeper**:
    - Verifies the code matches the EARS requirement.
    - Confirms No PII is logged (Security Rule).
    - Approves the task.
5.  **Lead Engineer**:
    - Closes Task 1.1 and moves it to "Completed Tasks".
    - Presents the result to the **Human**.

## 4. Conclusion
By treating the agents as a high-seniority engineering team rather than "code generators," you maximize throughput and minimize technical debt. Your role is now to **review the Blackboard**, not to write the code.
