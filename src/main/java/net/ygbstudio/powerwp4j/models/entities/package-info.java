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
 * Immutable WordPress domain entities.
 *
 * <p>Central type is {@link net.ygbstudio.powerwp4j.models.entities.WPSiteInfo}, an immutable
 * holder of a {@code java.net.URI} and Application Password credentials with {@code apiBaseUrl()}
 * derivation. Other entities include {@link net.ygbstudio.powerwp4j.models.entities.WPPost}, {@link
 * net.ygbstudio.powerwp4j.models.entities.WPRendered}, {@link
 * net.ygbstudio.powerwp4j.models.entities.WPClassGroup} and {@link
 * net.ygbstudio.powerwp4j.models.entities.WPClassMapping}, all deserialized via {@link
 * net.ygbstudio.powerwp4j.utils.JsonSupport}.
 *
 * @since 0.1.0
 * @see net.ygbstudio.powerwp4j.models.schema
 */
package net.ygbstudio.powerwp4j.models.entities;
