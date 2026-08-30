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
 * Tiny functional helpers.
 *
 * <p>Defines lightweight functional interfaces used to keep service and engine code concise: {@link
 * net.ygbstudio.powerwp4j.utils.functional.Trigger}, {@link
 * net.ygbstudio.powerwp4j.utils.functional.TypedTrigger}, {@link
 * net.ygbstudio.powerwp4j.utils.functional.TriggerCallable}, {@link
 * net.ygbstudio.powerwp4j.utils.functional.ExceptionTrigger} and {@link
 * net.ygbstudio.powerwp4j.utils.functional.ExceptionCauseTrigger}. They avoid duplication around
 * retry, exception wrapping and logging.
 *
 * @since 0.1.0
 */
package net.ygbstudio.powerwp4j.utils.functional;
