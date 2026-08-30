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
 * Enumeration SPI for the WordPress domain.
 *
 * <p>Defines {@link net.ygbstudio.powerwp4j.base.extension.enums.FriendlyEnum} with its {@code
 * value()} contract — the wire value used in REST requests and JSON (e.g., {@code pending}). Twelve
 * extension points build on it: {@link net.ygbstudio.powerwp4j.base.extension.enums.PostTypeEnum},
 * {@link net.ygbstudio.powerwp4j.base.extension.enums.PostStatusEnum}, {@link
 * net.ygbstudio.powerwp4j.base.extension.enums.QueryParamEnum}, {@link
 * net.ygbstudio.powerwp4j.base.extension.enums.CacheKeyEnum} and others. Default implementations
 * live in {@link net.ygbstudio.powerwp4j.models.schema} and {@link
 * net.ygbstudio.powerwp4j.models.taxonomies}.
 *
 * @since 0.1.0
 * @see net.ygbstudio.powerwp4j.models.schema
 */
package net.ygbstudio.powerwp4j.base.extension.enums;
