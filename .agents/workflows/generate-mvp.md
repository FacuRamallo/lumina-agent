# Workflow: Generate MVP (Lumina Agent)

This workflow defines the automated pipeline for building the Lumina Agent financial system.

## Phase 1: Planning (Lead Engineer)
1.  **Requirement Analysis**: Read `docs/specs` and `docs/milestones`.
2.  **Task Breakdown**: Identify the next atomic task and update `TASKS.md` using EARS notation.
3.  **Blackboard Initialization**: Initialize `state.md` using `.agents/templates/state_template.md`.
4.  **Directive Issuance**: Write the technical directive and expected artifacts for the Execution Engineer.

## Phase 2: Implementation (Execution Engineer)
1.  **Test Scaffolding**: Create unit/integration tests for the directive.
2.  **Atomic Coding**: Modify the codebase to satisfy the directive.
3.  **Local Verification**: Run `./gradlew test` and ensure 100% pass rate.
4.  **Handoff**: Update `state.md` with links to artifacts and test results.

## Phase 3: Verification (Quality Gatekeeper)
1.  **Traceability Audit**: Map every code change back to the EARS requirement.
2.  **Security Check**: Validate against `.agents/rules/security.md`.
3.  **Verdict**: Post `Approved` or `Rejected` in `state.md` with numbered feedback.

## Phase 4: Integration & Closure (Lead Engineer)
1.  **Post-Review Audit**: Verify the quality gate verdict.
2.  **Ledger Sync**: Move the task to "Completed Tasks" in `TASKS.md`.
3.  **Handoff to Next Cycle**: Identify the next task in the pipeline or close the milestone if all tasks are Done.

## Escalation Logic
- **If QA Rejects**: Lead Engineer refines the directive and restarts Phase 2.
- **If Blocked**: Lead Engineer records the blocker in `state.md` and pauses execution for human intervention.
