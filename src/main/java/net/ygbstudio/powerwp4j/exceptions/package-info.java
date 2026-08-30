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

/**
 * Unchecked exception hierarchy for PowerWP4j.
 *
 * <p>Groups failures by concern: configuration ({@link
 * net.ygbstudio.powerwp4j.exceptions.LocalConfigurationException}, {@link
 * net.ygbstudio.powerwp4j.exceptions.SSLConfigurationException}), transport ({@link
 * net.ygbstudio.powerwp4j.exceptions.WPRequestException}, {@link
 * net.ygbstudio.powerwp4j.exceptions.InvalidApiUrlException}, {@link
 * net.ygbstudio.powerwp4j.exceptions.MediaUploadException}) and cache ({@link
 * net.ygbstudio.powerwp4j.exceptions.CacheConstructionException}, {@link
 * net.ygbstudio.powerwp4j.exceptions.CacheFileSystemException}, {@link
 * net.ygbstudio.powerwp4j.exceptions.CacheMetaDataException}). All extend {@code RuntimeException}
 * and are thrown from {@code engine} and {@code services} packages.
 *
 * @since 0.1.0
 */
package net.ygbstudio.powerwp4j.exceptions;
