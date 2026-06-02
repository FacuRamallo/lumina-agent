# Ledger-AI Blackboard State

This file serves as the central "Blackboard" for the multi-agent system. Agents read from and write to this file to share context, status, and intermediate artifacts.

## Current State
- **Active Milestone**: M2
- **Active Task**: 2.3
- **Current Phase**: QA Review
- **Assigned Agent**: Quality Gatekeeper

## Blackboard Memory
*Shared context, findings, and temporary data across agents.*

### Context
- Task 2.2 (Structured Output Service) is complete and merged. The system has `Category` enum, `CategorizationPort`, and `SpringAiCategorizationAdapter` using `BeanOutputConverter`.
- The current `SYSTEM_PROMPT` is an inline `static final` constant in `SpringAiCategorizationAdapter`. It is minimal — only 6 lines with no vendor-specific rules, no taxonomy descriptions, and no categorization guidance beyond the enum names.
- Task 2.3 externalizes and enriches this prompt into its own dedicated class, keeping `SpringAiCategorizationAdapter` clean and testable.

### Architecture Decisions
- Create `CategorizationPromptProvider` (a `@Component`) in `com.facundo.lumina.infrastructure.agent`.
  - Single responsibility: own the full prompt string.
  - Exposes one method: `String prompt()`.
- Refactor `SpringAiCategorizationAdapter` to inject `CategorizationPromptProvider` via constructor.
  - Remove the `static final SYSTEM_PROMPT` constant from the adapter.
  - **Constraint**: the adapter must still have exactly 2 instance variables (`chatClient`, `converter`). To honour this, the `CategorizationPromptProvider` **replaces** `converter` as the second variable — `converter` becomes a local variable constructed inside `categorize()`, OR the provider replaces the need to hold converter as a field.
  - **Revised decision**: Keep `chatClient` and `promptProvider` as the 2 instance variables. Construct `BeanOutputConverter` locally inside `categorize()` — this is acceptable as it is stateless and cheap.
- The prompt must be rich: taxonomy table, vendor rules, guardrail for INTERNAL_TRANSFER.

### Handshakes & Handoffs
*Used to pass execution control between agents.*

#### Orchestrator (Lead Engineer) -> Execution Engineer
- **Directive**:
    Implement Task 2.3 — System Prompt Engineering.

    **Files to Create:**
    1. `src/main/java/com/facundo/lumina/infrastructure/agent/CategorizationPromptProvider.java`
       - Annotate with `@Component`.
       - Single public method: `String prompt()`.
       - Must return a rich, multi-section system prompt containing:
         - **Role definition**: "You are a financial transaction categorizer for a personal finance system."
         - **Taxonomy table** — one entry per category with description and examples:
           - `GROCERIES`: Supermarkets, food delivery, bakeries (e.g., Mercadona, Carrefour, Glovo).
           - `UTILITIES`: Electricity, water, gas, internet, phone bills (e.g., Endesa, Vodafone, Orange).
           - `ENTERTAINMENT`: Streaming, cinema, games, subscriptions (e.g., Netflix, Spotify, Steam).
           - `TRANSPORT`: Public transit, taxis, ride-sharing, fuel, parking (e.g., Renfe, Uber, Cabify, BP).
           - `INTERNAL_TRANSFER`: Transfers between own accounts to avoid double-counting (e.g., Bizum between own accounts, Transferwise/Wise, OCU inversiones, broker deposits).
           - `UNKNOWN`: Use only when no other category clearly applies.
         - **Vendor rules section** — explicit merchant-to-category mappings:
           - Transferwise / Wise → INTERNAL_TRANSFER
           - Bizum (when transferring to self) → INTERNAL_TRANSFER
           - OCU → INTERNAL_TRANSFER
           - Netflix → ENTERTAINMENT
           - Spotify → ENTERTAINMENT
           - Mercadona → GROCERIES
         - **INTERNAL_TRANSFER guardrail**: "Any movement of money between accounts you own — regardless of the description — MUST be classified as INTERNAL_TRANSFER to avoid double-counting in budget reports."
         - **Output format instruction**: "Return only valid JSON. No markdown, no extra text. Example: {\"category\": \"GROCERIES\"}"

    **Files to Modify:**
    2. `src/main/java/com/facundo/lumina/infrastructure/agent/SpringAiCategorizationAdapter.java`
       - Remove the `static final SYSTEM_PROMPT` constant.
       - Change constructor to accept `ChatClient` and `CategorizationPromptProvider`.
       - Instance variables become: `chatClient`, `promptProvider` (exactly 2).
       - Inside `categorize()`: construct `BeanOutputConverter<CategoryResponse>` as a local variable.
       - Call `chatClient.prompt().system(promptProvider.prompt()).user(...).call().content()`.
    3. `src/test/java/com/facundo/lumina/infrastructure/agent/SpringAiCategorizationAdapterTest.java`
       - Update to mock `CategorizationPromptProvider` and inject it into the adapter.
       - Existing test scenarios (GROCERIES, INTERNAL_TRANSFER, UNKNOWN, ENTERTAINMENT, TRANSPORT) must all still pass.

    **Files to Create (Tests):**
    4. `src/test/java/com/facundo/lumina/infrastructure/agent/CategorizationPromptProviderTest.java`
       - Unit test (no mocks needed — it's a pure value object).
       - Assert that `prompt()` contains: "GROCERIES", "UTILITIES", "ENTERTAINMENT", "TRANSPORT", "INTERNAL_TRANSFER", "UNKNOWN", "Transferwise", "double-counting", "valid JSON".

    **Constraints:**
    - Strict Hexagonal Architecture: `CategorizationPromptProvider` belongs to `infrastructure`. No new `domain` or `application` files.
    - Object Calisthenics: max 2 instance variables in `SpringAiCategorizationAdapter`; no else keyword; no abbreviations; methods must be small.
    - Do NOT add the `BeanOutputConverter` as an instance variable on the adapter.

- **Expected Artifacts**:
    - `CategorizationPromptProvider.java`
    - `SpringAiCategorizationAdapter.java` (modified)
    - `SpringAiCategorizationAdapterTest.java` (modified)
    - `CategorizationPromptProviderTest.java`

#### Execution Engineer -> Quality Gatekeeper
- **Artifacts Delivered**:
    - `src/main/java/com/facundo/lumina/infrastructure/agent/CategorizationPromptProvider.java` — New `@Component`. Single `prompt()` method returning a rich multi-section system prompt: taxonomy table (6 categories with descriptions and examples), vendor-to-category rules (13 mappings), INTERNAL_TRANSFER double-counting guardrail, and JSON output format instruction. Zero instance variables.
    - `src/main/java/com/facundo/lumina/infrastructure/agent/SpringAiCategorizationAdapter.java` — Refactored: removed `static final SYSTEM_PROMPT` constant. Constructor now accepts `ChatClient` and `CategorizationPromptProvider` (exactly 2 instance variables). `BeanOutputConverter` constructed locally inside `categorize()` — stateless and correct.
    - `src/test/java/com/facundo/lumina/infrastructure/agent/SpringAiCategorizationAdapterTest.java` — Updated: `CategorizationPromptProvider` is now mocked and injected. All 5 original category scenarios (GROCERIES, INTERNAL_TRANSFER, UNKNOWN, ENTERTAINMENT, TRANSPORT) preserved and passing.
    - `src/test/java/com/facundo/lumina/infrastructure/agent/CategorizationPromptProviderTest.java` — New test class. 4 tests verifying: all 6 category names present, vendor rules present (Transferwise, Mercadona, Netflix, Spotify), double-counting guardrail present, valid JSON instruction present.
- **Testing Instructions**: Run `./gradlew test` from project root.
- **Proof of Work**: `BUILD SUCCESSFUL in 10s` — all tasks executed: compileJava, compileTestJava, test (5 actionable tasks, no failures).

#### Quality Gatekeeper -> Orchestrator
- **Approval Status**: ✅ Approved
- **Feedback/Fixes**: N/A — all DoD criteria met.

##### Traceability Report
| Directive Item | Artifact | Verdict |
|---|---|---|
| `CategorizationPromptProvider` `@Component` with `prompt()` | `CategorizationPromptProvider.java` | ✅ `@Component`, single `prompt()` method, zero instance variables |
| Taxonomy table — 6 categories with descriptions and examples | `CategorizationPromptProvider.java` | ✅ Markdown table with all 6 categories, descriptions, and real-world examples |
| Vendor-to-category rules (≥13 mappings) | `CategorizationPromptProvider.java` | ✅ 13 explicit merchant rules in dedicated "Vendor Rules" section |
| INTERNAL_TRANSFER double-counting guardrail | `CategorizationPromptProvider.java` | ✅ Explicit guardrail paragraph with "doubt → INTERNAL_TRANSFER" instruction |
| Output format instruction ("valid JSON") | `CategorizationPromptProvider.java` | ✅ "Return only valid JSON. No markdown, no extra text." with example |
| Adapter refactored: inject `promptProvider`, exactly 2 instance vars | `SpringAiCategorizationAdapter.java` | ✅ `chatClient` + `promptProvider`; `BeanOutputConverter` is a local variable in `categorize()` |
| Remove `static final SYSTEM_PROMPT` constant from adapter | `SpringAiCategorizationAdapter.java` | ✅ Constant removed; prompt delegated to provider |
| `SpringAiCategorizationAdapterTest` updated with mock provider | `SpringAiCategorizationAdapterTest.java` | ✅ `CategorizationPromptProvider` mocked and injected; all 5 scenarios pass |
| `CategorizationPromptProviderTest` — 4 content assertions | `CategorizationPromptProviderTest.java` | ✅ All 6 categories, vendor rules, guardrail, and JSON format verified |

##### Global DoD Checklist
- [x] Code compiles without errors — `BUILD SUCCESSFUL`
- [x] All tests pass — 0 failures
- [x] **Hexagonal Architecture** — both new/modified files are in `infrastructure`; `domain` and `application` untouched
- [x] **SOLID** — SRP: provider owns only the prompt string; adapter only orchestrates the LLM call; DIP via `CategorizationPort`
- [x] **Object Calisthenics** — `SpringAiCategorizationAdapter`: exactly 2 instance variables; `CategorizationPromptProvider`: 0 instance variables; no `else` keywords; no abbreviations; all methods are small and single-purpose
- [x] **Clean Code** — intent-revealing names; prompt sections use markdown headers for clarity; no unnecessary comments
- [x] **No scope creep** — exactly the 4 files from the directive were created/modified; no unrelated changes
- [x] No regression — all pre-existing tests still pass

## Blockers / Issues
- None.

