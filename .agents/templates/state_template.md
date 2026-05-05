# Ledger-AI Blackboard State (Template)

This file serves as the central "Blackboard" for the multi-agent system. Agents read from and write to this file to share context, status, and intermediate artifacts.

## Current State
- **Active Milestone**: [Milestone ID]
- **Active Task**: [Task ID]
- **Current Phase**: [Planning | Execution | Testing | Review]
- **Assigned Agent**: [Lead Engineer | Execution Engineer | Quality Gatekeeper]

## Blackboard Memory
*Shared context, findings, and temporary data across agents.*

### Context
- *Insert relevant project context or links to specs here...*

### Architecture Decisions
- *Record any technical decisions made during the current task...*

### Handshakes & Handoffs
*Used to pass execution control between agents.*

#### Orchestrator (Lead Engineer) -> Execution Engineer
- **Directive**: [Detailed instructions for the Execution Engineer]
- **Expected Artifacts**: [List of files or outputs to be created/modified]

#### Execution Engineer -> Quality Gatekeeper
- **Artifacts Delivered**: [List of delivered artifacts with links]
- **Testing Instructions**: [How the QA Gatekeeper should verify the work]

#### Quality Gatekeeper -> Orchestrator
- **Approval Status**: [Pending | Approved | Rejected]
- **Feedback/Fixes**: [Detailed feedback if rejected, or confirmation of approval]

## Blockers / Issues
- *List any active blockers here...*
