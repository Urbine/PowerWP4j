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
 * Chainable builders for WordPress REST payloads.
 *
 * <p>Provides {@link net.ygbstudio.powerwp4j.builders.WPBasicPayloadBuilder}, {@link
 * net.ygbstudio.powerwp4j.builders.WPMediaPayloadBuilder}, {@link
 * net.ygbstudio.powerwp4j.builders.WPPayloadNodeBuilder} and {@link
 * net.ygbstudio.powerwp4j.builders.WPMediaPayloadNodeBuilder}. Builders produce Jackson {@code
 * JsonNode} with snake_case mapping and are consumed by {@link
 * net.ygbstudio.powerwp4j.engine.WPRestClient}. They extend the SPI in {@link
 * net.ygbstudio.powerwp4j.base.extension.builders}.
 *
 * @since 0.1.0
 * @see net.ygbstudio.powerwp4j.base.extension.builders
 */
package net.ygbstudio.powerwp4j.builders;
