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
package com.exceptionfactory.socketbroker.protocol.http;

import com.exceptionfactory.socketbroker.configuration.BrokerConfiguration;
import com.exceptionfactory.socketbroker.configuration.UsernamePasswordAuthenticationCredentials;
import com.exceptionfactory.socketbroker.BrokeredAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpConnectSocketBrokerTest {
    private static final String LOCALHOST = "localhost";

    private static final int REMOTE_PORT = 80;

    private static final InetSocketAddress REMOTE_ADDRESS = new InetSocketAddress(LOCALHOST, REMOTE_PORT);

    private static final String RESPONSE_SUCCESS = "HTTP/1.1 200 OK\r\n\r\n";

    private static final String UNAUTHORIZED_STATUS = "401";

    private static final String SERVER_ERROR_STATUS = "500";

    private static final String RESPONSE_UNAUTHORIZED = String.format("HTTP/1.1 %s Unauthorized\r\n\r\n", UNAUTHORIZED_STATUS);

    private static final String RESPONSE_INTERNAL_SERVER_ERROR = String.format("HTTP/1.1 %s Internal Server Error\r\n\r\n", SERVER_ERROR_STATUS);

    private static final String REQUIRED_REASON = "Proxy Authentication Required";

    private static final String BASIC_SCHEME = "Basic";

    private static final String RESPONSE_AUTHENTICATION_REQUIRED = String.format("HTTP/1.1 407 %s\r\nProxy-Authenticate: %s\r\n\r\n", REQUIRED_REASON, BASIC_SCHEME);

    private static final String USERNAME = "user";

    private static final String PASSWORD = "password";

    private static final String CREDENTIALS = "Basic dXNlcjpwYXNzd29yZA==";

    private static final String REQUEST_AUTHORIZATION = String.format("CONNECT %s:%d HTTP/1.1\r\nHost: %s:%d\r\nProxy-Authorization: %s\r\n\r\n",
            LOCALHOST,
            REMOTE_PORT,
            LOCALHOST,
            REMOTE_PORT,
            CREDENTIALS
    );

    private static final Charset CHARACTER_SET = StandardCharsets.US_ASCII;

    @Mock
    private Socket socket;

    @Mock
    private BrokerConfiguration brokerConfiguration;

    @Mock
    private UsernamePasswordAuthenticationCredentials credentials;

    private HttpConnectSocketBroker socketBroker;

    @BeforeEach
    public void setSocketBroker() {
        socketBroker = new HttpConnectSocketBroker();
    }

    @Test
    public void testConnect() throws IOException {
        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(RESPONSE_SUCCESS.getBytes(CHARACTER_SET));
        when(socket.getInputStream()).thenReturn(inputStream);

        connect();
    }

    @Test
    public void testConnectBasicAuthentication() throws IOException {
        when(credentials.getUsername()).thenReturn(USERNAME);
        when(credentials.getPassword()).thenReturn(PASSWORD.toCharArray());
        when(brokerConfiguration.getAuthenticationCredentials()).thenReturn(Optional.of(credentials));

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(RESPONSE_SUCCESS.getBytes(CHARACTER_SET));
        when(socket.getInputStream()).thenReturn(inputStream);

        connect();

        final byte[] outputBytes = outputStream.toByteArray();
        final String request = new String(outputBytes, CHARACTER_SET);
        assertEquals(REQUEST_AUTHORIZATION, request);
    }

    @Test
    public void testConnectProxyAuthenticationRequired() throws IOException {
        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(RESPONSE_AUTHENTICATION_REQUIRED.getBytes(CHARACTER_SET));
        when(socket.getInputStream()).thenReturn(inputStream);

        final BrokeredAuthenticationException exception = assertThrows(BrokeredAuthenticationException.class, this::connect);
        final String message = exception.getMessage();
        assertTrue(message.contains(Integer.toString(HttpStatusCode.PROXY_AUTHENTICATION_REQUIRED.getStatusCode())));
        assertTrue(message.contains(REQUIRED_REASON));
        assertTrue(message.contains(BASIC_SCHEME));
    }

    @Test
    public void testConnectUnauthorized() throws IOException {
        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(RESPONSE_UNAUTHORIZED.getBytes(CHARACTER_SET));
        when(socket.getInputStream()).thenReturn(inputStream);

        final BrokeredAuthenticationException exception = assertThrows(BrokeredAuthenticationException.class, this::connect);
        final String message = exception.getMessage();
        assertTrue(message.contains(UNAUTHORIZED_STATUS));
    }

    @Test
    public void testConnectInternalServerError() throws IOException {
        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(RESPONSE_INTERNAL_SERVER_ERROR.getBytes(CHARACTER_SET));
        when(socket.getInputStream()).thenReturn(inputStream);

        final ConnectException exception = assertThrows(ConnectException.class, this::connect);
        final String message = exception.getMessage();
        assertTrue(message.contains(SERVER_ERROR_STATUS));
    }

    private void connect() throws IOException {
        socketBroker.connect(socket, REMOTE_ADDRESS, brokerConfiguration);
    }
}
