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

package net.ygbstudio.powerwp4j.exceptions;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

/**
 * CacheMetaDataException is an exception that is thrown when there is an error with the cache
 * metadata or its creation.
 *
 * @author Yoham Gabriel B. @ YGBStudio
 * @since 0.1.0
 */
public class CacheMetaDataException extends RuntimeException {
  /**
   * Creates a new cache metadata exception with the given message.
   *
   * @param message the detail message explaining the cause of the failure.
   */
  public CacheMetaDataException(@NotNull String message) {
    super(message);
  }

  /**
   * Creates a new cache metadata exception with a lazily computed message.
   *
   * @param message a supplier that lazily computes the detail message explaining the cause of the
   *     failure.
   */
  public CacheMetaDataException(@NotNull Supplier<String> message) {
    super(message.get());
  }

  /**
   * Creates a new cache metadata exception with a message and an upstream exception (cause).
   *
   * @param message the detail message explaining the cause of the failure.
   * @param cause the upstream exception that caused this exception to be thrown.
   */
  public CacheMetaDataException(@NotNull String message, @NotNull Throwable cause) {
    super(message, cause);
  }
}
