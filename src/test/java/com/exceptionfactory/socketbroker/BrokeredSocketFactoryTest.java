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
package com.exceptionfactory.socketbroker;

import com.exceptionfactory.socketbroker.configuration.BrokerConfiguration;
import com.exceptionfactory.socketbroker.configuration.ProxyType;
import com.exceptionfactory.socketbroker.configuration.StandardBrokerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.SocketFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BrokeredSocketFactoryTest {
    private static final String LOCALHOST = "localhost";

    private static final int PROXY_PORT = 1080;

    private static final int LOCAL_PORT = 1024;

    private static final int REMOTE_PORT = 80;

    private static final String HTTP_RESPONSE_SUCCESS = "HTTP/1.1 200 OK\r\n\r\n";

    private static final byte[] HTTP_RESPONSE_SUCCESS_BYTES = HTTP_RESPONSE_SUCCESS.getBytes(StandardCharsets.US_ASCII);

    private static final int SOCKS_VERSION = 5;

    private static final int SOCKS_NO_AUTHENTICATION = 0;

    private static final int SOCKS_RESERVED = 0;

    private static final int SOCKS_IP_VERSION_4 = 1;

    private static final int SOCKS_SUCCEEDED = 0;

    private static final int SOCKS_PORT_UNSIGNED = 0;

    private static final byte[] SOCKS5_SUCCESS = new byte[]{
            SOCKS_VERSION,
            SOCKS_NO_AUTHENTICATION,
            SOCKS_VERSION,
            SOCKS_RESERVED,
            SOCKS_SUCCEEDED,
            SOCKS_IP_VERSION_4,
            127, 0, 0, 1,
            SOCKS_PORT_UNSIGNED, REMOTE_PORT
    };

    private static final InetSocketAddress PROXY_ADDRESS = new InetSocketAddress(LOCALHOST, PROXY_PORT);

    @Mock
    private SocketFactory socketFactory;

    @Mock
    private Socket proxySocket;

    @BeforeEach
    public void setFactory() throws IOException {
        when(socketFactory.createSocket()).thenReturn(proxySocket);
    }

    @Test
    public void testCreateSocketCreatedSocks5() throws IOException {
        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.SOCKS5, PROXY_ADDRESS);
        final BrokeredSocketFactory brokeredSocketFactory = new BrokeredSocketFactory(brokerConfiguration, socketFactory);

        final Socket socket = brokeredSocketFactory.createSocket();

        assertSocketCreated(socket);
    }

    @Test
    public void testCreateSocketConnectedSocks5() throws IOException {
        setSocks5Success();

        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.SOCKS5, PROXY_ADDRESS);
        final BrokeredSocketFactory brokeredSocketFactory = new BrokeredSocketFactory(brokerConfiguration, socketFactory);
        final Socket socket = brokeredSocketFactory.createSocket(PROXY_ADDRESS.getHostString(), PROXY_ADDRESS.getPort());

        assertSocketConnected(socket, PROXY_ADDRESS);
    }

    @Test
    public void testCreateSocketCreatedHttpConnect() throws IOException {
        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.HTTP_CONNECT, PROXY_ADDRESS);
        final BrokeredSocketFactory brokeredSocketFactory = new BrokeredSocketFactory(brokerConfiguration, socketFactory);
        final Socket socket = brokeredSocketFactory.createSocket();

        assertSocketCreated(socket);
    }

    @Test
    public void testCreateSocketConnectedHttpConnect() throws IOException {
        setHttpConnectSuccess();

        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.HTTP_CONNECT, PROXY_ADDRESS);
        final BrokeredSocketFactory brokeredSocketFactory = new BrokeredSocketFactory(brokerConfiguration, socketFactory);
        final Socket socket = brokeredSocketFactory.createSocket(PROXY_ADDRESS.getHostString(), PROXY_ADDRESS.getPort());

        assertSocketConnected(socket, PROXY_ADDRESS);
    }

    @Test
    public void testCreateSocketConnectedBoundHttpConnect() throws IOException {
        setHttpConnectSuccess();
        when(proxySocket.isBound()).thenReturn(true);

        final InetAddress localAddress = InetAddress.getByName(LOCALHOST);
        final InetSocketAddress requestedSocketAddress = new InetSocketAddress(LOCALHOST, REMOTE_PORT);
        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.HTTP_CONNECT, PROXY_ADDRESS);
        final BrokeredSocketFactory brokeredSocketFactory = new BrokeredSocketFactory(brokerConfiguration, socketFactory);
        final Socket socket = brokeredSocketFactory.createSocket(requestedSocketAddress.getHostString(), requestedSocketAddress.getPort(), localAddress, LOCAL_PORT);

        assertSocketConnected(socket, requestedSocketAddress);
        assertTrue(socket.isBound());
    }

    private void setHttpConnectSuccess() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(proxySocket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(HTTP_RESPONSE_SUCCESS_BYTES);
        when(proxySocket.getInputStream()).thenReturn(inputStream);

        when(proxySocket.isConnected()).thenReturn(true);
    }

    private void setSocks5Success() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(proxySocket.getOutputStream()).thenReturn(outputStream);

        final InputStream inputStream = new ByteArrayInputStream(SOCKS5_SUCCESS);
        when(proxySocket.getInputStream()).thenReturn(inputStream);

        when(proxySocket.isConnected()).thenReturn(true);
    }

    private void assertSocketCreated(final Socket socket) {
        assertNotNull(socket);
        assertFalse(socket.isConnected());
        assertNull(socket.getRemoteSocketAddress());
    }

    private void assertSocketConnected(final Socket socket, final InetSocketAddress requestedSocketAddress) {
        assertNotNull(socket);
        assertTrue(socket.isConnected());

        final SocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
        assertEquals(requestedSocketAddress, remoteSocketAddress);
        assertEquals(requestedSocketAddress.getAddress(), socket.getInetAddress());
    }
}
