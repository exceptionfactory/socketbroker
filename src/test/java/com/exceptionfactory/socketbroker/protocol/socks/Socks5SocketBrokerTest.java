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
package com.exceptionfactory.socketbroker.protocol.socks;

import com.exceptionfactory.socketbroker.BrokeredAuthenticationException;
import com.exceptionfactory.socketbroker.configuration.BrokerConfiguration;
import com.exceptionfactory.socketbroker.configuration.UsernamePasswordAuthenticationCredentials;
import com.exceptionfactory.socketbroker.protocol.PacketDecodingException;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationMethod;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksReplyStatus;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Socks5SocketBrokerTest {
    private static final String LOCALHOST = "localhost";

    private static final int REMOTE_PORT = 80;

    private static final InetSocketAddress REMOTE_ADDRESS = new InetSocketAddress(LOCALHOST, REMOTE_PORT);

    private static final String USERNAME = "user";

    private static final String PASSWORD = "password";

    private static final int SOCKS_VERSION_4 = 4;

    private static final int SOCKS_VERSION = 5;

    private static final int SOCKS_NO_AUTHENTICATION = 0;

    private static final int SOCKS_NO_ACCEPTABLE_METHODS = 255;

    private static final int SOCKS_GSSAPI_METHOD = 1;

    private static final int SOCKS_USERNAME_PASSWORD_METHOD = 2;

    private static final int SOCKS_USERNAME_PASSWORD_VERSION = 1;

    private static final int SOCKS_RESERVED = 0;

    private static final int SOCKS_IP_VERSION_4 = 1;

    private static final int SOCKS_SUCCEEDED = 0;

    private static final int SOCKS_GENERAL_SERVER_FAILURE = 1;

    private static final int SOCKS_PORT_UNSIGNED = 0;

    private static final byte[] SOCKS_VERSION_NOT_SUPPORTED = new byte[]{SOCKS_VERSION_4};

    private static final byte[] SOCKS5_SUCCESS = new byte[]{
            SOCKS_VERSION,
            SOCKS_NO_AUTHENTICATION,
            SOCKS_VERSION,
            SOCKS_SUCCEEDED,
            SOCKS_RESERVED,
            SOCKS_IP_VERSION_4,
            127, 0, 0, 1,
            SOCKS_PORT_UNSIGNED, REMOTE_PORT
    };

    private static final byte[] SOCKS5_USERNAME_PASSWORD_SUCCESS = new byte[]{
            SOCKS_VERSION,
            SOCKS_USERNAME_PASSWORD_METHOD,
            SOCKS_USERNAME_PASSWORD_VERSION,
            SOCKS_SUCCEEDED,
            SOCKS_VERSION,
            SOCKS_SUCCEEDED,
            SOCKS_RESERVED,
            SOCKS_IP_VERSION_4,
            127, 0, 0, 1,
            SOCKS_PORT_UNSIGNED, REMOTE_PORT
    };

    private static final byte[] SOCKS5_USERNAME_PASSWORD_FAILURE = new byte[]{
            SOCKS_VERSION,
            SOCKS_USERNAME_PASSWORD_METHOD,
            SOCKS_USERNAME_PASSWORD_VERSION,
            SOCKS_GENERAL_SERVER_FAILURE
    };

    private static final byte[] SOCKS5_GENERAL_SERVER_FAILURE = new byte[]{
            SOCKS_VERSION,
            SOCKS_NO_AUTHENTICATION,
            SOCKS_VERSION,
            SOCKS_GENERAL_SERVER_FAILURE,
            SOCKS_RESERVED,
            SOCKS_IP_VERSION_4,
            127, 0, 0, 1,
            SOCKS_PORT_UNSIGNED, REMOTE_PORT
    };

    private static final byte[] SOCKS5_USERNAME_PASSWORD_REQUIRED = new byte[]{
            SOCKS_VERSION,
            SOCKS_USERNAME_PASSWORD_METHOD
    };

    private static final byte[] SOCKS5_NO_ACCEPTABLE_METHODS = new byte[]{
            SOCKS_VERSION,
            (byte) SOCKS_NO_ACCEPTABLE_METHODS
    };

    private static final byte[] SOCKS5_GSSAPI_NOT_SUPPORTED = new byte[]{
            SOCKS_VERSION,
            SOCKS_GSSAPI_METHOD
    };

    @Mock
    private BrokerConfiguration configuration;

    @Mock
    private Socket socket;

    @Mock
    private UsernamePasswordAuthenticationCredentials usernamePasswordAuthenticationCredentials;

    private Socks5SocketBroker socketBroker;

    @BeforeEach
    public void setSocketBroker() {
        socketBroker = new Socks5SocketBroker();
    }

    @Test
    public void testConnect() throws IOException {
        when(configuration.getAuthenticationCredentials()).thenReturn(Optional.empty());

        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(SOCKS5_SUCCESS);
        when(socket.getInputStream()).thenReturn(inputStream);

        connect();
    }

    @Test
    public void testConnectUsernamePasswordAuthentication() throws IOException {
        when(usernamePasswordAuthenticationCredentials.getUsername()).thenReturn(USERNAME);
        when(usernamePasswordAuthenticationCredentials.getPassword()).thenReturn(PASSWORD.toCharArray());
        when(configuration.getAuthenticationCredentials()).thenReturn(Optional.of(usernamePasswordAuthenticationCredentials));

        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(SOCKS5_USERNAME_PASSWORD_SUCCESS);
        when(socket.getInputStream()).thenReturn(inputStream);

        connect();
    }

    @Test
    public void testConnectUsernamePasswordAuthenticationFailed() throws IOException {
        when(usernamePasswordAuthenticationCredentials.getUsername()).thenReturn(USERNAME);
        when(usernamePasswordAuthenticationCredentials.getPassword()).thenReturn(PASSWORD.toCharArray());
        when(configuration.getAuthenticationCredentials()).thenReturn(Optional.of(usernamePasswordAuthenticationCredentials));

        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(SOCKS5_USERNAME_PASSWORD_FAILURE);
        when(socket.getInputStream()).thenReturn(inputStream);

        final BrokeredAuthenticationException exception = assertThrows(BrokeredAuthenticationException.class, this::connect);
        assertTrue(exception.getMessage().contains(Integer.toString(SOCKS_GENERAL_SERVER_FAILURE)));
    }

    @Test
    public void testConnectUsernamePasswordAuthenticationRequiredNotConfigured() throws IOException {
        when(configuration.getAuthenticationCredentials()).thenReturn(Optional.empty());

        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(SOCKS5_USERNAME_PASSWORD_REQUIRED);
        when(socket.getInputStream()).thenReturn(inputStream);

        assertThrows(BrokeredAuthenticationException.class, this::connect);
    }

    @Test
    public void testConnectGeneralServerFailureConnectException() throws IOException {
        when(configuration.getAuthenticationCredentials()).thenReturn(Optional.empty());

        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(SOCKS5_GENERAL_SERVER_FAILURE);
        when(socket.getInputStream()).thenReturn(inputStream);

        final ConnectException exception = assertThrows(ConnectException.class, this::connect);
        assertTrue(exception.getMessage().contains(SocksReplyStatus.GENERAL_SERVER_FAILURE.toString()));
    }

    @Test
    public void testConnectSocksVersionNotSupported() throws IOException {
        when(configuration.getAuthenticationCredentials()).thenReturn(Optional.empty());

        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(SOCKS_VERSION_NOT_SUPPORTED);
        when(socket.getInputStream()).thenReturn(inputStream);

        final PacketDecodingException exception = assertThrows(PacketDecodingException.class, this::connect);
        final String message = exception.getMessage();
        assertTrue(message.contains(Integer.toString(SOCKS_VERSION_4)));
    }

    @Test
    public void testConnectNoAcceptableMethods() throws IOException {
        when(configuration.getAuthenticationCredentials()).thenReturn(Optional.empty());

        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(SOCKS5_NO_ACCEPTABLE_METHODS);
        when(socket.getInputStream()).thenReturn(inputStream);

        final BrokeredAuthenticationException exception = assertThrows(BrokeredAuthenticationException.class, this::connect);
        final String message = exception.getMessage();
        assertTrue(message.contains(SocksAuthenticationMethod.NO_ACCEPTABLE_METHODS.name()));

        verify(socket).close();
    }

    @Test
    public void testConnectServerMethodNotSupported() throws IOException {
        when(configuration.getAuthenticationCredentials()).thenReturn(Optional.empty());

        final OutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(SOCKS5_GSSAPI_NOT_SUPPORTED);
        when(socket.getInputStream()).thenReturn(inputStream);

        final BrokeredAuthenticationException exception = assertThrows(BrokeredAuthenticationException.class, this::connect);
        final String message = exception.getMessage();
        assertTrue(message.contains(SocksAuthenticationMethod.GSSAPI.name()));
    }

    private void connect() throws IOException {
        socketBroker.connect(socket, REMOTE_ADDRESS, configuration);
    }
}
