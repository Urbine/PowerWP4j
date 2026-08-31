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

import net.ygbstudio.powerwp4j.base.extension.enums.CacheKeyEnum;

/**
 * Enumeration of possible cache keys for the WordPress REST API cache. It is the default
 * implementation of the {@link CacheKeyEnum} interface.
 *
 * @see CacheKeyEnum
 * @since 0.1.0
 */
public enum WPCacheKey implements CacheKeyEnum {
  /** Alternative text. */
  ALT_TEXT("alt_text"),
  /** Author ID. */
  AUTHOR("author"),
  /** Media caption. */
  CAPTION("caption"),
  /** CSS class list — WordPress post classes; source for taxonomy parsing */
  CLASS_LIST("class_list"),
  /** Rendered content. */
  CONTENT("content"),
  /** Comment status. */
  COMMENT_STATUS("comment_status"),
  /** Local date. */
  DATE("date"),
  /** GMT date. */
  DATE_GMT("date_gmt"),
  /** Description text. */
  DESCRIPTION("description"),
  /** Excerpt text. */
  EXCERPT("excerpt"),
  /** Featured media ID. */
  FEATURED_MEDIA("featured_media"),
  /** Post format. */
  FORMAT("format"),
  /** Global unique identifier. */
  GUID("guid"),
  /** Post ID. */
  ID("id"),
  /** Permalink URL. */
  LINK("link"),
  /** URL slug. */
  SLUG("slug"),
  /** Post status. */
  STATUS("status"),
  /** Sticky flag. */
  STICKY("sticky"),
  /** Post title. */
  TITLE("title"),
  /** Post type. */
  TYPE("type");

  private final String value;

  WPCacheKey(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
