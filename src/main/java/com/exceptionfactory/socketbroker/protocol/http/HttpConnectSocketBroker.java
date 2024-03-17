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

import com.exceptionfactory.socketbroker.configuration.AuthenticationCredentials;
import com.exceptionfactory.socketbroker.configuration.BrokerConfiguration;
import com.exceptionfactory.socketbroker.protocol.PacketDecoder;
import com.exceptionfactory.socketbroker.protocol.PacketEncoder;
import com.exceptionfactory.socketbroker.protocol.http.authentication.AuthenticationChallenge;
import com.exceptionfactory.socketbroker.protocol.http.authentication.AuthenticationChallengeParser;
import com.exceptionfactory.socketbroker.protocol.http.authentication.StandardAuthenticationChallengeParser;
import com.exceptionfactory.socketbroker.protocol.http.authorization.BasicProxyAuthorizationProvider;
import com.exceptionfactory.socketbroker.protocol.http.authorization.ProxyAuthorizationProvider;
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
import java.util.stream.Collectors;

/**
 * HTTP CONNECT implementation of Socket Broker based on RFC 7231 Section 4.3.6
 */
public class HttpConnectSocketBroker implements SocketBroker {
    private static final PacketEncoder<HttpRequest> REQUEST_ENCODER = new HttpRequestEncoder();

    private static final PacketDecoder<HttpResponse> RESPONSE_DECODER = new HttpResponseDecoder();

    private static final AuthenticationChallengeParser AUTHENTICATION_CHALLENGE_PARSER = new StandardAuthenticationChallengeParser();

    private static final ProxyAuthorizationProvider BASIC_PROXY_AUTHORIZATION_PROVIDER = new BasicProxyAuthorizationProvider();

    private static final String AUTHENTICATION_REQUIRED = "HTTP Proxy Authentication Required: Status [%d] Reason [%s] Authentication Challenges %s";

    private static final String AUTHENTICATION_FAILED = "HTTP Proxy Authentication Failed: Status [%d] Reason [%s]";

    private static final String CONNECTION_FAILED = "HTTP Connection Failed: Status [%d] Reason [%s]";

    /**
     * Default constructor for HTTP CONNECT implementation of Socket Broker
     */
    public HttpConnectSocketBroker() {

    }

    /**
     * Request connection to remote address through socket connected to HTTP Proxy Server
     *
     * @param socket Socket connected to Proxy Server
     * @param remoteAddress Remote Address for requested connection through Proxy Server
     * @param brokerConfiguration Broker Configuration
     * @throws IOException Thrown on socket communication failures
     * @throws ConnectException Thrown on unsuccessful requests for connection to remote address
     * @throws BrokeredAuthenticationException Thrown on unsuccessful requests indicating proxy authentication required
     */
    @Override
    public void connect(final Socket socket, final InetSocketAddress remoteAddress, final BrokerConfiguration brokerConfiguration) throws IOException {
        Objects.requireNonNull(socket, "Socket required");
        Objects.requireNonNull(remoteAddress, "Remote Address required");

        final OutputStream outputStream = socket.getOutputStream();
        sendConnectRequest(outputStream, remoteAddress, brokerConfiguration);

        final InputStream inputStream = socket.getInputStream();
        final HttpResponse httpResponse = RESPONSE_DECODER.getDecoded(inputStream);
        final HttpStatusLine httpStatusLine = httpResponse.getStatusLine();
        final int statusCode = httpStatusLine.getStatusCode();

        if (isNotSuccessful(statusCode)) {
            final String reasonPhrase = httpStatusLine.getReasonPhrase();
            if (HttpStatusCode.PROXY_AUTHENTICATION_REQUIRED.getStatusCode() == statusCode) {
                final HttpHeaders responseHeaders = httpResponse.getHeaders();
                final List<AuthenticationChallenge> challenges = getAuthenticationChallenges(responseHeaders);
                final String message = String.format(AUTHENTICATION_REQUIRED, statusCode, reasonPhrase, challenges);
                throw new BrokeredAuthenticationException(message);
            } else if (HttpStatusCode.UNAUTHORIZED.getStatusCode() == statusCode) {
                final String message = String.format(AUTHENTICATION_FAILED, statusCode, reasonPhrase);
                throw new BrokeredAuthenticationException(message);
            } else {
                final String message = String.format(CONNECTION_FAILED, statusCode, reasonPhrase);
                throw new ConnectException(message);
            }
        }
    }

    private void sendConnectRequest(final OutputStream outputStream, final InetSocketAddress remoteAddress, final BrokerConfiguration brokerConfiguration) throws IOException {
        final List<HttpHeader> headers = new ArrayList<>();

        final Optional<AuthenticationCredentials> configurationCredentials = brokerConfiguration.getAuthenticationCredentials();
        if (configurationCredentials.isPresent()) {
            final AuthenticationCredentials authenticationCredentials = configurationCredentials.get();
            final Optional<String> providedAuthorization = BASIC_PROXY_AUTHORIZATION_PROVIDER.getCredentials(authenticationCredentials);
            providedAuthorization.ifPresent(authorization -> {
                final HttpHeader proxyAuthorizationHeader = new StandardHttpHeader(RequestHeader.PROXY_AUTHORIZATION.getHeader(), authorization);
                headers.add(proxyAuthorizationHeader);
            });
        }

        final HttpHeaders requestHeaders = new StandardHttpHeaders(headers);
        final HttpRequest httpRequest = new StandardHttpRequest(RequestMethod.CONNECT, remoteAddress.getHostName(), remoteAddress.getPort(), requestHeaders);
        final byte[] httpRequestEncoded = REQUEST_ENCODER.getEncoded(httpRequest);
        outputStream.write(httpRequestEncoded);
        outputStream.flush();
    }

    private boolean isNotSuccessful(final int statusCode) {
        return statusCode != HttpStatusCode.OK.getStatusCode();
    }

    private List<AuthenticationChallenge> getAuthenticationChallenges(final HttpHeaders httpHeaders) {
        return httpHeaders.getHeaders().stream()
                .filter(httpHeader -> ResponseHeader.PROXY_AUTHENTICATE.getHeader().equalsIgnoreCase(httpHeader.getFieldName()))
                .map(httpHeader -> AUTHENTICATION_CHALLENGE_PARSER.getAuthenticationChallenge(httpHeader.getFieldValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
