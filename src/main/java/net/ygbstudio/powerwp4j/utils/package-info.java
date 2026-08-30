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
 * Shared JSON and resource utilities.
 *
 * <p>Provides {@link net.ygbstudio.powerwp4j.utils.JsonSupport}, a thin wrapper around a single
 * pre-configured Jackson {@code ObjectMapper} (snake_case, indented output, tolerant
 * deserialization), and {@link net.ygbstudio.powerwp4j.utils.Helpers} for classpath resource
 * loading and small helper routines. Reused by {@code engine}, {@code services} and {@code models}
 * packages.
 *
 * @since 0.1.0
 * @see net.ygbstudio.powerwp4j.utils.functional
 */
package net.ygbstudio.powerwp4j.utils;
