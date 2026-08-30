# PowerWP4j

[![CI](https://github.com/Urbine/PowerWP4j/actions/workflows/maven.yml/badge.svg)](https://github.com/Urbine/PowerWP4j/actions/workflows/maven.yml)
![Java](https://img.shields.io/badge/java-21-blue)
[![License](https://img.shields.io/badge/license-Apache--2.0-orange)](https://www.apache.org/licenses/LICENSE-2.0)
![Code Style](https://img.shields.io/badge/code_style-Google%20Java-blueviolet)
![Status](https://img.shields.io/badge/status-alpha-orange)
[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://urbine.github.io/PowerWP4j)

A modern Java toolkit for WordPress automation and offline content analysis. Build, update, and analyze WordPress content with a type-safe REST client, incremental caching, and powerful analysis utilities—all designed with expressive Java idioms and testable abstractions.

## Table of Contents

1. [Features](#features)
2. [Requirements](#requirements)
3. [Installation](#installation)
4. [Quickstart](#quickstart)
5. [Use Cases](#use-cases)
6. [Key Packages](#key-packages)
7. [Extensibility](#extensibility)
8. [FAQs](#faqs)
9. [Development](#development)
10. [License](#license)

## Features

| Capability              | Description                                                                                           |
| ----------------------- | ----------------------------------------------------------------------------------------------------- |
| **REST Client**         | Create, update, delete posts, categories, tags, and media using Application Password auth             |
| **Local Cache**         | Fetch WordPress posts into a JSON file with metadata; supports incremental sync via WordPress headers |
| **Offline Analysis**    | Query the cache without HTTP calls—counts, sets, snapshots of posts, slugs, tags, categories, GUIDs   |
| **Taxonomy Extraction** | Extract and aggregate taxonomy data for automation, reporting, or ML workflows                        |
| **TLS / SSL**           | `SSLContexts` factory for default, `TrustManager[]`, or `KeyStore`-backed `SSLContext`                |

**Design philosophy**: Expressive, declarative modern Java (immutable value types, `Optional`, streams, immutability-first) with documented nullability. Alpha status—API may evolve.

> **Note (pre-release users, `main` → `0.1.0`):** The API has evolved for the first release: `WPSiteInfo` now takes a `java.net.URI` (`wp.baseURI` / `WP_BASE_URI`); `fromConfigResource` / `fromEnv` throw `LocalConfigurationException` instead of returning `Optional`; `WPRestClient` / `WPCacheManager` now configure TLS via `javax.net.ssl.SSLContext` (see `SSLContexts`); `WPRestClient` methods return `@Nullable HttpResponse<String>`; `WPCacheManager` uses `fetchCache` / `fetchCacheFromInstancePath` and `cacheSync()` is parameterless. See Quickstart for the current API.

## Requirements

- **JDK 21**
- A WordPress site with:
  - REST API enabled (`/wp-json/wp/v2/...`)
  - An **Application Password** (WP Admin → Users → Profile → Application Passwords)
- Site base URI including scheme, e.g. `https://example.com` or `https://localhost:8443` (port is preserved when building `https://<host>[:port]/wp-json/wp/v2`)

## Installation

### Build & install locally

```bash
mvn clean install
```

### Dependency Management

> `[VERSION]` refers to the current stable release.

#### Maven

```xml
<dependency>
  <groupId>net.ygbstudio</groupId>
  <artifactId>powerwp4j</artifactId>
  <version>[VERSION]</version>
</dependency>
```

#### Gradle

```gradle
dependencies {
    implementation 'net.ygbstudio:powerwp4j:[VERSION]'
}
```

#### JitPack (Alternative)

You can also fetch the project via [JitPack](https://jitpack.io/#Urbine/PowerWP4j).

### Runtime dependencies (minimal)

- **Jackson 3.x** — JSON processing
- **Apache Tika Core** — MIME type detection
- **Apache Commons Lang3** — String utilities
- **SLF4J API** — Logging abstraction (no implementation forced)

## Quickstart

### 1. Configure site info

**Properties file** (`<your-config-file>.properties` on classpath):

```properties
wp.baseURI=https://example.com
wp.user=my_username
wp.appPass=xxxx xxxx xxxx xxxx
```

```java
WPSiteInfo siteInfo =
    WPSiteInfo.fromConfigResource(
        "my-config-file.properties"
    );
// throws LocalConfigurationException if the resource
// is missing or any of wp.baseURI, wp.user, wp.appPass is absent
```

**Environment variables**:

```bash
export WP_BASE_URI=https://example.com \
       WP_USER=my_username \
       WP_APP_PASS='xxxx xxxx xxxx'
```

```java
WPSiteInfo siteInfo =
    WPSiteInfo.fromEnv();
// throws LocalConfigurationException if any of
// WP_BASE_URI, WP_USER, WP_APP_PASS is unset
```

> Former keys `wp.fqdn` / `WP_FQDN` were renamed to `wp.baseURI` / `WP_BASE_URI` and now require a full URI with scheme (and optional port). `WPSiteInfo` is an immutable `final class` holding a `java.net.URI`; `apiBaseUrl()` derives `scheme://host[:port]/wp-json/wp/v2`.

### 2. Create a post

```java
var payload =
    WPBasicPayloadBuilder.builder()
        .title("Hello from PowerWP4j")
        .status(WPStatus.DRAFT)
        .type(WPPostType.POST)
        .slug("hello-powerwp4j")
        .content("Created via WP REST API")
        .build();

WPRestClient client =
    WPRestClient.of(siteInfo);
// For specific dev setups (e.g., self-signed certificates):
// SSLContext customContext =
//     SSLContexts.withTrustStore(myKeyStore);
// WPRestClient localClient =
//     WPRestClient.of(siteInfo, customContext);

HttpResponse<String> response =
    client.createPost(payload);
// @Nullable — null if the request could not be executed
// throws WPRequestException on I/O / interruption
// throws InvalidApiUrlException if the URL is malformed
```

### 3. Upload media

```java
// Optional: pass WPMediaPayloadBuilder to update alt text, caption, description
HttpResponse<String> mediaResponse =
    client.uploadMedia(
        Path.of("/path/to/image.jpg")
    );

var mediaPayload =
    WPMediaPayloadBuilder.builder()
        .altText("My image")
        .build();

HttpResponse<String> mediaWithMeta =
    client.uploadMedia(
        Path.of("/path/to/image.jpg"),
        mediaPayload
    );
// throws MediaUploadException if the attachment is missing or upload fails
```

### 4. Create and sync cache

```java
Path cachePath =
    Path.of("wp-posts.json");

WPCacheManager cacheManager =
    new WPCacheManager(
        siteInfo,
        cachePath
    );
// For specific dev setups (e.g., self-signed certificates):
// WPCacheManager cacheManager =
//     new WPCacheManager(
//         siteInfo,
//         cachePath,
//         SSLContexts.withTrustManagers(trustManagers)
//     );
// Alternative constructors:
// new WPCacheManager(URI.create("https://example.com"), "user", "appPass", cachePath, sslContext)
// new WPCacheManager(siteInfo, sslContext) // no local cache

// Explicit fetch
cacheManager.fetchCache(cachePath);
cacheManager.fetchCacheFromInstancePath();

// Incremental sync — first call creates the cache if missing
boolean updated =
    cacheManager.cacheSync();

// Pagination tuning (WordPress allows 10–100, default 10)
cacheManager.overrideDefaultPerPage((short) 20);

// Change cache location at runtime
cacheManager.setCachePath(
    Path.of("other-cache.json")
);
```

`fetchCache` / `fetchCacheFromInstancePath` throw `CacheConstructionException` / `CacheFileSystemException` on failure; `cacheSync()` throws `CacheFileSystemException` if no `cachePath` is set and `CacheMetaDataException` if remote `x-wp-total` headers cannot be read.

### 5. Analyze cache offline

```java
WPCacheAnalyzer analyzer =
    new WPCacheAnalyzer(
        Path.of("wp-posts.json")
    );

long count =
    analyzer.getPostCount();

var slugs =
    analyzer.getSlugs();

var categories =
    analyzer.getCleanCategories();

var tags =
    analyzer.getCleanTags();
```

### 6. Extract taxonomies

```java
UnaryOperator<String> cleanOp =
    tag ->
        tag.replaceFirst("^tag-", "")
            .replaceAll("[^a-zA-Z0-9]", " ")
            .trim();

var mappedTags =
    analyzer.mapWPClassId(
        cleanOp,
        TaxonomyMarker.TAG,
        TaxonomyValues.TAGS
    );
```

## Use Cases

**Content Automation**

- Programmatically create/update posts using metadata from existing content
- Automate taxonomy assignment based on cached data

**Taxonomy & Metadata Analysis**

- Aggregate category/tag usage across a site
- Compute taxonomy frequencies without REST calls

**Data Modeling & ML**

- Use the local cache as a structured dataset
- Feed analyzed WordPress data into ML pipelines

**Offline-First Analysis**

- Perform repeatable analysis without network dependency
- Ensure deterministic results from fixed snapshots

### Non-Goals

PowerWP4j **does not** aim to:

- Replace WordPress as a CMS
- Provide exhaustive WordPress admin coverage
- Offer automatic/real-time cache sync
- Include UI components or CLI tooling
- Serve as a general-purpose HTTP framework

## Key Packages

| Package                  | Purpose                                                                          |
| ------------------------ | -------------------------------------------------------------------------------- |
| `engine.WPRestClient`    | REST façade for posts, taxonomies, media — `of(WPSiteInfo)` / `of(WPSiteInfo, SSLContext)`; returns `@Nullable HttpResponse<String>` |
| `engine.WPCacheManager`  | Fetches/syncs cache JSON with `SSLContext`; `fetchCache(Path)`, `fetchCacheFromInstancePath()`, `cacheSync()`, `overrideDefaultPerPage(short)`, `setCachePath(Path)` |
| `engine.WPCacheMeta`     | Remote metadata snapshot (`x-wp-total` / `x-wp-totalpages`) via `updateCacheMeta(siteInfo, path, sslContext)` |
| `engine.WPCacheAnalyzer` | Offline analysis utilities                                                       |
| `builders.*`             | Chainable payload builders with snake_case Jackson mapping                       |
| `services.SSLContexts`   | `SSLContext` factory — `defaultSSLContext()`, `withTrustManagers(...)`, `withTrustStore(KeyStore)` |
| `services.HttpRequestService` | Central `clientSend(request, logger, sslContext)` and `linkProcessor(..., sslContext)` (virtual-thread executor, throws `WPRequestException`) |
| `services.RestClientService` | Low-level static REST operations (delegated to by `WPRestClient`)               |
| `models.schema`          | Default WordPress schema enums (prefixed `WP`)                                   |
| `models.taxonomies`      | Taxonomy helpers                                                                 |
| `exceptions.*`           | `LocalConfigurationException`, `SSLConfigurationException`, `WPRequestException`, `CacheConstructionException`, `CacheFileSystemException`, `CacheMetaDataException`, `InvalidApiUrlException`, `MediaUploadException` |
| `utils.*`                | JSON support, functional helpers (`ExceptionCauseTrigger`, `Trigger`, etc.)       |

> Internal package-private helpers `engine.WPCacheReader`, `engine.WPCacheWriter` (atomic temp-file + `ReentrantLock` writes), and `engine.WPCacheDelta` (`nodeDiff` / `pageDiff`) are not part of the public API.

### Cache Design

- **Source of truth**: Analysis runs strictly against the local cache
- **Metadata**: `WPCacheMeta` uses WordPress `x-wp-total` and `x-wp-totalpages` headers; persisted to `<cacheName>_metadata.json` via `writeCacheMetadata`
- **Incremental sync**: `WPCacheDelta.fromMetadata(old, current)` computes `nodeDiff`/`pageDiff`; new pages fetched (virtual-thread `linkProcessor`) and merged by post `id`
- **Atomic writes**: `WPCacheWriter` writes to a temp file then `ATOMIC_MOVE`
- **TLS**: Falls back to `SSLContexts.defaultSSLContext()` when no `SSLContext` is supplied
- **Files**: `<cacheName>.json` + `<cacheName>_metadata.json`

## Extensibility

PowerWP4j supports custom post types and taxonomies via extension interfaces:

| Interface         | Purpose                                              |
| ----------------- | ---------------------------------------------------- |
| `PostTypeEnum`    | Custom post types (must have `show_in_rest => true`) |
| `ClassMarkerEnum` | Custom taxonomy markers                              |
| `ClassValueEnum`  | Custom taxonomy values                               |
| `CacheKeyEnum`    | Custom cache keys                                    |
| `CacheSubKeyEnum` | Nested JSON object keys                              |

**Example implementation:**

```java
public enum MyCacheKeys implements CacheKeyEnum {

    SEO_SCORE("seo_score");

    private final String key;

    MyCacheKeys(String key) {
        this.key = key;
    }

    @Override
    public String value() {
        return key;
    }
}

```

> **Note**: Extension interfaces use `value()` for serialization/REST mapping. `toString()` may be overridden for debugging but isn't used internally.

Default implementations are in `net.ygbstudio.powerwp4j.models.schema` (schema) and `net.ygbstudio.powerwp4j.models.taxonomies` (taxonomies).

## FAQs

**Why alpha status?**  
Core concepts are stable, but method signatures/package boundaries may change before 1.0.

**Why JSON cache instead of a database?**  
Keeps the library lightweight, enables deterministic offline analysis, and makes content easy to inspect/version-control. Use it as an ingestion layer for database workflows.

**Can I use local WordPress environments?**  
Yes. `WPCacheManager` and `WPRestClient` accept an optional `SSLContext` — e.g. `SSLContexts.withTrustStore(keyStore)` or `SSLContexts.withTrustManagers(trustManagers)` via `WPRestClient.of(siteInfo, sslContext)`. If no context is supplied, both fall back to `SSLContexts.defaultSSLContext()` (standard TLS). Custom contexts are only needed for specific development setups (e.g., self-signed certificates); many local sites work with the default.

## Development

| Command               | Purpose                         |
| --------------------- | ------------------------------- |
| `mvn clean package`   | Build                           |
| `mvn test`            | Run tests                       |
| `mvn fmt:format`      | Format code (Google Java Style) |
| `mvn javadoc:javadoc` | Generate docs                   |

### Project Layout

```
src/main/java/net/ygbstudio/powerwp4j/
├── base/       # Extension models and abstractions
├── engine/     # Entry points (WPCacheManager, WPCacheAnalyzer, WPRestClient, WPCacheMeta)
│               # + internal helpers WPCacheReader, WPCacheWriter, WPCacheDelta
├── builders/   # Chainable JSON payload builders
├── services/   # HTTP plumbing (HttpRequestService, RestClientService) + SSLContexts
├── models/     # Schema enums, entities (WPSiteInfo), taxonomies
├── exceptions/ # Library exceptions (Cache*, LocalConfiguration, SSLConfiguration, WPRequest, etc.)
└── utils/      # JSON support, functional helpers (ExceptionCauseTrigger, Trigger, etc.)
```

## Security

- Never commit credentials (application passwords, usernames, site info)
- Ensure the `your-config-file.properties` you create is in `.gitignore`

## License

Apache License, Version 2.0  
Copyright © 2025–2026 YGBStudio

---

_This project is not affiliated with or endorsed by WordPress.org._
