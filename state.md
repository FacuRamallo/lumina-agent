# Ledger-AI Blackboard State

This file serves as the central "Blackboard" for the multi-agent system. Agents read from and write to this file to share context, status, and intermediate artifacts.

## Current State
- **Active Milestone**: M1
- **Active Task**: 1.3
- **Current Phase**: Implementation
- **Assigned Agent**: Quality Gatekeeper

## Blackboard Memory
*Shared context, findings, and temporary data across agents.*

### Context
- Implementation of the Strategy Pattern for financial data ingestion as defined in `docs/specs`.
- Target Stack: Java 21, Spring Boot.

### Architecture Decisions
- Use an interface-based Strategy pattern to allow for easy addition of future parsers (e.g., Google Sheets).
- Leverage Spring's `@Component` and `Map<String, SourceParser>` for automatic strategy registration.

### Handshakes & Handoffs
*Used to pass execution control between agents.*

#### Orchestrator (Lead Engineer) -> Execution Engineer
- **Directive**: 
    1. Implement a hashing utility to generate a deterministic `DeduplicationId`.
    2. Create a `DeduplicationId` value object in `com.facundo.lumina.domain` that wraps a `String` (SHA-256 hash).
    3. The hashing logic should be in a stateless service (e.g., `HashingService` or `DeduplicationService`).
    4. The hash MUST be calculated from: `TransactionDate` + `Amount` + `RawDescription`.
    5. Ensure the input data is normalized before hashing to avoid inconsistencies (e.g., trim strings, consistent date format).
    6. Adhere to **Object Calisthenics**:
        - No classes with >2 instance variables.
        - Wrap the hash primitive.
    7. Follow TDD: Verify that identical transactions produce the same hash and different ones produce different hashes.
- **Expected Artifacts**: 
    - `DeduplicationId.java`
    - `HashingService.java`
    - `HashingServiceTest.java`

#### Execution Engineer -> Quality Gatekeeper
- **Artifacts Delivered**: 
    - Value Object: [DeduplicationId.java](file:///Users/facundo.ramallo/Documents/programing/repos/lumina-agent/src/main/java/com/facundo/lumina/domain/DeduplicationId.java)
    - Hashing Service: [HashingService.java](file:///Users/facundo.ramallo/Documents/programing/repos/lumina-agent/src/main/java/com/facundo/lumina/domain/service/HashingService.java)
    - Unit Tests: [HashingServiceTest.java](file:///Users/facundo.ramallo/Documents/programing/repos/lumina-agent/src/test/java/com/facundo/lumina/domain/service/HashingServiceTest.java)
- **Testing Instructions**: 
    - Run hashing tests: `./gradlew test --tests "com.facundo.lumina.domain.service.HashingServiceTest"`
    - Verify SHA-256 logic: Check that same data produces same hash and different data produces different ones.

#### Quality Gatekeeper -> Orchestrator
- **Approval Status**: Approved
- **Feedback/Fixes**: 
    - Deterministic hashing implementation verified.
    - `DeduplicationId` value object correctly wraps the hash.
    - Object Calisthenics and Hexagonal boundaries maintained.
    - TDD coverage confirmed.

## Blockers / Issues
- None.
