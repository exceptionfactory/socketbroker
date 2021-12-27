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

import com.exceptionfactory.socketbroker.configuration.AuthenticationCredentials;
import com.exceptionfactory.socketbroker.configuration.BrokerConfiguration;
import com.exceptionfactory.socketbroker.configuration.ProxyType;
import com.exceptionfactory.socketbroker.configuration.StandardBrokerConfiguration;
import com.exceptionfactory.socketbroker.protocol.UnicodeStandardStringEncoder;
import com.exceptionfactory.socketbroker.protocol.http.HttpConnectSocketBroker;
import com.exceptionfactory.socketbroker.protocol.http.authorization.BasicProxyAuthorizationProvider;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpConnectServerSocketBrokeredSocketTest extends ServerSocketBrokeredSocketTest {
    private static final String HTTP_STATUS_LINE_OK = "HTTP/1.1 200 OK";

    private static final String CRLF = "\r\n";

    private static final String EMPTY = "";

    private static final String PROXY_AUTHORIZATION = "Proxy-Authorization: %s";

    private static final String CONNECT_COMMAND = "CONNECT %s:%d HTTP/1.1";

    private static final String HOST_HEADER = "Host: %s:%d";

    private static final Charset REQUEST_CHARSET = StandardCharsets.US_ASCII;

    private static final UnicodeStandardStringEncoder STRING_ENCODER = new UnicodeStandardStringEncoder();

    private static final BasicProxyAuthorizationProvider BASIC_PROXY_AUTHORIZATION_PROVIDER = new BasicProxyAuthorizationProvider();

    private final HttpConnectSocketBroker socketBroker = new HttpConnectSocketBroker();

    @Test
    public void testConnectNoAuthenticationRequired() throws IOException {
        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.HTTP_CONNECT, getServerSocketAddress());

        final HttpServerCommand httpServerCommand = new HttpServerCommand(getServerSocket(), EMPTY);
        execute(httpServerCommand);

        assertSocketConnected(brokerConfiguration, socketBroker);
    }

    @Test
    public void testConnectUsernamePasswordProxyAuthorizationBasicCredentials() throws IOException {
        final AuthenticationCredentials authenticationCredentials = getUsernamePasswordCredentials();
        final BrokerConfiguration brokerConfiguration = new StandardBrokerConfiguration(ProxyType.HTTP_CONNECT, getServerSocketAddress(), authenticationCredentials);

        final String credentials = BASIC_PROXY_AUTHORIZATION_PROVIDER.getCredentials(authenticationCredentials).orElseThrow(IllegalArgumentException::new);
        final String proxyAuthorizationHeader = String.format(PROXY_AUTHORIZATION, credentials);

        final HttpServerCommand httpServerCommand = new HttpServerCommand(getServerSocket(), proxyAuthorizationHeader);
        execute(httpServerCommand);

        assertSocketConnected(brokerConfiguration, socketBroker);
    }

    private static final class HttpServerCommand implements Runnable {
        private final ServerSocket serverSocket;

        private final String proxyAuthorizationHeader;

        private HttpServerCommand(final ServerSocket serverSocket, final String proxyAuthorizationHeader) {
            this.serverSocket = serverSocket;
            this.proxyAuthorizationHeader = proxyAuthorizationHeader;
        }

        @Override
        public void run() {
            try (Socket socket = serverSocket.accept()) {
                final InputStream inputStream = socket.getInputStream();
                final OutputStream outputStream = socket.getOutputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, REQUEST_CHARSET));

                readConnectCommand(reader);
                readHostHeader(reader);

                if (EMPTY.equals(proxyAuthorizationHeader)) {
                    readDelimiter(reader);
                } else {
                    readProxyAuthorization(reader);
                    readDelimiter(reader);
                }

                writeServerStatusLine(outputStream);
                writeServerDelimiter(outputStream);
                writeServerDelimiter(outputStream);

                assertPacketReadWriteCompleted(inputStream, outputStream);
            } catch (final IOException e) {
                throw new UncheckedIOException("HTTP Server Socket processing failed", e);
            }
        }

        private void readConnectCommand(final BufferedReader reader) throws IOException {
            final String connectCommand = reader.readLine();

            final String expectedHostName = serverSocket.getInetAddress().getHostName();
            final String expectedCommand = String.format(CONNECT_COMMAND, expectedHostName, serverSocket.getLocalPort());
            assertEquals(expectedCommand, connectCommand);
        }

        private void readHostHeader(final BufferedReader reader) throws IOException {
            final String hostHeader = reader.readLine();

            final String expectedHostName = serverSocket.getInetAddress().getHostName();
            final String expectedHostHeader = String.format(HOST_HEADER, expectedHostName, serverSocket.getLocalPort());
            assertEquals(expectedHostHeader, hostHeader);
        }

        private void readDelimiter(final BufferedReader reader) throws IOException {
            final String delimiter = reader.readLine();
            assertEquals(EMPTY, delimiter);
        }

        private void readProxyAuthorization(final BufferedReader reader) throws IOException {
            final String clientProxyAuthorization = reader.readLine();
            assertEquals(proxyAuthorizationHeader, clientProxyAuthorization);
        }

        private void writeServerStatusLine(final OutputStream outputStream) throws IOException {
            outputStream.write(STRING_ENCODER.getEncoded(HTTP_STATUS_LINE_OK));
        }

        private void writeServerDelimiter(final OutputStream outputStream) throws IOException {
            outputStream.write(STRING_ENCODER.getEncoded(CRLF));
        }
    }
}
