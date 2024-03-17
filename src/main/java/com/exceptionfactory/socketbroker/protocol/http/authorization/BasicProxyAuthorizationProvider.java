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
import com.exceptionfactory.socketbroker.configuration.UsernamePasswordAuthenticationCredentials;
import com.exceptionfactory.socketbroker.protocol.ByteBufferEncoder;
import com.exceptionfactory.socketbroker.protocol.PacketEncoder;
import com.exceptionfactory.socketbroker.protocol.UnicodeStandardCharacterArrayEncoder;
import com.exceptionfactory.socketbroker.protocol.UnicodeStandardStringEncoder;
import com.exceptionfactory.socketbroker.protocol.http.authentication.AuthenticationScheme;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Optional;

/**
 * HTTP Basic implementation of Proxy Authorization Provider based on RFC 7617
 */
public class BasicProxyAuthorizationProvider implements ProxyAuthorizationProvider {
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private static final PacketEncoder<String> STRING_ENCODER = new UnicodeStandardStringEncoder();

    private static final PacketEncoder<char[]> CHARACTER_ARRAY_ENCODER = new UnicodeStandardCharacterArrayEncoder();

    private static final PacketEncoder<ByteBuffer> BYTE_BUFFER_ENCODER = new ByteBufferEncoder();

    private static final byte COLON_SEPARATOR = ':';

    private static final char SPACE_SEPARATOR = ' ';

    private static final int SEPARATOR_LENGTH = 1;

    /**
     * Default constructor for HTTP Basic implementation of Proxy Authorization Provider
     */
    public BasicProxyAuthorizationProvider() {

    }

    /**
     * Get Proxy Authorization Credentials formatted according to RFC 7617 Section 2
     *
     * @param authenticationCredentials Authentication Strategies
     * @return Proxy Authorization Header credentials or empty when not supported
     */
    @Override
    public Optional<String> getCredentials(final AuthenticationCredentials authenticationCredentials) {
        Optional<String> credentials = Optional.empty();
        if (authenticationCredentials instanceof UsernamePasswordAuthenticationCredentials) {
            final UsernamePasswordAuthenticationCredentials usernamePasswordAuthenticationCredentials = (UsernamePasswordAuthenticationCredentials) authenticationCredentials;
            credentials = Optional.of(getCredentials(usernamePasswordAuthenticationCredentials));
        }
        return credentials;
    }

    private String getCredentials(final UsernamePasswordAuthenticationCredentials credential) {
        final StringBuilder builder = new StringBuilder();
        builder.append(AuthenticationScheme.BASIC.getScheme());
        builder.append(SPACE_SEPARATOR);
        final String credentialsEncoded = getCredentialsEncoded(credential);
        builder.append(credentialsEncoded);
        return builder.toString();
    }

    private String getCredentialsEncoded(final UsernamePasswordAuthenticationCredentials credentials) {
        final byte[] username = STRING_ENCODER.getEncoded(credentials.getUsername());
        final byte[] password = CHARACTER_ARRAY_ENCODER.getEncoded(credentials.getPassword());
        final int bufferLength = username.length + password.length + SEPARATOR_LENGTH;

        final ByteBuffer buffer = ByteBuffer.allocate(bufferLength);
        buffer.put(username);
        buffer.put(COLON_SEPARATOR);
        buffer.put(password);

        final byte[] encodedBuffer = BYTE_BUFFER_ENCODER.getEncoded(buffer);
        return ENCODER.encodeToString(encodedBuffer);
    }
}
