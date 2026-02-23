# System Design Trade-offs & Architectural Decisions

> AERS — Alert Escalation & Resolution System  ·  Spring Boot  ·  Pavan Venkat Kumar

---

## 📋 Overview

This document outlines key architectural decisions and trade-offs made while building AERS.

The goal was to design a **simple, scalable, and maintainable** alert monitoring system that simulates real-world fleet operations while keeping development fast and production-ready.

Trade-offs are evaluated across: **Performance · Simplicity · Scalability · Maintainability · Development Speed**

---

## 🏗️ Architecture Trade-offs

### 1. Monolithic Backend vs Microservices

**✅ Decision: Monolithic Spring Boot Application**

| Pros | Cons (Accepted) |
|------|-----------------|
| Single deployable unit | Cannot scale modules independently |
| Easy debugging & testing | All logic in one backend |
| Faster development | Less suitable for very large scale |
| No service communication overhead | — |
| Perfect for assignment/demo scope | — |

> **Conclusion:** For a case-study scale project, monolithic architecture provides the best speed + simplicity. Microservices are only justified at millions of alerts/day with multiple backend teams.

---

### 2. REST API vs GraphQL

**✅ Decision: REST APIs using Spring Web MVC**

| REST (Chosen) | GraphQL (Rejected) |
|---------------|-------------------|
| Simple and widely understood | Overkill for this scope |
| Easy Postman testing | Adds schema & resolver complexity |
| Clear, predictable endpoints | Harder caching |
| Faster to build | — |

> **Conclusion:** REST is the best fit for backend service APIs and monitoring dashboards at this scale.

---

### 3. MySQL vs NoSQL (MongoDB)

**✅ Decision: MySQL — Relational Database**

| MySQL (Chosen) | MongoDB (Rejected) |
|----------------|-------------------|
| Structured, fixed alert schema | No complex dynamic schema needed |
| Easy aggregation for stats | SQL easier for analytics |
| ACID compliance | — |
| Works seamlessly with Spring Data JPA | — |

> **Conclusion:** Relational DB provides better consistency and simpler analytics queries for structured alert data.

---

### 4. JWT vs Session-Based Authentication

**✅ Decision: JWT Token Authentication**

| JWT (Chosen) | Sessions (Rejected) |
|--------------|---------------------|
| Stateless — no server storage | Requires server-side session store |
| Fast request validation | Extra DB/cache lookup per request |
| Works naturally with REST APIs | Less suited for stateless APIs |
| Easy frontend integration via headers | — |

> **Trade-off accepted:** Token cannot be revoked instantly, must be stored on the client side.

---

## ⚙️ Backend Design Trade-offs

### 5. Layered Architecture vs Single-File Logic

**✅ Decision: Clean Layered Spring Boot Architecture**

```
Controller   →   API endpoints & request handling
Service      →   Business logic, rule engine & @Scheduled jobs
Repository   →   Spring Data JPA database access
Model        →   JPA entity classes
Security     →   JWT utility & manual token validation
```

> Clean separation makes the codebase easy to debug, extend, and explain in interviews.

---

### 6. Scheduled Processing vs Manual/Event-Driven

**✅ Decision: Spring @Scheduled Background Job**

Runs every **60 seconds** inside `AlertService`:
- Checks all unresolved alerts
- Escalates if threshold is breached
- Auto-closes alerts after timeout

> **Trade-off:** Uses CPU cycles every 60s — but adds full automation without external dependencies like Kafka or RabbitMQ.

---

## 📊 Data & Processing Trade-offs

### 7. Real-time Processing vs Scheduled Processing

**✅ Decision: Hybrid Approach**

| Operation | Approach |
|-----------|----------|
| Alert creation | Real-time (on API call) |
| Escalation & auto-close | Scheduled (every 60s) |

> Saves resources by avoiding constant DB polling while keeping alert ingestion instant.

---

### 8. Analytics — Direct DB Query vs Cache (Redis)

**✅ Decision: Direct MySQL Queries — No Redis**

| Direct Query (Chosen) | Redis Cache (Rejected) |
|-----------------------|------------------------|
| Simple implementation | Extra infrastructure dependency |
| Fast enough for demo dataset | Overkill for assignment scale |
| No external service needed | — |

> **Trade-off:** Not optimised for millions of alerts, but perfectly sufficient for demo/assignment scale.

---

## 🎨 Frontend Trade-offs

### 9. React vs Vanilla HTML/CSS/JS

**✅ Decision: HTML + CSS + Vanilla JavaScript**

| Vanilla JS (Chosen) | React (Rejected) |
|---------------------|-----------------|
| Fastest development | Requires build tooling |
| Lightweight, zero dependencies | More complex for small dashboard |
| Easy integration with Spring Boot | Overkill for single-page dashboard |
| Fetch API for backend calls | — |

> **Trade-off:** Less scalable than React, but perfect for a monitoring dashboard demo.

---

## 🔐 Security Trade-offs

### 10. Spring Security vs Custom JWT Validation

**✅ Decision: Custom JWT Validation (Controller-level)**

```java
checkAuth(request)
jwtUtil.validateToken()
```

| Custom JWT (Chosen) | Spring Security (Rejected) |
|---------------------|---------------------------|
| Simple, readable implementation | Complex filter chain configuration |
| Easy to debug | Steeper learning curve |
| Sufficient for assignment scope | Overkill for demo system |

> **Trade-off:** Not as battle-hardened as full Spring Security, but completely sufficient for a demo-scale system with clear, auditable logic.

---

## 🚀 Deployment Trade-offs

### 11. Local vs Cloud Deployment

**✅ Decision: Local Development — Cloud Optional**

```
Spring Boot  →  localhost:8080
MySQL        →  local database
```

Can be deployed to: **Render · Railway · AWS · GCP**

> Cloud deployment skipped to keep focus on core logic within assignment scope and avoid infra costs.

---

## 📈 When This Architecture Should Evolve

| Trigger | Change Needed |
|---------|--------------|
| 1M+ alerts/day | Move to microservices |
| Multiple backend teams | Independent service deployment |
| Dashboard becomes slow | Add Redis caching layer |
| Large-scale UI | Migrate frontend to React |
| Real-time updates needed | Add WebSocket or Kafka streaming |
| High-security production | Upgrade to full Spring Security |

---

## 🎯 Design Philosophy

> **Simplicity first. Scale when needed.**

Every decision in AERS was made to balance:

- ✅ Clean, readable architecture
- ✅ Fast development without sacrificing structure
- ✅ Real-world simulation within assignment constraints
- ✅ Easy to explain, debug, and extend

---



---

*AERS  ·  MoveInSync Intelligent Alert Escalation Case Study  ·  Pavan Venkat Kumar*
