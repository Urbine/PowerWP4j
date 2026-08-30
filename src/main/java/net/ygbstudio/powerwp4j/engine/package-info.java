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
 * Core entry points for WordPress automation and offline analysis.
 *
 * <p>Public types are {@link net.ygbstudio.powerwp4j.engine.WPCacheManager}, {@link
 * net.ygbstudio.powerwp4j.engine.WPCacheAnalyzer}, {@link
 * net.ygbstudio.powerwp4j.engine.WPRestClient} and {@link
 * net.ygbstudio.powerwp4j.engine.WPCacheMeta}. Typical lifecycle is {@code WPCacheManager} →
 * fetch/sync → {@code WPCacheAnalyzer}. Pagination is controlled by {@code overrideDefaultPerPage}
 * (10–100) and TLS by {@link javax.net.ssl.SSLContext} via {@link
 * net.ygbstudio.powerwp4j.services.SSLContexts}. Package-private helpers {@code WPCacheReader},
 * {@code WPCacheWriter} (atomic {@code ATOMIC_MOVE} + {@code ReentrantLock}) and {@code
 * WPCacheDelta} implement the cache I/O and delta logic.
 *
 * @since 0.1.0
 * @see net.ygbstudio.powerwp4j.services.SSLContexts
 * @see net.ygbstudio.powerwp4j.models.entities.WPSiteInfo
 */
package net.ygbstudio.powerwp4j.engine;
