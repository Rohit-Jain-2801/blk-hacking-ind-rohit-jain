# Automated Retirement Savings API

## Overview

This project implements a production-grade API for an automated retirement savings system based on expense-based micro-investments. The system analyzes a series of user expenses, applies a "round-up" strategy (to the nearest multiple of 100), and calculates the investable "remanent".

It is designed to handle complex temporal constraints, validate financial transactions, and project investment returns across different vehicles (NPS and Index Funds) while accounting for inflation and tax benefits.

## Challenge Objectives

*   **Micro-savings Logic**: Round up expenses to the next multiple of 100. The difference (remanent) is invested.
*   **Temporal Constraints**:
    *   **q moments**: Periods where a fixed investment amount overrides the calculated remanent.
    *   **p moments**: Periods where an extra amount is added to the remanent.
    *   **k periods**: Specific evaluation ranges for grouping investments.
*   **Investment Projections**: Calculate compound interest for NPS (7.11%) and Index Funds (14.49%), adjusted for inflation.

## Tech Stack

*   **Language**: Java 17+
*   **Framework**: Spring Boot
*   **Containerization**: Docker
*   **Build Tool**: Maven/Gradle

---

## API Reference

Base URL: `/blackrock/challenge/v1`

### 1. Transaction Builder
**Endpoint**: `POST /transactions:parse`

Enriches a list of expenses with `ceiling` (next multiple of 100) and `remanent` (investable amount) fields.

**Input Example**:
```json
[
  { "date": "2023-10-12 20:15:30", "amount": 250 }
]
```

### 2. Transaction Validator
**Endpoint**: `POST /transactions:validator`

Validates transactions based on wage and maximum investment constraints. Returns lists of valid and invalid transactions.

**Input Example**:
```json
{
  "wage": 50000,
  "transactions": [ ... ]
}
```

### 3. Temporal Constraints Validator
**Endpoint**: `POST /transactions:filter`

Filters and modifies transactions based on `q` (fixed override), `p` (extra addition), and `k` (grouping) periods.

**Input Example**:
```json
{
  "q": [{ "fixed": 0, "start": "...", "end": "..." }],
  "p": [{ "extra": 25, "start": "...", "end": "..." }],
  "k": [{ "start": "...", "end": "..." }],
  "transactions": [ ... ]
}
```

### 4. Returns Calculation
**Endpoints**: 
*   `POST /returns:nps`
*   `POST /returns:index`

Calculates the final investment value, tax benefits (for NPS), and inflation-adjusted real returns.

### 5. Performance Report
**Endpoint**: `GET /performance`

Returns system metrics including response time, memory usage, and thread count.

---

## Deployment & Docker

Per the challenge requirements, the application is containerized to run on port **5477**.

### Prerequisites
*   Docker installed on your machine.

### Build and Run

1.  **Build the Docker Image**
    The image name follows the convention `blk-hacking-ind-{name-lastname}`.

    ```bash
    docker build -t blk-hacking-ind-rohit-jain .
    ```

2.  **Run the Container**
    The application runs on port 5477 inside the container and must be mapped to port 5477 on the host.

    ```bash
    docker run -d -p 5477:5477 blk-hacking-ind-rohit-jain
    ```

### Docker Configuration Details
*   **Base OS**: Linux (Alpine/Debian based OpenJDK image).
*   **Port**: Exposes `5477`.

---

## Development Setup

### Local Execution
To run the application locally without Docker:

```bash
./mvnw spring-boot:run
```
*Note: Ensure `server.port=5477` is set in `application.properties` to match the Docker configuration, or adjust your requests accordingly.*

### Testing
Tests are located in the `test` folder.

*   **Unit Tests**: Validate logic for rounding, q/p/k rule application, and financial formulas.
*   **Integration Tests**: Verify API endpoints and JSON serialization.

To run tests:
```bash
./mvnw test
```

---

## Business Rules & Logic

### Rounding Strategy
For an expense $x$:
*   `Ceiling` = Next multiple of 100 (e.g., 150 -> 200, 200 -> 300).
*   `Remanent` = Ceiling - Amount.

### Period Rules
1.  **q Period (Fixed)**: If a transaction falls within a `q` range, the remanent is replaced by the `fixed` amount.
    *   *Conflict Rule*: If multiple `q` periods match, use the one starting latest.
2.  **p Period (Extra)**: If a transaction falls within a `p` range, add `extra` amount to the remanent.
    *   *Accumulation*: Multiple `p` periods stack (sum their extras).
    *   *Order*: `p` rules apply after `q` rules.
3.  **k Period (Grouping)**: Used to sum remanents within specific date ranges for reporting.

### Financial Formulas
*   **Compound Interest**: $A = P(1 + \frac{r}{n})^{nt}$
*   **Inflation Adjustment**: $A_{real} = \frac{A}{(1 + inflation)^t}$
*   **NPS Tax Benefit**: $Min(Invested, 10\% \text{ of Annual Income}, 200000)$

---

## Contact
**Developer**: Rohit Jain
```