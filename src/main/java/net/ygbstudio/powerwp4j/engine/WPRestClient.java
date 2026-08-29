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

package net.ygbstudio.powerwp4j.engine;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;
import javax.net.ssl.SSLContext;
import net.ygbstudio.powerwp4j.base.extension.enums.PostStatusEnum;
import net.ygbstudio.powerwp4j.builders.WPBasicPayloadBuilder;
import net.ygbstudio.powerwp4j.exceptions.MediaUploadException;
import net.ygbstudio.powerwp4j.models.entities.WPPost;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;
import net.ygbstudio.powerwp4j.services.RestClientService;
import net.ygbstudio.powerwp4j.services.SSLContexts;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.jackson.databind.JsonNode;

/**
 * Facade for the WordPress REST API.
 *
 * <p>Wraps {@link RestClientService} with a user-friendly, stateful client bound to a {@link
 * WPSiteInfo} and an {@link SSLContext}. Obtain instances via {@link #of(WPSiteInfo)} or {@link
 * #of(WPSiteInfo, SSLContext)}.
 *
 * @author Yoham Gabriel @ YGBStudio
 */
public class WPRestClient {
  private final WPSiteInfo siteInfo;
  private SSLContext sslContext = SSLContexts.defaultSSLContext();

  /**
   * Creates a client with the default SSL context.
   *
   * @param siteInfo site credentials and base URL
   */
  protected WPRestClient(WPSiteInfo siteInfo) {
    this.siteInfo = siteInfo;
  }

  /**
   * Creates a client with a custom SSL context.
   *
   * @param siteInfo site credentials and base URL
   * @param sslContext SSL context for HTTPS requests
   */
  protected WPRestClient(WPSiteInfo siteInfo, SSLContext sslContext) {
    this(siteInfo);
    this.sslContext = sslContext;
  }

  /**
   * Creates a new instance of {@link WPRestClient} using the provided {@link WPSiteInfo}.
   *
   * @param siteInfo the site information for the WordPress site
   * @return the newly created {@link WPRestClient} instance
   */
  @Contract(value = "_ -> new", pure = true)
  public static @NotNull WPRestClient of(WPSiteInfo siteInfo) {
    return new WPRestClient(siteInfo);
  }

  /**
   * Creates a new client with a custom SSL context.
   *
   * @param siteInfo site credentials and base URL
   * @param sslContext SSL context for HTTPS requests
   * @return newly created instance
   */
  @Contract(value = "_, _ -> new", pure = true)
  public static @NotNull WPRestClient of(WPSiteInfo siteInfo, SSLContext sslContext) {
    return new WPRestClient(siteInfo, sslContext);
  }

  /**
   * Creates a new post.
   *
   * @param payload JSON payload representing the post
   * @return HTTP response with the server body, or {@code null} if the request failed
   */
  @Nullable
  public HttpResponse<String> createPost(JsonNode payload) {
    return RestClientService.postCreate(
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), payload, sslContext);
  }

  /**
   * Deletes a post by ID.
   *
   * @param postId ID of the post to delete
   * @return HTTP response with the server body, or {@code null} if the request failed
   */
  @Nullable
  public HttpResponse<String> deletePost(long postId) {
    return RestClientService.postDelete(
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), postId, sslContext);
  }

  /**
   * Changes the status of a post by building a minimal payload via {@link WPBasicPayloadBuilder}
   * and delegating to {@link RestClientService#changePostStatus}.
   *
   * @param postId ID of the post to update
   * @param status new status to set
   * @return HTTP response with the server body, or {@code null} if the request failed
   */
  @Nullable
  public HttpResponse<String> changePostStatus(long postId, PostStatusEnum status) {
    WPBasicPayloadBuilder builder = WPBasicPayloadBuilder.builder();
    builder.status(status);
    return RestClientService.changePostStatus(
        siteInfo.apiBaseUrl(),
        siteInfo.wpUser(),
        siteInfo.wpAppPass(),
        postId,
        builder.build(),
        sslContext);
  }

  /**
   * Adds a tag.
   *
   * @param payload JSON payload representing the new tag
   * @return HTTP response with the server body, or {@code null} if the request failed
   */
  @Nullable
  public HttpResponse<String> addTag(JsonNode payload) {
    return RestClientService.addTag(
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), payload, sslContext);
  }

  /**
   * Adds a category.
   *
   * @param payload JSON payload representing the new category
   * @return HTTP response with the server body, or {@code null} if the request failed
   */
  @Nullable
  public HttpResponse<String> addCategory(JsonNode payload) {
    return RestClientService.addCategory(
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), payload, sslContext);
  }

  /**
   * Uploads a media file.
   *
   * @param attachmentPath file path of the media to upload
   * @return HTTP response with the server body, or {@code null} if the request failed
   * @throws MediaUploadException if the upload fails
   */
  @Nullable
  public HttpResponse<String> uploadMedia(Path attachmentPath) {
    return RestClientService.uploadMedia(
        siteInfo.apiBaseUrl(),
        siteInfo.wpUser(),
        siteInfo.wpAppPass(),
        attachmentPath,
        null,
        sslContext);
  }

  /**
   * Uploads a media file with an attached JSON payload.
   *
   * @param attachmentPath file path of the media to upload
   * @param payload JSON payload to attach to the media
   * @return HTTP response with the server body, or {@code null} if the request failed
   * @throws MediaUploadException if the upload fails
   */
  @Nullable
  public HttpResponse<String> uploadMedia(Path attachmentPath, JsonNode payload) {
    return RestClientService.uploadMedia(
        siteInfo.apiBaseUrl(),
        siteInfo.wpUser(),
        siteInfo.wpAppPass(),
        attachmentPath,
        payload,
        sslContext);
  }

  /**
   * Fetches a single post by ID.
   *
   * @param id ID of the post to fetch
   * @return optional containing the {@link WPPost} if found, otherwise empty
   */
  public Optional<WPPost> getPost(long id) {
    return Optional.ofNullable(
            RestClientService.postGet(
                siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), id, sslContext))
        .flatMap(res -> JsonSupport.deserialize(res, WPPost.class));
  }
}
