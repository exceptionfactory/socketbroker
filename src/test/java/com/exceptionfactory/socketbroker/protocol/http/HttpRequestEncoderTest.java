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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpRequestEncoderTest {
    private static final RequestMethod REQUEST_METHOD = RequestMethod.CONNECT;

    private static final String HOST_NAME = "localhost";

    private static final int PORT = 80;

    private static final ProtocolVersion PROTOCOL_VERSION = ProtocolVersion.HTTP_1_1;

    private static final String STATUS_LINE = String.format("%s %s:%d %s", REQUEST_METHOD, HOST_NAME, PORT, PROTOCOL_VERSION.getProtocol());

    private static final String HOST_HEADER = String.format("Host: %s:%s", HOST_NAME, PORT);

    private static final String CRLF = "\r\n";

    private static final Charset CHARACTER_SET = StandardCharsets.US_ASCII;

    private static final String HEADER_NAME = "User-Agent";

    private static final String HEADER_VALUE = HttpRequestEncoderTest.class.getSimpleName();

    private static final String USER_AGENT_HEADER = String.format("%s: %s", HEADER_NAME, HEADER_VALUE);

    @Mock
    private HttpRequest request;

    @Mock
    private HttpHeaders headers;

    @Mock
    private HttpHeader header;

    private HttpRequestEncoder encoder;

    @BeforeEach
    public void setDecoder() {
        encoder = new HttpRequestEncoder();

        when(request.getRequestMethod()).thenReturn(REQUEST_METHOD);
        when(request.getHostName()).thenReturn(HOST_NAME);
        when(request.getPort()).thenReturn(PORT);
        when(request.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
        when(request.getHeaders()).thenReturn(headers);
    }

    @Test
    public void testGetEncodedWithoutHeaders() {
        final String expected = STATUS_LINE + CRLF + HOST_HEADER + CRLF + CRLF;

        final byte[] encodedBinary = encoder.getEncoded(request);
        final String encoded = new String(encodedBinary, CHARACTER_SET);
        assertEquals(expected, encoded);
    }

    @Test
    public void testGetEncodedWithHeaders() {
        when(header.getFieldName()).thenReturn(HEADER_NAME);
        when(header.getFieldValue()).thenReturn(HEADER_VALUE);
        when(headers.getHeaders()).thenReturn(Collections.singletonList(header));

        final String expected = STATUS_LINE + CRLF + HOST_HEADER + CRLF + USER_AGENT_HEADER + CRLF + CRLF;

        final byte[] encodedBinary = encoder.getEncoded(request);
        final String encoded = new String(encodedBinary, CHARACTER_SET);
        assertEquals(expected, encoded);
    }
}
