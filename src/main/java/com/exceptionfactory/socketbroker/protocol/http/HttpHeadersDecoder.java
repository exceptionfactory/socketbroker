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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.COLON;
import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.CR;
import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.LF;
import static com.exceptionfactory.socketbroker.protocol.http.HttpDelimiter.SPACE;

/**
 * Decoder for HTTP Headers described in RFC 7230 Section 3.2
 */
class HttpHeadersDecoder implements PacketDecoder<HttpHeaders> {
    private static final int END_OF_FILE = -1;

    /**
     * Get HTTP Headers with a collection of zero or more headers
     *
     * @param inputStream Encoded Input Stream
     * @return HTTP Headers
     */
    @Override
    public HttpHeaders getDecoded(final InputStream inputStream) {
        Objects.requireNonNull(inputStream);

        try {
            return getHeaders(inputStream);
        } catch (final IOException e) {
            throw new PacketDecodingException("Read HTTP Headers failed", e);
        }
    }

    private HttpHeaders getHeaders(final InputStream inputStream) throws IOException {
        final Collection<HttpHeader> headers = new ArrayList<>();

        StringBuilder fieldName = new StringBuilder();
        StringBuilder fieldValue = new StringBuilder();
        boolean readFieldName = true;

        int read = read(inputStream);
        while (read != LF.getDelimiter()) {
            if (COLON.getDelimiter() == read) {
                readFieldName = false;
                read = read(inputStream);
                if (SPACE.getDelimiter() == read) {
                    read = read(inputStream);
                }
                continue;
            } else if (CR.getDelimiter() == read) {
                readFieldName = true;
                read = read(inputStream);

                if (fieldName.length() == 0) {
                    continue;
                } else {
                    final HttpHeader header = new StandardHttpHeader(fieldName.toString(), fieldValue.toString());
                    headers.add(header);

                    fieldName = new StringBuilder();
                    fieldValue = new StringBuilder();
                }
            } else if (readFieldName) {
                fieldName.append((char) read);
            } else {
                fieldValue.append((char) read);
            }

            read = read(inputStream);
        }

        return new StandardHttpHeaders(headers);
    }

    private int read(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        if (END_OF_FILE == read) {
            throw new PacketDecodingException("Read HTTP Headers failed: EOF found");
        }
        return read;
    }
}
