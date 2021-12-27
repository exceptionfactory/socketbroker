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
import com.exceptionfactory.socketbroker.configuration.StandardUsernamePasswordAuthenticationCredentials;
import com.exceptionfactory.socketbroker.configuration.UsernamePasswordAuthenticationCredentials;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerSocketBrokeredSocketTest {
    private static final int SELECT_RANDOM_PORT = 0;

    private static final int SERVER_BACKLOG = 1;

    private static final int SOCKET_TIMEOUT = 1000;

    private static final int COMPLETED = 0;

    private static final byte[] PACKET = SocketBroker.class.getSimpleName().getBytes(StandardCharsets.UTF_8);

    private static final SocketFactory SOCKET_FACTORY = SocketFactory.getDefault();

    private static final String USERNAME = String.class.getSimpleName();

    private static final char[] PASSWORD = Character.class.getSimpleName().toCharArray();

    private static final UsernamePasswordAuthenticationCredentials USERNAME_PASSWORD_CREDENTIALS = new StandardUsernamePasswordAuthenticationCredentials(USERNAME, PASSWORD);

    private static ExecutorService executorService;

    private ServerSocket serverSocket;

    private InetSocketAddress serverSocketAddress;

    @BeforeAll
    public static void setExecutorService() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @AfterAll
    public static void shutdownExecutorService() {
        executorService.shutdown();
    }

    @BeforeEach
    public void setServerSocket() throws IOException {
        final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
        serverSocket = new ServerSocket(SELECT_RANDOM_PORT, SERVER_BACKLOG, loopbackAddress);
        serverSocket.setSoTimeout(SOCKET_TIMEOUT);
        serverSocketAddress = new InetSocketAddress(serverSocket.getInetAddress(), serverSocket.getLocalPort());
    }

    @AfterEach
    public void closeServerSocket() throws IOException {
        if (serverSocket == null) {
            throw new SocketException("Server Socket not started");
        }
        serverSocket.close();
    }

    protected static void assertPacketReadWriteCompleted(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final byte[] packet = readPacket(inputStream);
        outputStream.write(packet);

        readPacketCompleted(inputStream);
    }

    protected static UsernamePasswordAuthenticationCredentials getUsernamePasswordCredentials() {
        return USERNAME_PASSWORD_CREDENTIALS;
    }

    protected ServerSocket getServerSocket() {
        return serverSocket;
    }

    protected InetSocketAddress getServerSocketAddress() {
        return serverSocketAddress;
    }

    protected void execute(final Runnable runnable) {
        executorService.execute(runnable);
    }

    protected void assertSocketConnected(final BrokerConfiguration brokerConfiguration, final SocketBroker socketBroker) throws IOException {
        try (BrokeredSocket brokeredSocket = new BrokeredSocket(brokerConfiguration, socketBroker, SOCKET_FACTORY)) {
            brokeredSocket.setSoTimeout(SOCKET_TIMEOUT);
            brokeredSocket.connect(serverSocketAddress, SOCKET_TIMEOUT);

            assertTrue(brokeredSocket.isConnected());
            assertPacketEchoed(brokeredSocket);
        }
    }

    private static byte[] readPacket(final InputStream inputStream) throws IOException {
        final byte[] packet = new byte[PACKET.length];
        final int packetLength = inputStream.read(packet);
        assertEquals(packet.length, packetLength);
        assertArrayEquals(PACKET, packet);
        return packet;
    }

    private static void readPacketCompleted(final InputStream inputStream) throws IOException {
        final int completed = inputStream.read();
        assertEquals(COMPLETED, completed);
    }

    private void assertPacketEchoed(final BrokeredSocket brokeredSocket) throws IOException {
        final OutputStream outputStream = brokeredSocket.getOutputStream();
        final InputStream inputStream = brokeredSocket.getInputStream();

        outputStream.write(PACKET);

        final byte[] packet = new byte[PACKET.length];
        final int packetLength = inputStream.read(packet);
        assertEquals(PACKET.length, packetLength);
        assertArrayEquals(PACKET, packet);

        outputStream.write(COMPLETED);
    }
}
