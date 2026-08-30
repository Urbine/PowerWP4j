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

package net.ygbstudio.powerwp4j.models.entities;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * A record that represents a group of values grouped by a key.
 *
 * @param <T> the type of the key
 * @param <U> the type of the values
 * @param groupByKey the key used to group the values
 * @param groupedValues the values grouped under the key
 * @since 0.1.0
 */
public record WPClassGroup<T, U>(@NotNull T groupByKey, @NotNull Set<U> groupedValues) {
  public WPClassGroup {
    groupedValues = Set.copyOf(groupedValues);
  }
}
