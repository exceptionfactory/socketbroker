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
package com.exceptionfactory.socketbroker.protocol.http.authorization;

import com.exceptionfactory.socketbroker.configuration.AuthenticationCredentials;
import com.exceptionfactory.socketbroker.configuration.StandardUsernamePasswordAuthenticationCredentials;
import com.exceptionfactory.socketbroker.configuration.UsernamePasswordAuthenticationCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class BasicProxyAuthorizationProviderTest {
    private static final String USERNAME = "user";

    private static final String PASSWORD = "password";

    private static final String CREDENTIALS = "Basic dXNlcjpwYXNzd29yZA==";

    private static final byte[] UNICODE_ENCODED = new byte[]{-50, -111, -50, -87};

    private static final String UNICODE_USERNAME = new String(UNICODE_ENCODED, StandardCharsets.UTF_8);

    private static final char[] UNICODE_PASSWORD = Character.toChars(937);

    private static final String UNICODE_CREDENTIALS = "Basic zpHOqTrOqQA=";

    @Mock
    private AuthenticationCredentials authenticationCredentials;

    private BasicProxyAuthorizationProvider provider;

    @BeforeEach
    public void setProvider() {
        provider = new BasicProxyAuthorizationProvider();
    }

    @Test
    public void testGetCredentialsUnsupportedCredentials() {
        final Optional<String> credentials = provider.getCredentials(authenticationCredentials);

        assertFalse(credentials.isPresent());
    }

    @Test
    public void testGetCredentialsUsernamePassword() {
        final UsernamePasswordAuthenticationCredentials usernameCredentials = new StandardUsernamePasswordAuthenticationCredentials(USERNAME, PASSWORD.toCharArray());
        final Optional<String> credentials = provider.getCredentials(usernameCredentials);

        assertTrue(credentials.isPresent());

        final String basicCredentials = credentials.get();
        assertEquals(CREDENTIALS, basicCredentials);
    }

    @Test
    public void testGetCredentialsUsernamePasswordUnicodeString() {
        final UsernamePasswordAuthenticationCredentials usernameCredentials = new StandardUsernamePasswordAuthenticationCredentials(UNICODE_USERNAME, UNICODE_PASSWORD);
        final Optional<String> credentials = provider.getCredentials(usernameCredentials);

        assertTrue(credentials.isPresent());

        final String basicCredentials = credentials.get();
        assertEquals(UNICODE_CREDENTIALS, basicCredentials);
    }
}
