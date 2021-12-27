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

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StandardBrokerConfigurationTest {
    private static final InetSocketAddress PROXY_ADDRESS = InetSocketAddress.createUnresolved("localhost", 1080);

    private static final String PASSWORD = "password";

    private static final StandardUsernamePasswordAuthenticationCredentials CREDENTIALS = new StandardUsernamePasswordAuthenticationCredentials("username", PASSWORD.toCharArray());

    @Test
    public void testCredentialsNotConfigured() {
        final StandardBrokerConfiguration configuration = new StandardBrokerConfiguration(ProxyType.SOCKS5, PROXY_ADDRESS);

        assertEquals(ProxyType.SOCKS5, configuration.getProxyType());
        assertEquals(PROXY_ADDRESS, configuration.getProxySocketAddress());
        final Optional<AuthenticationCredentials> authenticationCredentials = configuration.getAuthenticationCredentials();
        assertFalse(authenticationCredentials.isPresent());
    }

    @Test
    public void testCredentialsConfigured() {
        final StandardBrokerConfiguration configuration = new StandardBrokerConfiguration(ProxyType.SOCKS5, PROXY_ADDRESS, CREDENTIALS);

        assertEquals(ProxyType.SOCKS5, configuration.getProxyType());
        assertEquals(PROXY_ADDRESS, configuration.getProxySocketAddress());

        final Optional<AuthenticationCredentials> authenticationCredentials = configuration.getAuthenticationCredentials();
        assertTrue(authenticationCredentials.isPresent());
        assertEquals(CREDENTIALS, authenticationCredentials.get());
    }
}
