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

package net.ygbstudio.powerwp4j.utils.functional;

/**
 * Functional interface that represents a trigger function that throws a checked exception when
 * activated. It resembles a {@link Trigger} but its intent is to declare a block that contains an
 * exception, so that it can be thrown at different stages without duplication. For example, you can
 * include logging and a {@code throw} statement in a single block.
 *
 * <p>It is also useful for wrapping checked exceptions in contexts where explicit exception
 * handling would clutter the code, such as in lambda expressions or method references.
 *
 * @see Trigger
 * @see ExceptionCauseTrigger
 * @author Yoham Gabriel B.
 */
@FunctionalInterface
public interface ExceptionTrigger {

  /** Activates the trigger function, throwing the exception. */
  void throwing();
}
