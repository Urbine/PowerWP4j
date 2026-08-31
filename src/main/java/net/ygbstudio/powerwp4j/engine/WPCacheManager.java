/*
 * PowerWP4j - Power WP for Java
 *
 * Copyright 2025-2026 Yoham Gabriel Barboza B. (YGBStudio)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package net.ygbstudio.powerwp4j.engine;

import static net.ygbstudio.powerwp4j.services.HttpRequestService.makeRequestURL;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.LongStream;
import javax.net.ssl.SSLContext;
import net.ygbstudio.powerwp4j.base.extension.enums.QueryParamEnum;
import net.ygbstudio.powerwp4j.exceptions.CacheConstructionException;
import net.ygbstudio.powerwp4j.exceptions.CacheFileSystemException;
import net.ygbstudio.powerwp4j.exceptions.CacheMetaDataException;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;
import net.ygbstudio.powerwp4j.models.schema.WPCacheKey;
import net.ygbstudio.powerwp4j.models.schema.WPQueryParam;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.services.HttpRequestService;
import net.ygbstudio.powerwp4j.services.SSLContexts;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

/**
 * Main entry point for interacting with a WordPress REST API and managing a local JSON cache.
 *
 * <p>Typical lifecycle: construct with {@link WPSiteInfo} and an optional {@link Path} cache file,
 * fetch full content via {@link #fetchCache(Path)}, {@link #fetchCacheFromInstancePath()} or the
 * first call to {@link #cacheSync()}, keep it up to date with subsequent {@link #cacheSync()}
 * calls, and analyse it with {@link #getCacheAnalyzer()}.
 *
 * <p>If no cache exists, the first {@link #cacheSync()} invocation creates it automatically. A
 * local cache is otherwise not created automatically; callers needing explicit control should use
 * {@link #fetchCache(Path)}.
 *
 * <p>Pagination is controlled by {@code DEFAULT_PER_PAGE} (WordPress allows 10–100, see {@link
 * #overrideDefaultPerPage(short)}). SSL is configured via the supplied {@link SSLContext}.
 *
 * @author Yoham Gabriel B.
 * @since 0.1.0
 * @see WPCacheReader
 * @see WPCacheWriter
 * @see WPCacheAnalyzer
 */
public final class WPCacheManager {
  private static final Logger wpSiteEngineLogger = LoggerFactory.getLogger(WPCacheManager.class);

  /** Default number of posts per paginated REST request; WordPress enforces 10–100. */
  private short defaultPerPage = 10;

  /** Site credentials and derived API base URL. */
  private WPSiteInfo siteInfo;

  /** Last-known remote cache metadata (total pages/posts). */
  private WPCacheMeta wpCacheMeta;

  /** File system path to the JSON cache; {@code null} when running without a local cache. */
  private Path cachePath;

  /** Paginated REST URLs used during fetch/sync. */
  private List<String> linkList;

  /** SSL context used for HTTPS requests. */
  private final SSLContext sslContext;

  /** Private delegating constructor that stores the SSL context. */
  private WPCacheManager(SSLContext sslContext) {
    this.sslContext = sslContext != null ? sslContext : SSLContexts.defaultSSLContext();
  }

  /**
   * Initializes a new instance of the WPCacheManager class. If a local WordPress cache is found, it
   * is loaded into memory, otherwise a new cache must be created using {@link
   * WPCacheManager#fetchCache(Path)}, {@link WPCacheManager#fetchCacheFromInstancePath()}, or the
   * first call to {@link #cacheSync()} if you already provided a path in the constructor.
   *
   * <p>A local cache is not created automatically since the client must handle any exceptions that
   * result from the cache creation process to ensure maximum control of client-specific flows,
   * exception handling, and logging styles.
   *
   * @param wpURI URI of the WordPress site
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   * @param cachePath path to the cache file, or {@code null} to run without a local cache
   * @param sslContext SSL context for HTTPS requests, or {@code null} to use the default
   */
  public WPCacheManager(
      @NotNull URI wpURI,
      @NotNull String username,
      @NotNull String applicationPassword,
      @Nullable Path cachePath,
      @Nullable SSLContext sslContext) {
    this(sslContext);
    this.siteInfo = new WPSiteInfo(wpURI, username, applicationPassword);
    this.cachePath = cachePath;
    if (cachePath != null) {
      WPCacheMeta.from(cachePath).ifPresent(cacheMeta -> wpCacheMeta = cacheMeta);
    }
    String apiBaseUrl = siteInfo.apiBaseUrl();
    wpSiteEngineLogger.info("Initialized WPCacheManager for site: {}", wpURI.getHost());
    wpSiteEngineLogger.info("API Base Path set to: {}", apiBaseUrl);
  }

  /**
   * Initializes a new instance of the WPCacheManager class. If a local WordPress cache is not
   * needed, the cache will be ignored and the cachePath parameter will be set to null.
   *
   * <p>In case you want to create a cache in the current instance of the WPCacheManager, proceed to
   * create the cache file using the {@link WPCacheManager#fetchCache(Path)} method.
   *
   * @param wpURI URI of the WordPress site
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   */
  public WPCacheManager(
      @NotNull URI wpURI, @NotNull String username, @NotNull String applicationPassword) {
    this(wpURI, username, applicationPassword, null, null);
  }

  /**
   * Initializes a new instance of the WPCacheManager class using the provided {@link WPSiteInfo}
   * object and an optional cache path. If a cache path is provided, the cache will be loaded from
   * the file system. If a cache path is not provided, the cache will not be created and the
   * cachePath parameter will be set to null.
   *
   * @param siteInfo the site information object containing the fully qualified domain name,
   *     username, and application password
   * @param cachePath an optional path to the cache file, or {@code null} for no local cache
   * @param sslContext SSL context for HTTPS requests, or {@code null} to use the default
   */
  public WPCacheManager(
      @NotNull WPSiteInfo siteInfo, @Nullable Path cachePath, @Nullable SSLContext sslContext) {
    this(siteInfo.wpURI(), siteInfo.wpUser(), siteInfo.wpAppPass(), cachePath, sslContext);
  }

  /**
   * Initializes a new instance of the WPCacheManager class using the provided {@link WPSiteInfo}
   * object. If a local WordPress cache is not needed, the cache will be ignored and the cachePath
   * parameter will be set to null.
   *
   * <p>In case you want to create a cache in the current instance of the WPCacheManager, proceed to
   * create the cache file using the {@link WPCacheManager#fetchCache(Path)} method.
   *
   * @param siteInfo the site information object containing the fully qualified domain name,
   *     username, and application password
   * @param sslContext SSL context for HTTPS requests, or {@code null} to use the default
   */
  public WPCacheManager(@NotNull WPSiteInfo siteInfo, @Nullable SSLContext sslContext) {
    this(siteInfo.wpURI(), siteInfo.wpUser(), siteInfo.wpAppPass(), null, sslContext);
  }

  /**
   * Connects to the WordPress REST API and returns the response. This is a convenience method for
   * this class; more capable operations are available in {@link
   * net.ygbstudio.powerwp4j.services.RestClientService}.
   *
   * @param queryParams the query parameters to be used in the request
   * @param pathParam the path parameter to be used in the request
   * @return the HTTP response, or {@code null} if the request could not be executed
   */
  public @Nullable HttpResponse<String> connectWP(
      @NotNull Map<QueryParamEnum, String> queryParams, @NotNull WPRestPath pathParam) {
    String url = makeRequestURL(siteInfo.apiBaseUrl(), queryParams, pathParam);
    return HttpRequestService.connectGetWP(
        url, siteInfo.wpUser(), siteInfo.wpAppPass(), wpSiteEngineLogger, sslContext);
  }

  /**
   * Creates paginated links to the WordPress REST API.
   *
   * @param totalPages the total number of pages
   * @param perPage the number of posts per page; if {@code <= 0} the {@code per_page} query param
   *     is omitted and the server default is used
   * @return an unmodifiable list of request URLs, one per page
   */
  @Unmodifiable
  @NotNull
  private List<String> linkListCreator(long totalPages, int perPage) {
    Map<WPQueryParam, String> queryParams = new EnumMap<>(WPQueryParam.class);
    return LongStream.range(1, totalPages + 1)
        .mapToObj(
            i -> {
              if (!queryParams.isEmpty()) queryParams.clear();
              queryParams.put(WPQueryParam.PAGE, String.valueOf(i));
              if (perPage > 0) queryParams.put(WPQueryParam.PER_PAGE, String.valueOf(perPage));
              return makeRequestURL(siteInfo.apiBaseUrl(), queryParams, WPRestPath.POSTS);
            })
        .toList();
  }

  /**
   * Fetches the local cache file from the WordPress REST API.
   *
   * @param cachePath the path to the cache file
   * @throws CacheConstructionException if remote metadata cannot be obtained
   * @throws CacheFileSystemException if the cache cannot be written to disk
   */
  private void fetchCacheInternal(@NotNull Path cachePath) {

    if (Objects.isNull(linkList) || linkList.isEmpty()) {
      Runnable throwCacheException =
          () -> {
            throw new CacheConstructionException(
                () ->
                    "Failed to gather WordPress post metadata for "
                        + siteInfo.wpURI().getHost()
                        + " Check your connection and try again");
          };
      Optional.ofNullable(WPCacheMeta.updateCacheMeta(siteInfo, cachePath, sslContext))
          .ifPresentOrElse(
              cacheMeta -> {
                wpCacheMeta = cacheMeta;
                linkList = linkListCreator(wpCacheMeta.totalPages(), defaultPerPage);
              },
              throwCacheException);
    }

    String apiBaseUrl = siteInfo.apiBaseUrl();
    wpSiteEngineLogger.info("Processing cache links for {}", apiBaseUrl);
    ArrayNode wpJsonArray = fetchCacheFromInstancePath(linkList, null, 0, 0, null, null);

    boolean isCacheWritten = WPCacheWriter.fromPath(cachePath).write(wpJsonArray);

    if (!isCacheWritten) {
      File cacheFile = cachePath.toFile();
      String errorMsg =
          String.format("Unable to write cache file at %s", cacheFile.getAbsolutePath());
      wpSiteEngineLogger.debug(
          "Method 'fetchCacheInternal' cache at {} could not be written. Location is writable: {}",
          cacheFile.getAbsolutePath(),
          cacheFile.canWrite());
      throw new CacheFileSystemException(errorMsg);
    }
  }

  /**
   * Fetches the JSON cache from the WordPress REST API.
   *
   * @param listOfLinks the list of links to fetch
   * @param retryPred predicate to retry on when a specific condition is expected, or {@code null}
   * @param retryAttempts number of retry attempts
   * @param intervalTime delay between retries
   * @param intervalUnit time unit for {@code intervalTime}, or {@code null} for no delay
   * @param retryFailedMsg supplier for the log message when retries are exhausted, or {@code null}
   * @return the aggregated JSON cache as a single {@link ArrayNode}
   */
  private ArrayNode fetchCacheFromInstancePath(
      @NotNull List<String> listOfLinks,
      @Nullable Predicate<ArrayNode> retryPred,
      int retryAttempts,
      int intervalTime,
      @Nullable TimeUnit intervalUnit,
      @Nullable Supplier<String> retryFailedMsg) {
    ObjectMapper mapper = JsonSupport.getMapper();
    BiFunction<HttpClient, String, CompletableFuture<ArrayNode>> procedureFunction =
        getFetchProcedure();
    return HttpRequestService.linkProcessor(
        listOfLinks,
        procedureFunction,
        Objects::nonNull,
        Collector.of(mapper::createArrayNode, ArrayNode::addAll, ArrayNode::addAll),
        Objects.isNull(retryPred) ? null : retryPred,
        intervalUnit,
        intervalTime,
        retryAttempts,
        retryFailedMsg,
        sslContext);
  }

  /**
   * Builds the async fetch procedure for a list of paginated links.
   *
   * @return a {@link BiFunction} that fetches a single link via {@link HttpClient#sendAsync} and
   *     parses the body into an {@link ArrayNode}
   */
  @NotNull
  @Contract(pure = true)
  private BiFunction<HttpClient, String, CompletableFuture<ArrayNode>> getFetchProcedure() {

    Function<String, HttpRequest> requestFunction =
        link -> {
          wpSiteEngineLogger.debug("Processing link -> {} ", link);
          return HttpRequestService.buildWpGetRequest(
              link, siteInfo.wpUser(), siteInfo.wpAppPass(), wpSiteEngineLogger);
        };

    return (client, link) ->
        client
            .sendAsync(requestFunction.apply(link), BodyHandlers.ofString(StandardCharsets.UTF_8))
            .thenApply(HttpResponse::body)
            .thenApply(
                body -> {
                  try {
                    JsonNode node = JsonSupport.getTreeNode(body);
                    if (!node.isArray()) {
                      wpSiteEngineLogger.debug(
                          "Expected JSON array but got {} for link {}", node.getNodeType(), link);
                      return null;
                    }
                    return (ArrayNode) node;
                  } catch (Exception ex) {
                    wpSiteEngineLogger.debug(
                        "Failed parsing JSON for link {}: {}", link, ex.getMessage());
                    return null;
                  }
                })
            .exceptionally(
                ex -> {
                  wpSiteEngineLogger.debug(
                      "Failed processing link {} due to {} cause: {}",
                      link,
                      ex.getClass().getSimpleName(),
                      ex.getCause() != null ? ex.getCause().getMessage() : "unknown");
                  return null;
                });
  }

  /**
   * Fetches the cache using the instance's {@link #cachePath}.
   *
   * <p>Alternatively, if no cache exists the first call to {@link #cacheSync()} will create it
   * automatically.
   *
   * @throws UnsupportedOperationException if no cache path was provided at construction
   * @throws CacheConstructionException if remote metadata cannot be obtained
   * @throws CacheFileSystemException if the cache cannot be written to disk
   */
  public void fetchCacheFromInstancePath() {
    if (cachePath == null)
      throw new UnsupportedOperationException(
          "Unable to fetch cache without a cache path in this instance. Provide a cache path and try again.");
    fetchCacheInternal(cachePath);
  }

  /**
   * Fetches the cache to an explicit file path.
   *
   * <p>Alternatively, if no cache exists the first call to {@link #cacheSync()} will create it
   * automatically when an instance path has been set.
   *
   * @param cachePath the destination path for the cache file
   * @throws CacheConstructionException if remote metadata cannot be obtained
   * @throws CacheFileSystemException if the cache cannot be written to disk
   */
  public void fetchCache(@NotNull Path cachePath) {
    fetchCacheInternal(cachePath);
  }

  /**
   * Refreshes instance metadata from the remote site.
   *
   * @param forceUpdate if {@code true} always refreshes; otherwise only if {@code wpCacheMeta} is
   *     {@code null}
   * @throws CacheMetaDataException if remote metadata cannot be obtained
   */
  private void updateInstanceMetadata(boolean forceUpdate) {
    if (forceUpdate || wpCacheMeta == null) {
      WPCacheMeta newCacheMeta = WPCacheMeta.updateCacheMeta(siteInfo, cachePath, sslContext);
      if (newCacheMeta == null) {
        wpSiteEngineLogger.debug(
            "Response to {} returned a 'null' value. Throwing CacheMetadataException...",
            siteInfo.wpURI().getHost());
        throw new CacheMetaDataException(
            "Failed cache metadata update. Check your internet connection.");
      }
      wpCacheMeta = newCacheMeta;
    }
  }

  /**
   * Synchronizes the local cache with the WordPress site. Assumes incremental changes and uses
   * sorting pipelines to compute deltas efficiently; not suited for large rewrites.
   *
   * <p>If no cache file exists at {@link #cachePath}, the first invocation creates it via {@link
   * #fetchCacheFromInstancePath()} and returns {@code true}.
   *
   * @return {@code true} if the cache was created or updated, {@code false} if it was already
   *     up-to-date
   * @throws CacheFileSystemException if {@link #cachePath} is {@code null} or not set
   * @throws CacheMetaDataException if remote metadata cannot be refreshed
   * @throws CacheConstructionException if the initial cache creation fails
   * @throws CacheFileSystemException if the cache file cannot be written
   */
  public boolean cacheSync() {

    if (cachePath == null)
      throw new CacheFileSystemException(
          "Instance cache path has not been set. Use the setter or use a constructor that takes one.");

    if (!cachePath.toFile().exists()) fetchCacheFromInstancePath();

    updateInstanceMetadata(false);

    WPCacheMeta cacheMetaOld =
        new WPCacheMeta(
            wpCacheMeta.totalPages(),
            wpCacheMeta.totalPosts(),
            LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC));

    updateInstanceMetadata(true);

    ArrayNode fromCache = WPCacheReader.fromPath(cachePath).getArrayNodeCache();

    WPCacheDelta wpDelta = WPCacheDelta.fromMetadata(wpCacheMeta, cacheMetaOld);
    wpSiteEngineLogger.debug("Node diff: {}", wpDelta.nodeDiff());
    wpSiteEngineLogger.debug("Page diff: {}", wpDelta.pageDiff());

    if (wpDelta.nodeDiff() == 0 && wpDelta.pageDiff() == 0) {
      wpSiteEngineLogger.info("{} Cache is up-to-date", siteInfo.wpURI().getHost());
      return false;
    }

    linkList =
        linkListCreator(wpCacheMeta.totalPages(), defaultPerPage).stream()
            // Each page has a default number of items and,
            // if the number of pages is less than the default number of items,
            // those may be contained in the last page
            .limit(
                wpDelta.nodeDiff() < defaultPerPage && wpDelta.pageDiff() == 0
                    ? 1
                    : wpDelta.pageDiff())
            .toList();

    Comparator<JsonNode> jsonNodeComparator =
        (jsonNode1, jsonNode2) -> {
          long id1 = jsonNode1.get(WPCacheKey.ID.value()).asLong();
          long id2 = jsonNode2.get(WPCacheKey.ID.value()).asLong();
          return Long.compare(id1, id2);
        };

    long lastId =
        fromCache
            .valueStream()
            .sorted(jsonNodeComparator)
            .toList()
            .getLast()
            .get(WPCacheKey.ID.value())
            .asLong();

    Predicate<ArrayNode> testForLastElem =
        elem ->
            elem.valueStream()
                    .sorted(jsonNodeComparator)
                    .toList()
                    .getLast()
                    .get(WPCacheKey.ID.value())
                    .asLong()
                > lastId;

    List<JsonNode> updatedPosts =
        fetchCacheFromInstancePath(
                linkList,
                testForLastElem,
                3,
                2,
                TimeUnit.SECONDS,
                () -> "Failed to fetch new cache pages. Reached maximum retry attempts")
            .valueStream()
            .sorted(jsonNodeComparator.reversed())
            .limit(wpDelta.nodeDiff())
            .toList();

    fromCache.addAll(updatedPosts);
    return WPCacheWriter.fromPath(cachePath).write(fromCache);
  }

  /**
   * Overrides the pagination size for REST requests.
   *
   * @param defaultPerPage number of posts per page, must be between 10 and 100 inclusive
   * @throws IllegalArgumentException if {@code defaultPerPage} is outside [10, 100]
   */
  public void overrideDefaultPerPage(short defaultPerPage) {
    if (defaultPerPage > 100 || defaultPerPage < 10)
      throw new IllegalArgumentException(
          "WordPress can retrieve a maximum of 100 posts per page and a minimum of 10");
    this.defaultPerPage = defaultPerPage;
  }

  /**
   * Sets the file system path for the JSON cache.
   *
   * <p>If no cache exists at the new path, the next {@link #cacheSync()} call will create it.
   *
   * @param cachePath the new cache file path, or {@code null} to clear
   */
  public void setCachePath(Path cachePath) {
    this.cachePath = cachePath;
  }

  /**
   * Returns the base URL of the WordPress REST API.
   *
   * @return the base URL of the WordPress REST API
   */
  public String apiBaseUrl() {
    return siteInfo.apiBaseUrl();
  }

  /**
   * Returns the user name for the WordPress site.
   *
   * @return the user name for the WordPress site
   */
  public String wpUser() {
    return siteInfo.wpUser();
  }

  /**
   * Returns a new WPCacheAnalyzer instance using the cache file specified at construction time.
   *
   * @return a new WPCacheAnalyzer instance
   */
  public WPCacheAnalyzer getCacheAnalyzer() {
    return new WPCacheAnalyzer(cachePath);
  }
}
