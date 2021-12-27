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

import com.exceptionfactory.socketbroker.protocol.PacketEncoder;
import com.exceptionfactory.socketbroker.protocol.StringEncoder;

import java.util.Objects;

import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.COLON;
import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.CR;
import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.LF;
import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.SPACE;

/**
 * HTTP Request Encoder implementation based on RFC 7230 Section 3
 */
class HttpRequestEncoder implements PacketEncoder<HttpRequest> {
    private static final PacketEncoder<String> STRING_ENCODER = new StringEncoder();

    /**
     * Get HTTP Request encoded as string with Request Line and Host Header
     *
     * @param httpRequest HTTP Request to be encoded
     * @return Encoded HTTP Request UTF-8 bytes
     */
    @Override
    public byte[] getEncoded(final HttpRequest httpRequest) {
        Objects.requireNonNull(httpRequest);
        final StringBuilder builder = new StringBuilder();

        builder.append(httpRequest.getRequestMethod());
        builder.append(SPACE.getDelimiter());
        builder.append(httpRequest.getHostName());
        builder.append(COLON.getDelimiter());
        builder.append(httpRequest.getPort());
        builder.append(SPACE.getDelimiter());
        builder.append(httpRequest.getProtocolVersion().getProtocol());
        builder.append(CR.getDelimiter());
        builder.append(LF.getDelimiter());

        builder.append(RequestHeader.HOST.getHeader());
        builder.append(COLON.getDelimiter());
        builder.append(SPACE.getDelimiter());
        builder.append(httpRequest.getHostName());
        builder.append(COLON.getDelimiter());
        builder.append(httpRequest.getPort());
        builder.append(CR.getDelimiter());
        builder.append(LF.getDelimiter());

        final HttpHeaders headers = httpRequest.getHeaders();
        for (final HttpHeader header : headers.getHeaders()) {
            builder.append(header.getFieldName());
            builder.append(COLON.getDelimiter());
            builder.append(SPACE.getDelimiter());
            builder.append(header.getFieldValue());
            builder.append(CR.getDelimiter());
            builder.append(LF.getDelimiter());
        }

        builder.append(CR.getDelimiter());
        builder.append(LF.getDelimiter());

        return STRING_ENCODER.getEncoded(builder.toString());
    }
}
