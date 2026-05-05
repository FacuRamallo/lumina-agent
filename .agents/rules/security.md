# Global Security Rules (Zero Trust)

All agents must adhere to these security constraints at all times. Failure to comply results in automatic rejection by the Quality Gatekeeper.

## 1. Secret Management
- **No Hardcoding**: Never hardcode API keys, database credentials, or private tokens in the source code.
- **Environment Variables**: Use `.env` files or system environment variables. All `.env` files must be listed in `.gitignore`.
- **Credential Leak Prevention**: Before posting artifacts to `state.md`, ensure no secrets are visible in logs or code snippets.

## 2. Data Privacy
- **PII Protection**: Personally Identifiable Information (PII) must never be logged or used in test cases. Use mock data instead.
- **Financial Integrity**: When handling bank descriptions or amounts, ensure the logic prevents "Double Counting" and maintains transactional idempotency.

## 3. Infrastructure & Dependencies
- **Dependency Audit**: Only use approved libraries. Any new dependency must be reviewed for known vulnerabilities (CVEs).
- **Local-First Processing**: Favor local LLM processing (Ollama) over external APIs for sensitive financial data to ensure data residency.

## 4. Input Validation (Zero Trust)
- **Sanitization**: All inputs from external sources (CSVs, APIs, User Input) must be sanitized and validated against a schema before processing.
- **Fail-Safe**: If a security constraint is violated, the system must fail gracefully and log a security alert instead of continuing execution.
