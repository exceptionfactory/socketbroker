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
import com.exceptionfactory.socketbroker.configuration.UsernamePasswordAuthenticationCredentials;
import com.exceptionfactory.socketbroker.protocol.UnicodeStandardCharacterArrayEncoder;
import com.exceptionfactory.socketbroker.protocol.UnicodeStandardStringEncoder;
import com.exceptionfactory.socketbroker.protocol.socks.Socks5SocketBroker;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAddressType;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationMethod;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationStatus;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksReplyStatus;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksRequestCommand;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksReservedField;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksUsernamePasswordVersion;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksVersion;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationMethod.NO_AUTHENTICATION_REQUIRED;
import static com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationMethod.USERNAME_PASSWORD;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Socks5ServerSocketBrokeredSocketTest extends ServerSocketBrokeredSocketTest {
    private static final int IPV4_ADDRESS_LENGTH = 4;

    private static final UnicodeStandardStringEncoder STRING_ENCODER = new UnicodeStandardStringEncoder();

    private static final UnicodeStandardCharacterArrayEncoder CHARACTER_ARRAY_ENCODER = new UnicodeStandardCharacterArrayEncoder();

    private final Socks5SocketBroker socketBroker = new Socks5SocketBroker();

    @Test
    public void testConnectNoAuthenticationRequired() throws IOException {
        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.SOCKS5, getServerSocketAddress());

        final List<SocksAuthenticationMethod> clientAuthenticationMethods = Collections.singletonList(NO_AUTHENTICATION_REQUIRED);
        final SocksServerCommand socksServerCommand = new SocksServerCommand(getServerSocket(), NO_AUTHENTICATION_REQUIRED, clientAuthenticationMethods);
        execute(socksServerCommand);

        assertSocketConnected(brokerConfiguration, socketBroker);
    }

    @Test
    public void testConnectNoAuthenticationRequiredUsernamePasswordConfigured() throws IOException {
        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.SOCKS5, getServerSocketAddress(), getUsernamePasswordCredentials());

        final List<SocksAuthenticationMethod> clientAuthenticationMethods = Arrays.asList(NO_AUTHENTICATION_REQUIRED, USERNAME_PASSWORD);
        final SocksServerCommand socksServerCommand = new SocksServerCommand(getServerSocket(), NO_AUTHENTICATION_REQUIRED, clientAuthenticationMethods);
        execute(socksServerCommand);

        assertSocketConnected(brokerConfiguration, socketBroker);
    }

    @Test
    public void testConnectUsernamePassword() throws IOException {
        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.SOCKS5, getServerSocketAddress(), getUsernamePasswordCredentials());

        final List<SocksAuthenticationMethod> clientAuthenticationMethods = Arrays.asList(NO_AUTHENTICATION_REQUIRED, USERNAME_PASSWORD);
        final SocksServerCommand socksServerCommand = new SocksServerCommand(getServerSocket(), USERNAME_PASSWORD, clientAuthenticationMethods);
        execute(socksServerCommand);

        assertSocketConnected(brokerConfiguration, socketBroker);
    }

    private static final class SocksServerCommand implements Runnable {
        private final ServerSocket serverSocket;

        private final List<SocksAuthenticationMethod> clientAuthenticationMethods;

        private final SocksAuthenticationMethod serverAuthenticationMethod;

        private final String username;

        private final char[] password;

        private SocksServerCommand(final ServerSocket serverSocket, final SocksAuthenticationMethod serverAuthenticationMethod, final List<SocksAuthenticationMethod> clientAuthenticationMethods) {
            this.serverSocket = serverSocket;
            this.serverAuthenticationMethod = serverAuthenticationMethod;
            this.clientAuthenticationMethods = clientAuthenticationMethods;
            final UsernamePasswordAuthenticationCredentials credentials = getUsernamePasswordCredentials();
            this.username = credentials.getUsername();
            this.password = credentials.getPassword();
        }

        @Override
        public void run() {
            try (Socket socket = serverSocket.accept()) {
                final InputStream inputStream = socket.getInputStream();
                final OutputStream outputStream = socket.getOutputStream();

                readClientVersion(inputStream);
                readClientAuthenticationMethods(inputStream);

                writeServerVersion(outputStream);
                writeServerAuthenticationMethod(outputStream);

                if (SocksAuthenticationMethod.USERNAME_PASSWORD == serverAuthenticationMethod) {
                    readClientAuthenticationVersion(inputStream);
                    readClientAuthenticationUsername(inputStream);
                    readClientAuthenticationPassword(inputStream);

                    writeServerAuthenticationStatus(outputStream);
                }

                readClientVersion(inputStream);
                readClientCommand(inputStream);
                readReserved(inputStream);
                readAddressType(inputStream);
                readAddress(inputStream);
                readPort(inputStream);

                writeServerVersion(outputStream);
                writeServerReplyStatus(outputStream);
                writeServerBoundAddress(outputStream);

                assertPacketReadWriteCompleted(inputStream, outputStream);
            } catch (final IOException e) {
                throw new UncheckedIOException("SOCKS Server Socket processing failed", e);
            }
        }

        private void readClientVersion(final InputStream inputStream) throws IOException {
            final int version = inputStream.read();
            assertEquals(SocksVersion.VERSION_5.getCode(), version);
        }

        private void readClientAuthenticationMethods(final InputStream inputStream) throws IOException {
            final int methods = inputStream.read();
            assertEquals(clientAuthenticationMethods.size(), methods);
            for (final SocksAuthenticationMethod expectedMethod : clientAuthenticationMethods) {
                final int clientMethod = inputStream.read();
                assertEquals(expectedMethod.getCode(), clientMethod);
            }
        }

        private void readClientAuthenticationVersion(final InputStream inputStream) throws IOException {
            final int version = inputStream.read();
            assertEquals(SocksUsernamePasswordVersion.VERSION_1.getCode(), version);
        }

        private void readClientAuthenticationUsername(final InputStream inputStream) throws IOException {
            final int length = inputStream.read();
            assertEquals(username.length(), length);

            final byte[] clientUsername = new byte[length];
            final int usernameLength = inputStream.read(clientUsername);
            assertEquals(username.length(), usernameLength);

            final byte[] expectedUsername = STRING_ENCODER.getEncoded(username);
            assertArrayEquals(expectedUsername, clientUsername);
        }

        private void readClientAuthenticationPassword(final InputStream inputStream) throws IOException {
            final int length = inputStream.read();
            assertEquals(password.length, length);

            final byte[] clientPassword = new byte[length];
            final int passwordLength = inputStream.read(clientPassword);
            assertEquals(password.length, passwordLength);

            final byte[] expectedPassword = CHARACTER_ARRAY_ENCODER.getEncoded(password);
            assertArrayEquals(expectedPassword, clientPassword);
        }

        private void readClientCommand(final InputStream inputStream) throws IOException {
            final int command = inputStream.read();
            assertEquals(SocksRequestCommand.CONNECT.getCommand(), command);
        }

        private void readReserved(final InputStream inputStream) throws IOException {
            final int reserved = inputStream.read();
            assertEquals(SocksReservedField.RESERVED.getCode(), reserved);
        }

        private void readAddressType(final InputStream inputStream) throws IOException {
            final int addressType = inputStream.read();
            assertEquals(SocksAddressType.IP_V4_ADDRESS.getCode(), addressType);
        }

        private void readAddress(final InputStream inputStream) throws IOException {
            final byte[] address = new byte[IPV4_ADDRESS_LENGTH];
            final int addressLength = inputStream.read(address);
            assertEquals(IPV4_ADDRESS_LENGTH, addressLength);
            final InetAddress requestedAddress = InetAddress.getByAddress(address);
            assertEquals(serverSocket.getInetAddress(), requestedAddress);
        }

        private void readPort(final InputStream inputStream) throws IOException {
            final DataInputStream dataInputStream = new DataInputStream(inputStream);
            final int port = dataInputStream.readUnsignedShort();
            assertEquals(serverSocket.getLocalPort(), port);
        }

        private void writeServerVersion(final OutputStream outputStream) throws IOException {
            outputStream.write(SocksVersion.VERSION_5.getCode());
        }

        private void writeServerAuthenticationMethod(final OutputStream outputStream) throws IOException {
            outputStream.write(serverAuthenticationMethod.getCode());
        }

        private void writeServerAuthenticationStatus(final OutputStream outputStream) throws IOException {
            outputStream.write(SocksUsernamePasswordVersion.VERSION_1.getCode());
            outputStream.write(SocksAuthenticationStatus.SUCCESS.getCode());
        }

        private void writeServerReplyStatus(final OutputStream outputStream) throws IOException {
            outputStream.write(SocksReplyStatus.SUCCEEDED.getCode());
            outputStream.write(SocksReservedField.RESERVED.getCode());
        }

        private void writeServerBoundAddress(final OutputStream outputStream) throws IOException {
            final byte[] address = serverSocket.getInetAddress().getAddress();
            outputStream.write(SocksAddressType.IP_V4_ADDRESS.getCode());
            outputStream.write(address);

            final int port = serverSocket.getLocalPort();
            final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeShort(port);
        }
    }
}
