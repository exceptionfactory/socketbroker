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

import java.io.InputStream;

/**
 * Decoder for HTTP responses described in RFC 7230 Section 3 with Status Line and Headers
 */
class HttpResponseDecoder implements PacketDecoder<HttpResponse> {

    private static final PacketDecoder<HttpStatusLine> STATUS_LINE_DECODER = new HttpStatusLineDecoder();

    private static final PacketDecoder<HttpHeaders> HEADERS_DECODER = new HttpHeadersDecoder();

    /**
     * Get HTTP Response with Status Line and Headers
     *
     * @param inputStream Encoded Input Stream
     * @return HTTP Response
     */
    @Override
    public HttpResponse getDecoded(final InputStream inputStream) {
        final HttpStatusLine statusLine = STATUS_LINE_DECODER.getDecoded(inputStream);
        final HttpHeaders headers = HEADERS_DECODER.getDecoded(inputStream);
        return new StandardHttpResponse(statusLine, headers);
    }
}
