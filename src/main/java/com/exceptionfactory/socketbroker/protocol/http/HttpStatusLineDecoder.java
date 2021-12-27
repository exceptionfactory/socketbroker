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

import com.exceptionfactory.socketbroker.protocol.PacketDecoder;
import com.exceptionfactory.socketbroker.protocol.PacketDecodingException;
import com.exceptionfactory.socketbroker.protocol.PacketEncoder;
import com.exceptionfactory.socketbroker.protocol.StringEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.CR;
import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.LF;
import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.SPACE;

/**
 * Decoder for HTTP Status Line described in RFC 7230 Section 3.1.2
 */
class HttpStatusLineDecoder implements PacketDecoder<HttpStatusLine> {
    private static final PacketEncoder<String> STRING_ENCODER = new StringEncoder();

    private static final byte[] HTTP_1_1_BYTES = STRING_ENCODER.getEncoded(ProtocolVersion.HTTP_1_1.getProtocol());

    private static final byte[] HTTP_1_0_BYTES = STRING_ENCODER.getEncoded(ProtocolVersion.HTTP_1_0.getProtocol());

    private static final Charset STATUS_CHARACTER_SET = StandardCharsets.US_ASCII;

    private static final int VERSION_BYTES_LENGTH = HTTP_1_0_BYTES.length;

    private static final int STATUS_CODE_LENGTH = 3;

    private static final int END_OF_FILE = -1;

    /**
     * Get Status Line including Protocol Version and Status Code with optional Reason Phrase
     *
     * @param inputStream Encoded Input Stream
     * @return HTTP Status Line
     */
    @Override
    public HttpStatusLine getDecoded(final InputStream inputStream) {
        Objects.requireNonNull(inputStream);

        try {
            final ProtocolVersion protocolVersion = readProtocolVersion(inputStream);
            final int statusCode = readStatusCode(inputStream);
            final String reasonPhrase = readReasonPhrase(inputStream);
            return new StandardHttpStatusLine(protocolVersion, statusCode, reasonPhrase);
        } catch (final IOException e) {
            throw new PacketDecodingException("Read HTTP Status Line failed", e);
        }
    }

    private ProtocolVersion readProtocolVersion(final InputStream inputStream) throws IOException {
        final byte[] versionBytes = new byte[VERSION_BYTES_LENGTH];
        final int bytesRead = inputStream.read(versionBytes);
        if (VERSION_BYTES_LENGTH == bytesRead) {
            return readProtocolVersion(versionBytes);
        } else {
            final String message = String.format("Read HTTP Protocol bytes read [%d] less than required", bytesRead);
            throw new PacketDecodingException(message);
        }
    }

    private ProtocolVersion readProtocolVersion(final byte[] versionBytes) {
        final ProtocolVersion protocolVersion;
        if (Arrays.equals(HTTP_1_0_BYTES, versionBytes)) {
            protocolVersion = ProtocolVersion.HTTP_1_0;
        } else if (Arrays.equals(HTTP_1_1_BYTES, versionBytes)) {
            protocolVersion = ProtocolVersion.HTTP_1_1;
        } else {
            final String version = new String(versionBytes, STATUS_CHARACTER_SET);
            final String message = String.format("Supported HTTP Protocol Version not found [%s]", version);
            throw new PacketDecodingException(message);
        }
        return protocolVersion;
    }

    private int readStatusCode(final InputStream inputStream) throws IOException {
        final int separator = inputStream.read();
        if (SPACE.getDelimiter() == separator) {
            final byte[] statusCodeBytes = new byte[STATUS_CODE_LENGTH];
            final int bytesRead = inputStream.read(statusCodeBytes);
            if (STATUS_CODE_LENGTH == bytesRead) {
                return readStatusCode(statusCodeBytes);
            } else {
                final String message = String.format("Read HTTP Status Code required bytes read [%d] less than required", bytesRead);
                throw new PacketDecodingException(message);
            }
        } else {
            final String message = String.format("Read HTTP Status Code found unexpected separator [%d]", separator);
            throw new PacketDecodingException(message);
        }
    }

    private int readStatusCode(final byte[] statusCodeBytes) {
        final String statusCode = new String(statusCodeBytes, STATUS_CHARACTER_SET);
        try {
            return Integer.parseInt(statusCode);
        } catch (final NumberFormatException e) {
            throw new PacketDecodingException(String.format("Read HTTP Status Code [%s] parsing failed", statusCode));
        }
    }

    private String readReasonPhrase(final InputStream inputStream) throws IOException {
        final StringBuilder builder = new StringBuilder();

        final int separator = inputStream.read();
        if (SPACE.getDelimiter() == separator) {
            int read = inputStream.read();
            while (read != LF.getDelimiter()) {
                if (END_OF_FILE == read) {
                    throw new PacketDecodingException("Read HTTP Reason Phrase failed: EOF found");
                }
                if (CR.getDelimiter() == read) {
                    read = inputStream.read();
                    continue;
                }
                builder.append((char) read);
                read = inputStream.read();
            }
        } else {
            final String message = String.format("Read HTTP Reason Phrase found unexpected separator [%d]", separator);
            throw new PacketDecodingException(message);
        }

        return builder.toString();
    }
}
