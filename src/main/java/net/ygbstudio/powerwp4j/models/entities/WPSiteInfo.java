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

import static net.ygbstudio.powerwp4j.utils.Helpers.getPropertiesFromResources;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import net.ygbstudio.powerwp4j.base.extension.enums.EnvironmentScope;
import net.ygbstudio.powerwp4j.exceptions.LocalConfigurationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Holds the site information required to interact with a WordPress site.
 *
 * <p>Stores the WordPress base {@link URI} and application-password credentials.
 *
 * @since 0.1.0
 */
public final class WPSiteInfo {

  private final URI wpURI;
  private final String wpUser;
  private final String wpAppPass;

  /**
   * Creates site info from a WordPress base URI and credentials.
   *
   * @param wpURI URI of the WordPress site (host, scheme and port are extracted)
   * @param wpUser username for the WordPress site
   * @param wpAppPass application password for the WordPress site
   */
  public WPSiteInfo(@NotNull URI wpURI, @NotNull String wpUser, @NotNull String wpAppPass) {
    this.wpURI = wpURI;
    this.wpUser = wpUser;
    this.wpAppPass = wpAppPass;
  }

  /**
   * Returns the base URL of the WordPress REST API.
   *
   * @return the base URL of the WordPress REST API
   */
  @Contract(pure = true)
  public @NotNull String apiBaseUrl() {
    int port = wpURI.getPort();
    return String.format(
        "%s://%s%s/wp-json/wp/v2",
        wpURI.getScheme(), wpURI.getHost(), port == -1 ? "" : ":" + port);
  }

  /**
   * Loads site info from a configuration resource properties file.
   *
   * @param fileName name of the resource file to load properties from
   * @return site info loaded from the resource
   * @throws LocalConfigurationException if the resource is missing or required properties are
   *     absent
   */
  public static @NotNull WPSiteInfo fromConfigResource(String fileName) {
    Optional<Properties> props = getPropertiesFromResources(fileName);
    if (props.isPresent()) {
      Properties appProps = props.get();
      String wpUri = appProps.getProperty(EnvironmentScope.WP_BASE_URI_PROP.value());
      String wpUser = appProps.getProperty(EnvironmentScope.WP_USER_PROP.value());
      String wpAppPass = appProps.getProperty(EnvironmentScope.WP_APPLICATION_PASS_PROP.value());

      if (wpUri == null || wpUser == null || wpAppPass == null)
        throw new LocalConfigurationException(
            "Failed to parse configuration: 'wp.baseURI', 'wp.user', and 'wp.appPass' from classpath resource");

      return new WPSiteInfo(URI.create(wpUri), wpUser, wpAppPass);
    } else {
      throw new LocalConfigurationException("Missing configuration properties file in classpath");
    }
  }

  /**
   * Loads site info from environment variables defined in {@link EnvironmentScope}.
   *
   * @return site info loaded from the environment
   * @throws LocalConfigurationException if any required environment variable is unset
   */
  public static @NotNull WPSiteInfo fromEnv() {
    String uri = System.getenv(EnvironmentScope.WP_BASE_URI_ENV.value());
    String wpUser = System.getenv(EnvironmentScope.WP_USER_ENV.value());
    String wpAppPass = System.getenv(EnvironmentScope.WP_APPLICATION_PASS_ENV.value());
    if (uri == null || wpUser == null || wpAppPass == null)
      throw new LocalConfigurationException(
          "Unset either or all env vars: 'WP_BASE_URI', 'WP_USER', or 'WP_APP_PASS'");

    return new WPSiteInfo(URI.create(uri), wpUser, wpAppPass);
  }

  /**
   * Returns the WordPress base URI.
   *
   * @return base URI of the WordPress site
   */
  public URI wpURI() {
    return wpURI;
  }

  /**
   * Returns the WordPress username.
   *
   * @return application username
   */
  public String wpUser() {
    return wpUser;
  }

  /**
   * Returns the WordPress application password.
   *
   * @return application password
   */
  public String wpAppPass() {
    return wpAppPass;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (WPSiteInfo) obj;
    return Objects.equals(this.wpURI, that.wpURI)
        && Objects.equals(this.wpUser, that.wpUser)
        && Objects.equals(this.wpAppPass, that.wpAppPass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wpURI, wpUser, wpAppPass);
  }

  @Override
  public String toString() {
    return "WPSiteInfo{" + "wpURI=" + wpURI + ", wpUser='" + wpUser + '\'' + '}';
  }
}
