/**
 * Copyright 2021 Socket Broker Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exceptionfactory.socketbroker.configuration;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;

/**
 * Standard implementation of Broker Configuration
 */
public class StandardBrokerConfiguration implements BrokerConfiguration {
    private final ProxyType proxyType;

    private final InetSocketAddress proxySocketAddress;

    private final AuthenticationCredentials authenticationCredentials;

    /**
     * Standard Broker Configuration without authentication credentials
     *
     * @param proxyType Proxy Type required
     * @param proxySocketAddress Proxy Socket Address required
     */
    public StandardBrokerConfiguration(final ProxyType proxyType,
                                       final InetSocketAddress proxySocketAddress) {
        this.proxyType = Objects.requireNonNull(proxyType);
        this.proxySocketAddress = Objects.requireNonNull(proxySocketAddress);
        this.authenticationCredentials = null;
    }

    /**
     * Standard Broker Configuration with authentication credentials
     *
     * @param proxyType Proxy Type required
     * @param proxySocketAddress Proxy Socket Address required
     * @param authenticationCredentials Authentication Credentials required
     */
    public StandardBrokerConfiguration(final ProxyType proxyType,
                                final InetSocketAddress proxySocketAddress,
                                final AuthenticationCredentials authenticationCredentials) {
        this.proxyType = Objects.requireNonNull(proxyType);
        this.proxySocketAddress = Objects.requireNonNull(proxySocketAddress);
        this.authenticationCredentials = Objects.requireNonNull(authenticationCredentials);
    }

    /**
     * Get configured Proxy Type
     *
     * @return Proxy Type
     */
    @Override
    public ProxyType getProxyType() {
        return proxyType;
    }

    /**
     * Get Proxy Server Socket Address containing address and port number
     *
     * @return Proxy Server Socket Address
     */
    @Override
    public InetSocketAddress getProxySocketAddress() {
        return proxySocketAddress;
    }

    /**
     * Get optional authentication credentials
     *
     * @return Authentication Credentials
     */
    @Override
    public Optional<AuthenticationCredentials> getAuthenticationCredentials() {
        return Optional.ofNullable(authenticationCredentials);
    }
}
