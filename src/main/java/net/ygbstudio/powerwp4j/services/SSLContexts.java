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

package net.ygbstudio.powerwp4j.services;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import net.ygbstudio.powerwp4j.exceptions.SSLConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for {@link SSLContext} instances used by the WordPress client.
 *
 * <p>Provides helpers for default, custom trust-manager, and trust-store configurations.
 */
public class SSLContexts {

  private SSLContexts() {}

  private static final Logger SSLContextsLogger = LoggerFactory.getLogger(SSLContexts.class);
  private static final String SSL_PROTOCOL = "TLS";
  private static final String ERROR_MESSAGE = "Configuration of SSLContext for WordPress failed";

  /**
   * Configures an {@link SSLContext} with the given managers.
   *
   * @param keyStores key managers, or {@code null} for defaults
   * @param trustManagers trust managers, or {@code null} for defaults
   * @param random secure random source, or {@code null} for defaults
   * @return configured SSL context
   * @throws SSLConfigurationException if the TLS algorithm is unavailable or init fails
   */
  private static @NotNull SSLContext configureSSLContext(
      @Nullable KeyManager[] keyStores,
      @Nullable TrustManager[] trustManagers,
      @Nullable SecureRandom random) {
    try {
      SSLContext ctx = SSLContext.getInstance(SSL_PROTOCOL);
      ctx.init(keyStores, trustManagers, random);
      return ctx;
    } catch (KeyManagementException | NoSuchAlgorithmException cause) {
      throw new SSLConfigurationException(ERROR_MESSAGE, cause);
    }
  }

  /**
   * Returns a default TLS context with no custom managers.
   *
   * @return default {@link SSLContext}
   */
  public static @NotNull SSLContext defaultSSLContext() {
    return SSLContexts.configureSSLContext(null, null, null);
  }

  /**
   * Returns an SSL context using the given trust managers.
   *
   * @param trustManagers trust managers to use
   * @return configured {@link SSLContext}
   * @throws SSLConfigurationException if the TLS algorithm is unavailable or init fails
   */
  public static @NotNull SSLContext withTrustManagers(TrustManager[] trustManagers) {
    return SSLContexts.configureSSLContext(null, trustManagers, null);
  }

  /**
   * Returns an SSL context initialized from the given trust store.
   *
   * @param keyStore trust store containing trusted certificates
   * @return configured {@link SSLContext}
   * @throws SSLConfigurationException if the trust store cannot be initialized
   */
  public static @NotNull SSLContext withTrustStore(KeyStore keyStore) {
    try {
      TrustManagerFactory factory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

      factory.init(keyStore);

      TrustManager[] trustManagers = factory.getTrustManagers();

      return SSLContexts.withTrustManagers(trustManagers);
    } catch (NoSuchAlgorithmException | KeyStoreException cause) {
      throw new SSLConfigurationException(ERROR_MESSAGE, cause);
    }
  }
}
