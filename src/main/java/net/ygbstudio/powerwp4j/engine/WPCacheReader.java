/*
 * PowerWP4j - Power WP for Java
 *
 * Copyright 2025-2026 Yoham Gabriel Barboza B. (YGBStudio)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
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

import static net.ygbstudio.powerwp4j.utils.JsonSupport.jsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.ygbstudio.powerwp4j.exceptions.CacheFileSystemException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.node.ArrayNode;

/**
 * Package-private reader for the local WordPress JSON cache file.
 *
 * <p>Instances are obtained via {@link #fromFile(File)} or {@link #fromPath(Path)}. Provides access
 * to the raw {@link FileReader} and to the parsed {@link ArrayNode} cache.
 */
final class WPCacheReader {
  private static final Logger wpCacheReaderLogger = LoggerFactory.getLogger(WPCacheReader.class);

  /** Backing cache file on the filesystem. */
  private final File cacheFile;

  /**
   * Creates a reader for the given file.
   *
   * @param cacheFile the cache file to read
   */
  private WPCacheReader(@NotNull File cacheFile) {
    this.cacheFile = cacheFile;
  }

  /**
   * Creates a reader for the given path.
   *
   * @param cachePath the cache file path to read
   */
  @Contract(pure = true)
  private WPCacheReader(@NotNull Path cachePath) {
    this(cachePath.toFile());
  }

  /**
   * Creates a new reader for the given file.
   *
   * @param cacheFile the cache file to read
   * @return a new {@code WPCacheReader} instance
   */
  @Contract(value = "_ -> new", pure = true)
  public static @NotNull WPCacheReader fromFile(File cacheFile) {
    return new WPCacheReader(cacheFile);
  }

  /**
   * Creates a new reader for the given path.
   *
   * @param cachePath the cache file path to read
   * @return a new {@code WPCacheReader} instance
   */
  @Contract(value = "_ -> new", pure = true)
  public static @NotNull WPCacheReader fromPath(Path cachePath) {
    return new WPCacheReader(cachePath);
  }

  /**
   * Returns a UTF-8 {@link FileReader} for the cache file.
   *
   * @return a new {@code FileReader} for {@link #cacheFile}; never {@code null}
   * @throws CacheFileSystemException if the file does not exist
   * @throws IOException if the file cannot be opened
   */
  public @NotNull FileReader getFileReader() throws IOException {
    if (!cacheFile.exists()) {
      Supplier<String> notFoundMsg =
          () -> "Cache file does not exist at " + cacheFile.getAbsolutePath();
      wpCacheReaderLogger.debug(notFoundMsg.get());
      throw new CacheFileSystemException(notFoundMsg);
    }
    return new FileReader(cacheFile, StandardCharsets.UTF_8);
  }

  /**
   * Reads and parses the cache file into a Jackson {@link ArrayNode} via {@link
   * net.ygbstudio.powerwp4j.utils.JsonSupport#jsonReader(java.io.Reader, Class)}.
   *
   * <p>The file is opened as UTF-8 via {@link #getFileReader()}.
   *
   * @return the parsed JSON array from the cache file
   * @throws CacheFileSystemException if the file does not exist or cannot be read/parsed
   */
  public ArrayNode getArrayNodeCache() {
    try (FileReader cacheFileReader = getFileReader()) {
      return jsonReader(cacheFileReader, ArrayNode.class);
    } catch (IOException ieEx) {
      String absoluteFilePath = cacheFile.getAbsolutePath();
      String causeMessage = ieEx.getCause() != null ? ieEx.getCause().getMessage() : "no cause";
      wpCacheReaderLogger.debug(
          "Failed to read cache file at {}: {} caused by: {}",
          absoluteFilePath,
          ieEx.getMessage(),
          causeMessage);
      throw new CacheFileSystemException(
          () ->
              String.format(
                  "Failed to read cache file at %s due to %s", absoluteFilePath, causeMessage));
    }
  }
}
