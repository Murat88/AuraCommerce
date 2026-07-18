# Auracart Agent Guide

## Project shape
- Maven multi-module project on Spring Boot 4.1 / Java 21.
- The only module in this workspace is `core-shared/`; it is shared infrastructure, not a runnable app.
- Root `pom.xml` owns module registration and the Java version; add any future modules there.

## Core architecture
- Tenant flow is `TenantInterceptor` -> `TenantContext` in `core-shared/src/main/java/com/auracart/core/context/`.
- `TenantInterceptor` reads `X-Tenant-ID`; blank or missing values fall back to `TenantContext.DEFAULT_TENANT_ID` (`public`).
- `TenantContext` stores the tenant in a `ThreadLocal`; always clear it after request handling to avoid leaks.
- JPA audit/version defaults live in `core-shared/src/main/java/com/auracart/core/entity/BaseEntity.java`.
- `JpaAuditingConfig` enables Spring Data auditing, and `AuditorAwareImpl` currently returns `"system"` until Spring Security is wired in.

## Coding conventions
- Keep code under the existing `com.auracart.core.*` package namespace.
- Shared entity classes should extend `BaseEntity` instead of duplicating UUID/audit/version fields.
- Lombok is used for boilerplate reduction; preserve the existing annotation-based style.
- Comments in this module are often Turkish; keep nearby comments consistent unless you are rewriting the block.

## Developer workflow
- Run checks from the repo root with `./mvnw test`.
- When working only on the shared module, `./mvnw -pl core-shared test` keeps the scope narrow.
- After fixing a module failure, Maven can resume with `./mvnw -rf :core-shared`.

## Current build caveat
- `./mvnw test` currently fails in `core-shared` because `TenantInterceptor` depends on servlet/web APIs and `TenantContext` relies on Lombok-generated logging; re-check module dependencies when editing those files.

## Where to look first
- `pom.xml` for module composition and build defaults.
- `core-shared/pom.xml` for shared dependencies.
- `core-shared/src/main/java/com/auracart/core/context/` for tenant handling.
- `core-shared/src/main/java/com/auracart/core/config/` for auditing setup.
- `core-shared/src/main/java/com/auracart/core/entity/BaseEntity.java` for the persistence baseline.
