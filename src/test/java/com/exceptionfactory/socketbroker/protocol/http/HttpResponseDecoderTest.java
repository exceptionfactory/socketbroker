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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static com.exceptionfactory.socketbroker.protocol.http.ProtocolVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpResponseDecoderTest {
    private static final int STATUS = 200;

    private static final String REASON = "OK";

    private static final String NAME = "Connection";

    private static final String VALUE = "close";

    private static final String RESPONSE = String.format("%s %d %s\r\n%s: %s\r\n\r\n", HTTP_1_1.getProtocol(), STATUS, REASON, NAME, VALUE);

    private static final Charset CHARACTER_SET = StandardCharsets.US_ASCII;

    private HttpResponseDecoder decoder;

    @BeforeEach
    public void setDecoder() {
        decoder = new HttpResponseDecoder();
    }

    @Test
    public void testGetDecoded() {
        final InputStream inputStream = new ByteArrayInputStream(RESPONSE.getBytes(CHARACTER_SET));
        final HttpResponse httpResponse = decoder.getDecoded(inputStream);

        assertNotNull(httpResponse);
        final HttpStatusLine statusLine = httpResponse.getStatusLine();
        assertNotNull(statusLine);
        assertEquals(HTTP_1_1, statusLine.getProtocolVersion());
        assertEquals(STATUS, statusLine.getStatusCode());
        assertEquals(REASON, statusLine.getReasonPhrase());

        final HttpHeaders headers = httpResponse.getHeaders();
        assertNotNull(headers);
        final Iterator<HttpHeader> headersIterator = headers.getHeaders().iterator();
        assertTrue(headersIterator.hasNext());
        final HttpHeader header = headersIterator.next();
        assertEquals(NAME, header.getFieldName());
        assertEquals(VALUE, header.getFieldValue());
    }
}
