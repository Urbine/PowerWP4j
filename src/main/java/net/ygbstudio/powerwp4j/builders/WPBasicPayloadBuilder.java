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

package net.ygbstudio.powerwp4j.builders;

import static net.ygbstudio.powerwp4j.utils.Helpers.enumFromValue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import net.ygbstudio.powerwp4j.base.extension.builders.AbstractPayloadBuilder;
import net.ygbstudio.powerwp4j.base.extension.enums.CommentStatusEnum;
import net.ygbstudio.powerwp4j.base.extension.enums.PostStatusEnum;
import net.ygbstudio.powerwp4j.models.schema.WPCommentStatus;
import net.ygbstudio.powerwp4j.models.schema.WPStatus;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.JsonNode;

/**
 * Basic payload builder for WordPress REST Payloads. It can be used for posts and taxonomy
 * creation.
 *
 * <p>Inherits all methods from {@link AbstractPayloadBuilder} to provide a chainable pattern for
 * building payloads for the WordPress REST API. The payload is built using a {@link JsonNode}
 * object.
 *
 * <p>In case you need to attach a payload to a media file, use {@link WPMediaPayloadBuilder}.
 *
 * @see AbstractPayloadBuilder
 */
@JsonInclude(Include.NON_NULL)
public class WPBasicPayloadBuilder extends AbstractPayloadBuilder<WPBasicPayloadBuilder> {

  private WPBasicPayloadBuilder() {}

  @Override
  protected WPBasicPayloadBuilder self() {
    return this;
  }

  /**
   * Creates a new instance of {@link WPBasicPayloadBuilder}.
   *
   * @return A new instance of {@link WPBasicPayloadBuilder}.
   */
  @Contract(" -> new")
  public static @NotNull WPBasicPayloadBuilder builder() {
    return new WPBasicPayloadBuilder();
  }

  /** Returns the configured post status, or {@code null} if unset. */
  public PostStatusEnum getStatus() {
    return enumFromValue(WPStatus.class, status, true).orElse(null);
  }

  /** Returns the configured category IDs, or {@code null} if unset. */
  public List<Integer> getCategories() {
    return categories;
  }

  /** Returns the configured tag IDs, or {@code null} if unset. */
  public List<Integer> getTags() {
    return tags;
  }

  /** Returns the configured slug, or {@code null} if unset. */
  public String getSlug() {
    return slug;
  }

  /** Returns the configured title, or {@code null} if unset. */
  public String getTitle() {
    return title;
  }

  /** Returns the configured content, or {@code null} if unset. */
  public String getContent() {
    return content;
  }

  /** Returns the configured excerpt, or {@code null} if unset. */
  public String getExcerpt() {
    return excerpt;
  }

  /** Returns the configured author ID, or {@code null} if unset. */
  public Integer getAuthor() {
    return author;
  }

  /** Returns the configured featured media ID, or {@code null} if unset. */
  public Integer getFeaturedMedia() {
    return featuredMedia;
  }

  /** Returns the configured taxonomy name, or {@code null} if unset. */
  public String getName() {
    return name;
  }

  /** Returns the configured taxonomy description, or {@code null} if unset. */
  public String getDescription() {
    return description;
  }

  /** Returns the configured comment status, or {@code null} if unset. */
  public CommentStatusEnum getCommentStatus() {
    return enumFromValue(WPCommentStatus.class, commentStatus, true).orElse(null);
  }

  @Override
  public JsonNode build() {
    return JsonSupport.getMapper().valueToTree(this);
  }
}
