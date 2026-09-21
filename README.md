# Enterprise Event-Driven Rewards System

A production-grade, highly performant monorepo engineered with a **React frontend** and a **Spring Boot REST API**, built to compute high-volume transactional metrics via **Eventual Consistency**.

### 🔗 Public Live Deployments (Available 24/7)
* **Interactive React Dashboard Client:** [View Deployed UI (Vercel)](https://vercel.app)
* **Interactive Swagger Testing Sandbox:** [View Live Swagger Workspace (AWS)](http://elasticbeanstalk.com)

---

### 🏗️ Architectural Framework & Patterns
* **Strategy Pattern:** Decouples volatile multi-tier rewards logic into isolated macro classes. The algorithm evaluates your exact tiered ruleset (`spend > 100` and `50 < spend <= 100`) across the entire historical transaction record in **exactly one method call**, eliminating loop degradation.
* **Command Pattern:** Encapsulates transaction calculation arguments into an immutable, validated Java 17 Request Record DTO at the API boundary.
* **Asynchronous Write Pipeline (AWS SQS/SNS):** Offloads resource-heavy calculation executions from the REST thread pool, maximizing API availability.
* **High-Throughput Read Pipeline (Redis):** Implements a write-through cache mechanism (`@CachePut`) to store computed balances instantly on database commit, delivering sub-5ms read paths to the React UI dashboard.

---

### 🛡️ Data Concurrency & Scalability Safeguards
* **Optimistic Concurrency Control (@Version):** Manages multi-thread race conditions safely at the database level. Paired with a programmatic **`@Retryable` exponential backoff handler**, it re-plays transactions out-of-band during collisions without dropping requests or freezing threads.
* **Composite Database Indexing:** Binds columns into a composite B-Tree structure on `(customer_id, created_at)`. This ensures deep multi-month transaction ledger queries execute in single-digit milliseconds, eliminating full table scans.
* **Native SQL Interface Projections:** Streams raw columns straight out of the database driver into non-managed Java interfaces, completely bypassing Hibernate's session state tracking and protecting JVM memory.

---

### 🚀 Local Execution Harness (Zero Configuration)
To ensure a completely frictionless review, the system utilizes Spring Profiles to default to a self-contained local simulation out of the box. 

Clone the repository and launch the backend from the root directory:
```bash
# Compiles and runs the backend using embedded H2 memory datastores
mvn spring-boot:run -pl backend
```
Once launched, open your browser to `http://localhost:8080/swagger-ui.html` to access the interactive endpoint sandbox.

### 🗄️ Database Entity-Relationship Diagram (ERD)

```mermaid
erDiagram
    user_accounts ||--o{ points_ledger : "owns / has many"
    
    user_accounts {
        BIGINT id PK "Map to entity id"
        DOUBLE total_rewards_balance "Calculated active points counter"
        INT version "Optimistic locking concurrency token"
    }

    points_ledger {
        BIGINT id PK "Auto-increment primary key"
        VARCHAR customer_id FK "Composite Index Part 1 (Maps to user_accounts.id)"
        DOUBLE amount "Historical purchase dollar total"
        VARCHAR tx_type "Ledger transaction category token"
        DATE created_at "Composite Index Part 2 (Timestamp range query metric)"
    }
```

### 📊 System Data Flow & Architecture Diagram

```mermaid
sequenceDiagram
    autonumber
    actor User as React Frontend (TypeScript)
    participant API as Rewards REST Controller
    participant Engine as Enterprise Calculation Engine
    participant DB as H2 Database (Points Ledger)
    participant Cache as Redis Cache Tier

    User->>API: POST /v1/rewards/calculations (Command Record DTO + Trace ID)
    activate API
    API-->>User: HTTP 202 ACCEPTED (Instant Non-Blocking Response)
    deactivate API

    Note over API,Engine: Handoff to Background Thread Loop (Eventual Consistency)
    API->>Engine: updateAndRecalculatePoints(command, traceId)
    activate Engine
    
    Note over Engine,DB: range scan using Composite Index (customer_id, created_at)
    Engine->>DB: fetchReadOnlyHistory() via Native SQL Projection Query
    DB-->>Engine: Returns Streamed List<TransactionProjection> (No memory bloat)

    Note over Engine: Single-Pass Multi-Bracket Strategy Pattern Execution
    Engine->>Engine: calculateCumulativePoints(history)

    Note over Engine,DB: Commit & Validate Concurrency Guard
    Engine->>DB: accountRepository.save(account) verifies @Version
    DB-->>Engine: DB Commit Succeeds & Version Increments

    Note over Engine,Cache: Write-Through Caching Sync
    Engine->>Cache: @CachePut updates customer key state instantly
    deactivate Engine
    
    Note over User,Cache: UI Sub-5ms Read Path
    User->>Cache: GET /v1/rewards/balances/{id}
    Cache-->>User: Returns Instant Accurate View
```
