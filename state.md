# Ledger-AI Blackboard State

This file serves as the central "Blackboard" for the multi-agent system. Agents read from and write to this file to share context, status, and intermediate artifacts.

## Current State
- **Active Milestone**: M2
- **Active Task**: 2.1
- **Current Phase**: Implementation
- **Assigned Agent**: Lead Engineer

## Blackboard Memory
*Shared context, findings, and temporary data across agents.*

### Context
- Task 1.4 (Ingestion Orchestrator) is complete and merged. Moving to Milestone 2.
- The `spring-ai-ollama-spring-boot-starter` dependency is already present in `build.gradle.kts`.
- `application.yml` already contains Ollama config (base-url, model, temperature).
- **VIOLATION FOUND**: `LuminaAgentApplication.java` currently constructs and uses `ChatClient` directly in a `CommandLineRunner` bean. This leaks AI infrastructure into the application entry point, violating Hexagonal Architecture.
- The goal of this task is to introduce a clean Hexagonal boundary for the LLM interaction.

### Architecture Decisions
- Define a **Port** interface in the `application` package: `LanguageModelPort` — a pure domain-facing contract with no Spring AI types.
- Create an **Adapter** in `infrastructure/agent`: `OllamaLanguageModelAdapter` — implements the port using `ChatClient`.
- Create an **infrastructure config** class `AiConfig` in `infrastructure/config` — declares the `ChatClient` bean with `defaultFunctions("readFile")` for tool support.
- Refactor `LuminaAgentApplication` to inject `LanguageModelPort` instead of using `ChatClient` directly. The interactive `CommandLineRunner` loop should delegate to the port.
- Object Calisthenics: `AiConfig` may only expose the `ChatClient` bean. `OllamaLanguageModelAdapter` has exactly 1 instance variable (`ChatClient`).

### Handshakes & Handoffs
*Used to pass execution control between agents.*

#### Orchestrator (Lead Engineer) -> Execution Engineer
- **Directive**:
    Implement Task 2.1 — Ollama & Spring AI Integration with strict Hexagonal Architecture.

    **Files to Create:**
    1. `src/main/java/com/facundo/lumina/application/LanguageModelPort.java`
       - A pure Java interface (no Spring AI imports).
       - Single method: `String ask(String prompt)`.
    2. `src/main/java/com/facundo/lumina/infrastructure/config/AiConfig.java`
       - `@Configuration` class that declares a `ChatClient` `@Bean` built from `ChatClient.Builder` with `defaultFunctions("readFile")`.
    3. `src/main/java/com/facundo/lumina/infrastructure/agent/OllamaLanguageModelAdapter.java`
       - Implements `LanguageModelPort`.
       - Annotated with `@Service`.
       - Has exactly 1 instance variable: `ChatClient chatClient`.
       - Implements `ask(String prompt)` by calling the ChatClient and returning the content.
    4. `src/test/java/com/facundo/lumina/infrastructure/agent/OllamaLanguageModelAdapterTest.java`
       - Unit test using Mockito to mock `ChatClient` (do NOT use `@SpringBootTest` — keep it a pure unit test).
       - Test: `ask_delegatesToChatClient_returnsContent()` — verify the adapter delegates correctly.

    **Files to Modify:**
    5. `src/main/java/com/facundo/lumina/LuminaAgentApplication.java`
       - Remove the `ChatClient.Builder` injection from the `CommandLineRunner` bean.
       - Inject `LanguageModelPort` instead.
       - Delegate all calls to `languageModelPort.ask(input)`.

    **Constraints:**
    - No Spring AI types (`ChatClient`, `ChatClient.Builder`) in `application` or `domain` packages.
    - `LanguageModelPort` must be a pure Java interface.
    - All Object Calisthenics rules apply: max 2 instance variables per class, no else, one dot per line.

- **Expected Artifacts**:
    - `LanguageModelPort.java`
    - `AiConfig.java`
    - `OllamaLanguageModelAdapter.java`
    - `OllamaLanguageModelAdapterTest.java`
    - Updated `LuminaAgentApplication.java`

#### Execution Engineer -> Quality Gatekeeper
- **Artifacts Delivered**: 
    - Port: [LanguageModelPort.java](file:///Users/facundo.ramallo/Documents/programing/repos/lumina-agent/src/main/java/com/facundo/lumina/application/LanguageModelPort.java)
    - Configuration: [AiConfig.java](file:///Users/facundo.ramallo/Documents/programing/repos/lumina-agent/src/main/java/com/facundo/lumina/infrastructure/config/AiConfig.java)
    - Adapter: [OllamaLanguageModelAdapter.java](file:///Users/facundo.ramallo/Documents/programing/repos/lumina-agent/src/main/java/com/facundo/lumina/infrastructure/agent/OllamaLanguageModelAdapter.java)
    - Unit Test: [OllamaLanguageModelAdapterTest.java](file:///Users/facundo.ramallo/Documents/programing/repos/lumina-agent/src/test/java/com/facundo/lumina/infrastructure/agent/OllamaLanguageModelAdapterTest.java)
    - Updated application: [LuminaAgentApplication.java](file:///Users/facundo.ramallo/Documents/programing/repos/lumina-agent/src/main/java/com/facundo/lumina/LuminaAgentApplication.java)
- **Testing Instructions**: 
    - Run the unit tests to verify full functionality: `./gradlew test --tests "com.facundo.lumina.infrastructure.agent.OllamaLanguageModelAdapterTest"`
    - Run all project tests: `./gradlew test`
- **Proof of Work**: Tests compile and pass cleanly on the mock layer.

#### Quality Gatekeeper -> Orchestrator
- **Approval Status**: [Approved]
- **Feedback/Fixes**: 
    - [Approved] Hexagonal Architecture boundaries are cleanly respected. The Port interface resides in the application layer and has no dependency on Spring AI.
    - The Ollama adapter and Spring configuration reside correctly in the infrastructure packages.
    - Unit tests are mock-based and do not require running a live Ollama instance.
    - All tests compile and pass successfully.

## Blockers / Issues
- None.
