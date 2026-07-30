# AuraCart

**A Modular-Monolith foundation for building Multi-Tenant SaaS E-Commerce platforms with Spring Boot 4 & Java 21.**

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/build-Maven-blue?logo=apachemaven)](https://maven.apache.org/)
[![Status](https://img.shields.io/badge/status-early--stage%20architecture-yellow)]()
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](LICENSE)

> ⚠️ **Project status:** AuraCart is an **architecture-first, work-in-progress reference project**. It is not a finished product and is not intended for production use as-is. What *is* here has been deliberately designed and implemented to be correct, extensible, and idiomatic — see [Current Status](#-current-status) below for exactly what's done and what's next.

---

## 📌 Why this project exists

Most "multi-tenant e-commerce" tutorials online either:
- fake multi-tenancy with a single shared schema and a `tenant_id` column, or
- skip the hard architectural problems entirely (dynamic datasource routing, module boundaries, async consistency, auditing).

**AuraCart** takes the harder, more realistic path: a true **database-per-tenant** SaaS architecture, built as a **Modular Monolith** so it can be deployed as one application today and split into services later without a rewrite.

This repository is meant to be:
- 📖 A **learning resource** for developers who want to understand how multi-tenant SaaS commerce platforms are actually architected.
- 🧱 A **starter foundation** that other developers can **fork and build on** instead of starting from zero.
- 🧪 A **sandbox** for exploring patterns like the Outbox Pattern, tenant-aware routing datasources, and JPA auditing in a modular codebase.

If you're building (or want to learn how to build) a B2C/B2B SaaS e-commerce platform, fork this repo and use the groundwork already in place.

---

## 🏗️ Architecture

AuraCart follows a **Modular Monolith** pattern: a single deployable Spring Boot application, internally partitioned into strictly isolated modules that communicate only through events and well-defined contracts — never through direct database access.

```
Auracart/
├── application/     → Host module: the runnable Spring Boot entry point
├── core-shared/     → Shared infrastructure ONLY (no business logic)
│                        BaseEntity, JPA auditing, tenant context, outbox core
├── tenant/          → Tenant resolution, registry & routing datasource management
├── catalog/         → First domain module: products, brands, categories, attributes
└── docs/            → Database schema (DBML) — the single source of truth for the domain
```

### Key architectural decisions already implemented

| Concern | Approach | Where |
|---|---|---|
| **Multi-tenancy** | Database-per-tenant. Each tenant gets a physically isolated PostgreSQL schema/database. | `tenant/`, `core-shared/.../database/` |
| **Tenant resolution** | An `X-Tenant-ID` header is resolved by a servlet filter and stored in a `ThreadLocal` `TenantContext`, cleared after every request to avoid leaks. | `tenant/.../filter`, `core-shared/.../context/TenantContext.java` |
| **Dynamic datasource routing** | A custom `AbstractRoutingDataSource` implementation resolves the active tenant's `DataSource` at runtime, with connections built via a `DataSourceFactory`. | `core-shared/.../database/TenantRoutingDataSource.java` |
| **Tenant registry caching** | Tenant metadata is cached with Spring Cache + Redis to avoid hitting the master DB on every request. | `tenant/` |
| **Persistence baseline** | Every entity extends a shared `@MappedSuperclass` `BaseEntity` (UUID id, audit fields, optimistic locking version) — no duplicated boilerplate across modules. | `core-shared/.../entity/BaseEntity.java` |
| **JPA Auditing** | `@CreatedDate` / `@LastModifiedDate` / `@CreatedBy` wired via Spring Data JPA auditing infrastructure. | `core-shared/.../config/JpaAuditingConfig.java` |
| **Async consistency** | Outbox Pattern scaffolding (`outbox_events` table + entity/repository) to guarantee at-least-once event delivery from transactional writes, in preparation for event-driven inter-module communication. | `core-shared/.../outbox/` |
| **Secrets management** | Application configuration is designed around **HashiCorp Vault** for externalized secrets rather than plaintext credentials. | `application/src/main/resources/application.yml` |
| **Layered module design** | Each domain module is internally split into `controller → service → repository`, with DTOs and dedicated mapper classes — controllers hold no business logic, services hold no persistence logic. | `catalog/` |

### Guiding rules for this codebase

These are enforced conventions, not aspirations — they shape every module added to this repo:

1. Modules never access another module's database tables/entities directly — only via events or explicit contracts.
2. All entities extend `BaseEntity`; nobody hand-rolls `id`/`createdAt`/`version` fields.
3. Controllers → Services → Repositories, strictly one direction. No business logic leaks upward or downward.
4. Constructor injection only — no field `@Autowired`.
5. Soft deletes (`is_active` / status enums) instead of hard `DELETE`s.
6. DTO ↔ Entity conversion goes through dedicated mapper classes.
7. No hardcoded config — everything configurable lives in `application.yml` / Vault.

The full standards list lives in [`Architecture.md`](Architecture.md), and the DBML domain model that drives every entity decision is in [`docs/database-schema.dbml`](docs/database-schema.dbml).

---

## 🧰 Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 4.1 (Spring Web, Spring Data JPA, Spring Cache, Spring Cloud Vault)
- **Database:** PostgreSQL (master DB + isolated per-tenant databases), with planned `pgvector` / PostGIS usage for AI features and geo data
- **Caching:** Redis
- **Secrets:** HashiCorp Vault
- **Build:** Maven (multi-module reactor build)
- **Boilerplate reduction:** Lombok

---

## 📂 Current Status

**Be transparent about where this project is:** AuraCart is in its **early foundation phase**. The goal so far has not been to ship features fast, but to get the *hard architectural plumbing* right before layering business logic on top of it.

**✅ Implemented / working today:**
- Multi-module Maven skeleton (`core-shared`, `tenant`, `catalog`, `application`) with correct dependency boundaries.
- Tenant resolution pipeline: header → filter → `ThreadLocal` context → routing datasource.
- Dynamic, per-tenant `DataSource` factory and routing.
- Shared `BaseEntity` + JPA auditing configuration.
- Outbox pattern data model (entity, repository, status enum) ready to back event publishing.
- Redis-backed tenant caching.
- Vault-backed externalized configuration.
- First domain module (**Catalog**): entities, repositories, mappers, services, and admin controllers for **products, brands, categories, and attributes/attribute values**, following the strict layered convention.
- Full target domain model documented as DBML (`docs/database-schema.dbml`) covering catalog, inventory, pricing/promotions, customers, orders, payments, returns, shipments, reviews, and notifications — this is the blueprint the codebase is being built out against.

**🚧 Deliberately not yet built (roadmap):**
- IAM / authentication & authorization (Spring Security integration — `AuditorAwareImpl` currently returns a static placeholder until this lands).
- Inventory, Pricing/Promotions, Orders, Payments, Returns, Shipments, and Notification modules.
- Event-driven inter-module communication consuming the Outbox (a publisher/relay worker).
- API documentation (OpenAPI/Swagger).
- Automated test suite and CI pipeline.
- Tenant provisioning workflow (automated per-tenant database creation).

If you clone this expecting a working online store, you'll be disappointed — that's not the point. If you clone this to see a *deliberately-designed* multi-tenant SaaS foundation you can build a real product on top of, you're in the right place.

---

## 🍴 Fork this project

If you're a developer looking to build a multi-tenant SaaS e-commerce platform, you don't have to solve tenant routing, module boundaries, and the outbox pattern from scratch. **Fork this repository** and use it as your starting point:

- The tenant resolution + routing datasource layer works today and can be reused as-is.
- The module boundary conventions (`controller → service → repository`, mapper-based DTO conversion, shared `BaseEntity`) give you a consistent pattern to extend into new domains (inventory, orders, payments, etc.).
- The DBML schema in `docs/` gives you a complete, realistic domain model to implement against — you don't have to design the data model yourself.

Contributions, forks, and issue reports are welcome. If you extend this into new modules, consider opening a PR — this project intentionally stays open for the community to build on.

---

## 🚀 Getting Started

### Prerequisites
- JDK 21
- Maven (or use the bundled `./mvnw`)
- PostgreSQL (master DB + at least one tenant DB)
- Redis
- HashiCorp Vault (dev mode is fine for local development)

### Build

```bash
./mvnw clean install
```

### Run tests for a single module

```bash
./mvnw -pl core-shared test
```

### Configuration

Runtime configuration (datasource URLs, Vault connection, Redis host, per-tenant datasource mappings) lives in `application/src/main/resources/application.yml`. Update it to match your local environment before running the `application` module.

> Note: as documented in [`AGENTS.md`](AGENTS.md), `./mvnw test` currently has a known module-dependency gap in `core-shared` around servlet/web APIs — this is an open item, not a hidden defect.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) — use it, fork it, build your own SaaS platform on top of it.

