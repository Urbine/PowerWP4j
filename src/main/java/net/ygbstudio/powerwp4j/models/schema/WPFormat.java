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

import net.ygbstudio.powerwp4j.base.extension.enums.PostFormatEnum;

/**
 * Enum class that represents formats in WordPress.
 *
 * @author Yoham Gabriel @ YGBStudio
 * @since 0.1.0
 */
public enum WPFormat implements PostFormatEnum {
  /** The standard post format. */
  STANDARD("standard"),
  /** The aside post format, typically short content without a title. */
  ASIDE("aside"),
  /** The link post format, usually linking to another site. */
  LINK("link"),
  /** The status post format, typically a brief status update. */
  STATUS("status"),
  /** The quote post format, usually a quotation. */
  QUOTE("quote"),
  /** The gallery post format, displaying a collection of images. */
  GALLERY("gallery"),
  /** The chat post format, displaying a conversation. */
  CHAT("chat"),
  /** The video post format, featuring a video. */
  VIDEO("video"),
  /** The audio post format, featuring an audio clip. */
  AUDIO("audio");

  private final String value;

  WPFormat(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
