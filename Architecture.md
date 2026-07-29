# AuraCart - System Context & AI Guidelines

## 1. Project Overview
**AuraCart** is an Enterprise-grade, Multi-Tenant SaaS E-commerce platform built with **Java 21** and **Spring Boot 3.x**. It is designed to be highly scalable, secure, and AI-ready.

## 2. Architectural Pattern: Modular Monolith
The application follows a **Modular Monolith** architecture. It runs as a single Spring Boot application but is strictly partitioned into independent modules.
*   **Host Module**: `application` (The entry point that bundles everything).
*   **Core Module**: `core-shared` (Contains global configs, `BaseEntity`, `TenantContext`, security filters, and shared utilities. NO business logic here).
*   **Domain Modules**: `module-iam`, `module-catalog`, `module-inventory`, `module-sales`, `module-notification`.

**Strict Rule**: Modules **MUST NOT** directly access another module's database tables/entities. Tight coupling is strictly prohibited.

## 3. Database Strategy: Database-per-Tenant
We utilize a highly isolated Multi-Tenant data architecture.
*   **Master DB**: Contains ONLY the `tenants` and `tenant_features` tables. It holds connection metadata.
*   **Tenant DB**: Every tenant (customer) has their own physically isolated database schema containing operational data (Products, Orders, Customers, etc.).
*   **Routing mechanism**: HTTP Requests include tenant identifiers (via Headers or JWT). The `TenantInterceptor` extracts this and sets it in a ThreadLocal `TenantContext`. Spring's `AbstractRoutingDataSource` dynamically routes queries to the correct Tenant DB based on this context.

## 4. Inter-Module Communication
*   **Event-Driven**: Modules communicate via Spring Application Events. For example, `module-sales` fires an `OrderCreatedEvent`, which `module-inventory` listens to.
*   **Outbox Pattern**: To guarantee data consistency, asynchronous tasks must use the `outbox_events` table. Domain operations and their resulting events are saved in the same local database transaction. A separate worker processes the outbox.

## 5. Strict Coding Standards & Constraints
When writing code for this project, AI assistants MUST follow these rules:
1.  **BaseEntity**: All JPA Entities must extend a common `@MappedSuperclass` `BaseEntity` located in `core-shared` (which handles `id`, `created_at`, `updated_at`, etc.).
2.  **JPA Auditing**: Use `@CreatedDate`, `@LastModifiedDate`, and `@CreatedBy` annotations. Do not set these fields manually.
3.  **Tenant Context Handling**: Never hardcode tenant IDs. Always rely on `TenantContext.getTenantId()`.
4.  **Soft Deletes**: Use active/inactive flags (e.g., `is_active`) or status Enums instead of hard `DELETE` queries, unless explicitly requested.
5.  **Audit Logs**: Critical changes should be tracked in the `audit_logs` table.
6.  **AI Readiness**: Recognize that vector embeddings (`pgvector`) are used in the DB (`ai_embeddings`, `preferences_embedding`).
7.  **Controller classes must not contain business logic**. They should delegate to Service classes
8.  **Service classes must not contain persistence logic**. They should delegate to Repository classes.
9.  **Repository classes must not contain business logic**. They should only handle data access.
10. **No direct SQL queries** in Service classes. Use Spring Data JPA repositories or custom repository methods.
11. **No cross-module database access**. Services in one module must not query entities from another module's database.
12. **No hardcoded configuration values**. Use `application.yml` or `@Value` injection for all configurable parameters.
13. **No business logic in the `core-shared` module**. It is strictly for shared infrastructure, not domain logic.
14. **No direct access to `TenantContext` in Repository classes**. Repositories should be agnostic of tenant context; the routing is handled by Spring's `AbstractRoutingDataSource`.
15. **No use of `@Transactional` in Controller classes**. Transaction boundaries should be defined in Service classes.
16. **No use of `@Autowired` on fields**. Use constructor injection for all dependencies.
17. **Service methods should validate input parameters** and throw appropriate exceptions (e.g., `IllegalArgumentException`) for invalid data.
18. **All exceptions must be handled gracefully**. Use `@ControllerAdvice` for global exception handling and return meaningful error responses.
19. **All public APIs must be documented** using OpenAPI/Swagger annotations.
20. **Validate methods inside service classes should check for null or invalid inputs and must not check for business rules**. Business rules should be enforced in the service methods themselves, not in validation methods.
21. **If it is mandatory to create an entity class or another class from another dto.If possible use mapper classes to map between them. Do not create entity classes directly from dto classes.**
22. **Use 'var' for object definition if possible. Sample: var product = new CustomType() instead of CustomType product = new CustomType()**
23. **Do not use comments(especially javadoc) a lot in the code. Use comments only if it is necessary to explain a complex logic.**
24. **Distinguish business logics with packages and classes. For example, if you have a business logic related to product, create a package named product and put all the classes related to product in that package.Do this distinction for controllers,dto,entities,services and repositories**
25. **Unless a complex business logic do not write comments.**
26. **Add TODO comments for technical debts or future improvements.**
---

## 6. Single Source of Truth: Database Schema (DBML)
The strict database schema that dictates the domain design is separated to prevent context overload.

**CRITICAL INSTRUCTION FOR AI:**
Whenever you are asked to generate or modify Entities, Repositories, Services, write SQL queries, or make domain-level decisions, you **MUST** read and refer to the `docs/database-schema.dbml` file located in the workspace.