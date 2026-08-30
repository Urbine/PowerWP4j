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

package net.ygbstudio.powerwp4j.models.schema;

import net.ygbstudio.powerwp4j.base.extension.enums.URLFieldsEnum;

/**
 * Enum representing the fields that can be requested in a WordPress REST API path.
 *
 * <p>By default, these field values do not include a trailing slash.
 *
 * @since 0.1.0
 */
public enum WPPathField implements URLFieldsEnum {
  // fields are comma-separated in the URL after the fields_base value.
  /** Base query parameter prefix used for requesting specific fields. */
  FIELDS_BASE("?_fields="),
  /** Field identifier for the author. */
  FIELD_AUTHOR("author"),
  /** Field identifier for the ID. */
  FIELD_ID("id"),
  /** Field identifier for the excerpt. */
  FIELD_EXCERPT("excerpt"),
  /** Field identifier for the title. */
  FIELD_TITLE("title"),
  /** Field identifier for the link. */
  FIELD_LINK("link");

  private final String value;

  WPPathField(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
