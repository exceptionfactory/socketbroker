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
import java.util.Optional;

/**
 * Broker Configuration for creating proxy socket connections
 */
public interface BrokerConfiguration {
    /**
     * Get Proxy Type
     *
     * @return Proxy Type
     */
    ProxyType getProxyType();

    /**
     * Get Socket Address for Proxy Server
     *
     * @return Proxy Server Socket Address
     */
    InetSocketAddress getProxySocketAddress();

    /**
     * Get credentials for authenticating to proxy server
     *
     * @return Authentication Credentials
     */
    Optional<AuthenticationCredentials> getAuthenticationCredentials();
}
