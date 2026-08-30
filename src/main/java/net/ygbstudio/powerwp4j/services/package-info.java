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
 * Low-level HTTP and TLS support.
 *
 * <p>Contains {@link net.ygbstudio.powerwp4j.services.HttpRequestService} (central {@code
 * clientSend} and virtual-thread {@code linkProcessor}), {@link
 * net.ygbstudio.powerwp4j.services.RestClientService} (static REST delegates for {@link
 * net.ygbstudio.powerwp4j.engine.WPRestClient}) and {@link
 * net.ygbstudio.powerwp4j.services.SSLContexts} — a factory for {@link javax.net.ssl.SSLContext}
 * with {@code defaultSSLContext()}, {@code withTrustManagers} and {@code withTrustStore} variants.
 * Used internally by {@code engine} and not intended for direct use in most client code.
 *
 * @since 0.1.0
 * @see net.ygbstudio.powerwp4j.engine.WPCacheManager
 */
package net.ygbstudio.powerwp4j.services;
