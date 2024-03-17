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

import com.exceptionfactory.socketbroker.configuration.BrokerConfiguration;
import com.exceptionfactory.socketbroker.configuration.AuthenticationCredentials;
import com.exceptionfactory.socketbroker.configuration.UsernamePasswordAuthenticationCredentials;
import com.exceptionfactory.socketbroker.protocol.PacketDecoder;
import com.exceptionfactory.socketbroker.protocol.PacketEncoder;
import com.exceptionfactory.socketbroker.protocol.UnicodeStandardCharacterArrayEncoder;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationMethod;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationStatus;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksReplyStatus;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksRequestCommand;
import com.exceptionfactory.socketbroker.BrokeredAuthenticationException;
import com.exceptionfactory.socketbroker.SocketBroker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * SOCKS Protocol Version 5 implementation of Socket Broker
 */
public class Socks5SocketBroker implements SocketBroker {
    private static final PacketEncoder<char[]> PASSWORD_ENCODER = new UnicodeStandardCharacterArrayEncoder();

    private static final PacketEncoder<SocksClientGreeting> CLIENT_GREETING_ENCODER = new SocksClientGreetingEncoder();

    private static final PacketDecoder<SocksServerAuthentication> SERVER_AUTHENTICATION_DECODER = new SocksServerAuthenticationDecoder();

    private static final PacketEncoder<SocksUsernamePasswordAuthentication> AUTHENTICATION_ENCODER = new SocksUsernamePasswordAuthenticationEncoder();

    private static final PacketEncoder<SocksRequest> REQUEST_ENCODER = new SocksRequestEncoder();

    private static final PacketDecoder<SocksUsernamePasswordStatus> USERNAME_PASSWORD_STATUS_DECODER = new SocksUsernamePasswordStatusDecoder();

    private static final PacketDecoder<SocksReply> REPLY_DECODER = new SocksReplyDecoder();

    /**
     * Default constructor for SOCKS5 implementation of Socket Broker
     */
    public Socks5SocketBroker() {

    }

    /**
     * Connect to SOCKS server using provided Socket and request connection to the specified remote address
     *
     * @param socket Socket configured with remote address of SOCKS server
     * @param remoteAddress Remote address of connection destination
     * @param brokerConfiguration Broker Configuration
     * @throws IOException Thrown on communication failures
     * @throws BrokeredAuthenticationException Thrown on authentication failures when communicating with SOCKS server
     * @throws ConnectException Thrown on SOCKS server connection request failures
     */
    @Override
    public void connect(final Socket socket, final InetSocketAddress remoteAddress, final BrokerConfiguration brokerConfiguration) throws IOException {
        Objects.requireNonNull(socket, "Socket required");
        Objects.requireNonNull(remoteAddress, "Remote Address required");

        final OutputStream outputStream = socket.getOutputStream();
        final List<SocksAuthenticationMethod> authenticationMethods = getAuthenticationMethods(brokerConfiguration);
        final SocksClientGreeting clientGreeting = new StandardSocksClientGreeting(authenticationMethods);
        final byte[] clientGreetingEncoded = CLIENT_GREETING_ENCODER.getEncoded(clientGreeting);
        outputStream.write(clientGreetingEncoded);
        outputStream.flush();

        final InputStream inputStream = socket.getInputStream();
        final SocksServerAuthentication serverAuthentication = SERVER_AUTHENTICATION_DECODER.getDecoded(inputStream);
        processServerAuthentication(serverAuthentication, socket, inputStream, outputStream, brokerConfiguration);

        final SocksRequest request = new StandardSocksRequest(SocksRequestCommand.CONNECT, remoteAddress);
        final byte[] requestEncoded = REQUEST_ENCODER.getEncoded(request);
        outputStream.write(requestEncoded);
        outputStream.flush();

        final SocksReply reply = REPLY_DECODER.getDecoded(inputStream);
        final SocksReplyStatus replyStatus = reply.getReplyStatus();
        if (isNotSuccessful(replyStatus)) {
            final String message = String.format("SOCKS Connect Request Failed: [%s] Status [%d]", replyStatus, replyStatus.getCode());
            throw new ConnectException(message);
        }
    }

    private boolean isNotSuccessful(final SocksReplyStatus replyStatus) {
        return SocksReplyStatus.SUCCEEDED != replyStatus;
    }

    private void processServerAuthentication(final SocksServerAuthentication serverAuthentication,
                                             final Socket socket,
                                             final InputStream inputStream,
                                             final OutputStream outputStream,
                                             final BrokerConfiguration brokerConfiguration
    ) throws IOException {
        final SocksAuthenticationMethod authenticationMethod = serverAuthentication.getAuthenticationMethod();
        if (SocksAuthenticationMethod.NO_ACCEPTABLE_METHODS == authenticationMethod) {
            // Close Connection as required in RFC 1929 Section 2 for authentication failures
            socket.close();
            throw new BrokeredAuthenticationException(String.format("SOCKS Authentication Failed: %s", SocksAuthenticationMethod.NO_ACCEPTABLE_METHODS));
        } else if (SocksAuthenticationMethod.USERNAME_PASSWORD == authenticationMethod) {
            final SocksUsernamePasswordAuthentication usernamePassword = getUsernamePasswordAuthentication(brokerConfiguration);
            final byte[] usernamePasswordEncoded = AUTHENTICATION_ENCODER.getEncoded(usernamePassword);
            outputStream.write(usernamePasswordEncoded);
            outputStream.flush();

            final SocksUsernamePasswordStatus usernamePasswordStatus = USERNAME_PASSWORD_STATUS_DECODER.getDecoded(inputStream);
            final int status = usernamePasswordStatus.getStatus();
            if (SocksAuthenticationStatus.SUCCESS.getCode() != status) {
                final String message = String.format("SOCKS Authentication Failed: Status [%d] Username Password Failed", status);
                throw new BrokeredAuthenticationException(message);
            }
        } else if (SocksAuthenticationMethod.NO_AUTHENTICATION_REQUIRED != authenticationMethod) {
            final String message = String.format("SOCKS Authentication Failed: Server Method not supported [%s]", authenticationMethod);
            throw new BrokeredAuthenticationException(message);
        }
    }

    private SocksUsernamePasswordAuthentication getUsernamePasswordAuthentication(final BrokerConfiguration brokerConfiguration) throws BrokeredAuthenticationException {
        final UsernamePasswordAuthenticationCredentials credentials = getUsernamePasswordAuthenticationCredentials(brokerConfiguration);
        if (credentials == null) {
            throw new BrokeredAuthenticationException("SOCKS Authentication Failed: Server Authentication [Username and Password] not configured");
        }

        final byte[] password = PASSWORD_ENCODER.getEncoded(credentials.getPassword());
        return new StandardSocksUsernamePasswordAuthentication(credentials.getUsername(), password);
    }

    private List<SocksAuthenticationMethod> getAuthenticationMethods(final BrokerConfiguration brokerConfiguration) {
        final List<SocksAuthenticationMethod> authenticationMethods = new ArrayList<>();
        authenticationMethods.add(SocksAuthenticationMethod.NO_AUTHENTICATION_REQUIRED);

        final UsernamePasswordAuthenticationCredentials credentials = getUsernamePasswordAuthenticationCredentials(brokerConfiguration);
        if (credentials != null) {
            authenticationMethods.add(SocksAuthenticationMethod.USERNAME_PASSWORD);
        }

        return authenticationMethods;
    }

    private UsernamePasswordAuthenticationCredentials getUsernamePasswordAuthenticationCredentials(final BrokerConfiguration brokerConfiguration) {
        final Optional<AuthenticationCredentials> authenticationCredentials = brokerConfiguration.getAuthenticationCredentials();
        final AuthenticationCredentials credentials = authenticationCredentials.orElse(null);
        return credentials instanceof UsernamePasswordAuthenticationCredentials ? (UsernamePasswordAuthenticationCredentials) credentials : null;
    }
}
