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

import com.exceptionfactory.socketbroker.protocol.PacketDecodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpHeadersDecoderTest {
    private static final String NAME = "Connection";

    private static final String VALUE = "close";

    private static final String FORMATTED_HEADER = String.format("%s: %s", NAME, VALUE);

    private static final String SINGLE_HEADER = String.format("%s: %s\r\n\r\n", NAME, VALUE);

    private static final String NO_SPACE_SINGLE_HEADER = String.format("%s:%s\r\n\r\n", NAME, VALUE);

    private static final String SECOND_NAME = "Server";

    private static final String SECOND_VALUE = HttpHeadersDecoderTest.class.getSimpleName();

    private static final String MULTIPLE_HEADERS = String.format("%s: %s\r\n%s: %s\r\n\r\n", NAME, VALUE, SECOND_NAME, SECOND_VALUE);

    private static final Charset CHARACTER_SET = StandardCharsets.US_ASCII;

    private HttpHeadersDecoder decoder;

    @Mock
    private InputStream mockInputStream;

    @BeforeEach
    public void setDecoder() {
        decoder = new HttpHeadersDecoder();
    }

    @Test
    public void testGetDecodedEmptyInputStream() {
        final InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));
    }

    @Test
    public void testGetDecodedException() throws IOException {
        when(mockInputStream.read()).thenThrow(new IOException());

        assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(mockInputStream));
    }

    @Test
    public void testGetDecodedNoHeaders() {
        final InputStream inputStream = new ByteArrayInputStream(new byte[]{'\r', '\n'});
        final HttpHeaders httpHeaders = decoder.getDecoded(inputStream);

        assertNotNull(httpHeaders);
        assertNotNull(httpHeaders.getHeaders());
        assertTrue(httpHeaders.getHeaders().isEmpty());
    }

    @Test
    public void testGetDecodedSingleHeader() {
        final InputStream inputStream = new ByteArrayInputStream(SINGLE_HEADER.getBytes(CHARACTER_SET));
        final HttpHeaders httpHeaders = decoder.getDecoded(inputStream);

        assertSingleHeaderFound(httpHeaders);
    }

    @Test
    public void testGetDecodedSingleNoSpaceHeader() {
        final InputStream inputStream = new ByteArrayInputStream(NO_SPACE_SINGLE_HEADER.getBytes(CHARACTER_SET));
        final HttpHeaders httpHeaders = decoder.getDecoded(inputStream);

        assertSingleHeaderFound(httpHeaders);
    }

    @Test
    public void testGetDecodedMultipleHeaders() {
        final InputStream inputStream = new ByteArrayInputStream(MULTIPLE_HEADERS.getBytes(CHARACTER_SET));
        final HttpHeaders httpHeaders = decoder.getDecoded(inputStream);

        assertNotNull(httpHeaders);

        final Collection<HttpHeader> headers = httpHeaders.getHeaders();
        assertNotNull(headers);
        assertFalse(headers.isEmpty());

        final Iterator<HttpHeader> iterator = headers.iterator();
        final HttpHeader header = iterator.next();
        assertEquals(NAME, header.getFieldName());
        assertEquals(VALUE, header.getFieldValue());

        final HttpHeader secondHeader = iterator.next();
        assertEquals(SECOND_NAME, secondHeader.getFieldName());
        assertEquals(SECOND_VALUE, secondHeader.getFieldValue());

        assertFalse(iterator.hasNext());
    }

    private void assertSingleHeaderFound(final HttpHeaders httpHeaders) {
        assertNotNull(httpHeaders);

        final Collection<HttpHeader> headers = httpHeaders.getHeaders();
        assertNotNull(headers);
        assertFalse(headers.isEmpty());

        final Iterator<HttpHeader> iterator = headers.iterator();
        final HttpHeader header = iterator.next();
        assertEquals(NAME, header.getFieldName());
        assertEquals(VALUE, header.getFieldValue());

        assertEquals(FORMATTED_HEADER, header.toString());
        assertEquals(Collections.singletonList(FORMATTED_HEADER).toString(), httpHeaders.toString());
    }
}
