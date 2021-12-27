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
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BrokeredSocketTest {
    private static final String LOCALHOST = "localhost";

    private static final int PROXY_PORT = 1080;

    private static final InetSocketAddress PROXY_SOCKET_ADDRESS = new InetSocketAddress(LOCALHOST, PROXY_PORT);

    private static final int REMOTE_PORT = 80;

    private static final InetSocketAddress REMOTE_SOCKET_ADDRESS = new InetSocketAddress(LOCALHOST, REMOTE_PORT);

    private static final int TIMEOUT = 5000;

    private static final int PORT_DISCONNECTED = 0;

    private static final BrokerConfiguration BROKER_CONFIGURATION = new StandardBrokerConfiguration(ProxyType.SOCKS5, PROXY_SOCKET_ADDRESS);

    @Mock
    private SocketFactory socketFactory;

    @Mock
    private Socket proxySocket;

    @Mock
    private SocketBroker socketBroker;

    @Mock
    private SocketAddress socketAddress;

    private BrokeredSocket socket;

    @BeforeEach
    public void setFactory() throws IOException {
        when(socketFactory.createSocket()).thenReturn(proxySocket);
        socket = new BrokeredSocket(BROKER_CONFIGURATION, socketBroker, socketFactory);
    }

    @Test
    public void testConnectSocketAddressIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> socket.connect(socketAddress));
    }

    @Test
    public void testConnectProxySocketConnectException() throws IOException {
        doThrow(new ConnectException()).when(proxySocket).connect(any(), anyInt());

        assertThrows(BrokeredConnectException.class, () -> socket.connect(REMOTE_SOCKET_ADDRESS, TIMEOUT));
    }

    @Test
    public void testConnectSocketBrokerConnectException() throws IOException {
        doThrow(new ConnectException()).when(socketBroker).connect(eq(proxySocket), eq(REMOTE_SOCKET_ADDRESS), eq(BROKER_CONFIGURATION));

        assertThrows(BrokeredConnectException.class, () -> socket.connect(REMOTE_SOCKET_ADDRESS, TIMEOUT));
    }

    @Test
    public void testConnect() throws IOException {
        assertNull(socket.getInetAddress());
        assertEquals(PORT_DISCONNECTED, socket.getPort());

        socket.connect(REMOTE_SOCKET_ADDRESS, TIMEOUT);

        assertEquals(REMOTE_SOCKET_ADDRESS.getAddress(), socket.getInetAddress());
        assertEquals(REMOTE_PORT, socket.getPort());

        verify(proxySocket).connect(eq(PROXY_SOCKET_ADDRESS), eq(TIMEOUT));
        verify(socketBroker).connect(eq(proxySocket), eq(REMOTE_SOCKET_ADDRESS), eq(BROKER_CONFIGURATION));
    }

    @Test
    public void testSocketMethods() throws IOException {
        assertNotNull(socket.toString());

        socket.close();
        verify(proxySocket).close();

        socket.getChannel();
        verify(proxySocket).getChannel();

        socket.getInputStream();
        verify(proxySocket).getInputStream();

        socket.getKeepAlive();
        verify(proxySocket).getKeepAlive();

        socket.getLocalSocketAddress();
        verify(proxySocket).getLocalSocketAddress();

        socket.getLocalAddress();
        verify(proxySocket).getLocalAddress();

        socket.getLocalPort();
        verify(proxySocket).getLocalPort();

        socket.getOutputStream();
        verify(proxySocket).getOutputStream();

        socket.getOOBInline();
        verify(proxySocket).getOOBInline();

        socket.getReuseAddress();
        verify(proxySocket).getReuseAddress();

        socket.getReceiveBufferSize();
        verify(proxySocket).getReceiveBufferSize();

        socket.getSendBufferSize();
        verify(proxySocket).getSendBufferSize();

        socket.getSoLinger();
        verify(proxySocket).getSoLinger();

        socket.getSoTimeout();
        verify(proxySocket).getSoLinger();

        socket.getTcpNoDelay();
        verify(proxySocket).getTcpNoDelay();

        socket.getTrafficClass();
        verify(proxySocket).getTrafficClass();

        socket.isBound();
        verify(proxySocket).isBound();

        socket.isConnected();
        verify(proxySocket).isConnected();

        socket.isClosed();
        verify(proxySocket).isClosed();

        socket.isInputShutdown();
        verify(proxySocket).isInputShutdown();

        socket.isOutputShutdown();
        verify(proxySocket).isOutputShutdown();

        socket.setKeepAlive(true);
        verify(proxySocket).setKeepAlive(true);

        socket.setOOBInline(true);
        verify(proxySocket).setOOBInline(true);

        socket.setPerformancePreferences(Integer.SIZE, Integer.SIZE, Integer.SIZE);
        verify(proxySocket).setPerformancePreferences(Integer.SIZE, Integer.SIZE, Integer.SIZE);

        socket.setReceiveBufferSize(Integer.SIZE);
        verify(proxySocket).setReceiveBufferSize(Integer.SIZE);

        socket.setReuseAddress(true);
        verify(proxySocket).setReuseAddress(true);

        socket.setSendBufferSize(Integer.SIZE);
        verify(proxySocket).setSendBufferSize(Integer.SIZE);

        socket.setSoLinger(true, Integer.SIZE);
        verify(proxySocket).setSoLinger(true, Integer.SIZE);

        socket.setSoTimeout(Integer.SIZE);
        verify(proxySocket).setSoTimeout(Integer.SIZE);

        socket.setTcpNoDelay(true);
        verify(proxySocket).setTcpNoDelay(true);

        socket.setTrafficClass(Integer.SIZE);
        verify(proxySocket).setTrafficClass(Integer.SIZE);

        socket.shutdownInput();
        verify(proxySocket).shutdownInput();

        socket.shutdownOutput();
        verify(proxySocket).shutdownOutput();

        socket.sendUrgentData(Integer.MAX_VALUE);
        verify(proxySocket).sendUrgentData(eq(Integer.MAX_VALUE));
    }
}
