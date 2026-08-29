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

/**
 * Thrown when a request to the WordPress REST API fails or returns an error response.
 *
 * <p>This exception indicates that the WordPress site could not be reached, returned an error HTTP
 * status, or responded with an unexpected body.
 */
public class WPRequestException extends RuntimeException {
  /**
   * Constructs a new {@code WPRequestException} with the specified detail message.
   *
   * @param message the detail message explaining the reason of the failure.
   */
  public WPRequestException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code WPRequestException} with the specified detail message and cause.
   *
   * @param message the detail message explaining the reason of the failure
   * @param cause the cause of the failure, may be {@code null}
   */
  public WPRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
