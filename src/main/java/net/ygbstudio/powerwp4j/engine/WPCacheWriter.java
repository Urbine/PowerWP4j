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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import net.ygbstudio.powerwp4j.exceptions.CacheFileSystemException;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.node.ArrayNode;

/**
 * Package-private writer for the local WordPress JSON cache file.
 *
 * <p>Instances are obtained via {@link #fromPath(Path)} or {@link #fromFile(File)}. Writes are
 * guarded by a {@link ReentrantLock} so concurrent calls to {@link #write(ArrayNode)} are
 * serialized. Used internally by {@link WPCacheManager}.
 *
 * @since 0.1.0
 */
final class WPCacheWriter {

  private static final Logger wpCacheWriterLogger = LoggerFactory.getLogger(WPCacheWriter.class);

  /** Lock guarding file creation and write operations. */
  private final ReentrantLock cacheLock = new ReentrantLock();

  /** Destination file for the JSON cache. */
  private final File cacheFile;

  /**
   * Creates a writer for the given path.
   *
   * @param cachePath destination path for the cache file
   */
  private WPCacheWriter(@NotNull Path cachePath) {
    this.cacheFile = cachePath.toFile();
  }

  /**
   * Creates a writer for the given file.
   *
   * @param cacheFile destination file for the cache
   */
  private WPCacheWriter(@NotNull File cacheFile) {
    this.cacheFile = cacheFile;
  }

  /**
   * Creates a new writer for the given path.
   *
   * @param cachePath destination path for the cache file
   * @return a new {@code WPCacheWriter} instance
   */
  @Contract("_ -> new")
  public static @NotNull WPCacheWriter fromPath(Path cachePath) {
    return new WPCacheWriter(cachePath);
  }

  /**
   * Creates a new writer for the given file.
   *
   * @param cacheFile destination file for the cache
   * @return a new {@code WPCacheWriter} instance
   */
  @Contract("_ -> new")
  public static @NotNull WPCacheWriter fromFile(File cacheFile) {
    return new WPCacheWriter(cacheFile);
  }

  /**
   * Writes the JSON array to the cache file atomically via a temporary file.
   *
   * <p>Creates a temporary file in the cache directory, writes {@code wpJsonArray} with
   * pretty-printing, then moves it to the destination with {@link StandardCopyOption#ATOMIC_MOVE}.
   *
   * @param wpJsonArray the JSON array to write
   * @return {@code true} if the temp file was moved and the cache file exists
   * @throws IOException if an I/O error occurs during writing or the atomic move
   * @throws CacheFileSystemException if the temporary file could not be created
   */
  private boolean fsCacheWrite(@NotNull ArrayNode wpJsonArray) throws IOException {
    Path cachePath = cacheFile.toPath();
    Path tempFilePath =
        Files.createTempFile(
            Objects.requireNonNullElse(
                cachePath.toAbsolutePath().getParent(), cachePath.getParent()),
            null,
            null);
    if (!tempFilePath.toFile().exists()) {
      wpCacheWriterLogger.debug("Failed to cache temp file at {}", cacheFile.getAbsolutePath());
      throw new CacheFileSystemException(
          String.format("Failed to create cache file at %s", cacheFile.getAbsolutePath()));
    }
    try (FileWriter writer = new FileWriter(tempFilePath.toFile(), StandardCharsets.UTF_8)) {
      JsonSupport.getMapper().writerWithDefaultPrettyPrinter().writeValue(writer, wpJsonArray);
    } finally {
      Files.move(tempFilePath, cachePath, StandardCopyOption.ATOMIC_MOVE);
    }

    wpCacheWriterLogger.info("Cache has been updated at {}", cacheFile.getAbsolutePath());
    return !tempFilePath.toFile().exists() && cacheFile.exists();
  }

  /**
   * Thread-safe write of the cache file.
   *
   * <p>Acquires {@link #cacheLock}, delegates to {@link #fsCacheWrite(ArrayNode)}, and releases the
   * lock.
   *
   * @param wpJsonArray the JSON array to persist
   * @return {@code true} if the cache file was written and exists
   * @throws CacheFileSystemException if writing fails due to filesystem constraints
   */
  public boolean write(@NotNull ArrayNode wpJsonArray) {
    cacheLock.lock();
    try {
      return fsCacheWrite(wpJsonArray);
    } catch (IOException ioEx) {
      wpCacheWriterLogger.debug(
          "Caught {} caused by {}", ioEx.getClass().getSimpleName(), ioEx.getMessage());
      throw new CacheFileSystemException(() -> "Failed to write cache file: " + ioEx.getMessage());
    } finally {
      cacheLock.unlock();
    }
  }
}
