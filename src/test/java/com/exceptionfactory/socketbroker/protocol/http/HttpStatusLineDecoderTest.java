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

import static com.exceptionfactory.socketbroker.protocol.http.ProtocolVersion.HTTP_1_0;
import static com.exceptionfactory.socketbroker.protocol.http.ProtocolVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpStatusLineDecoderTest {
    private static final Charset CHARACTER_SET = StandardCharsets.US_ASCII;

    private static final String UNSUPPORTED_PROTOCOL = "HTTP/1.2";

    private static final char INVALID_SEPARATOR = '0';

    private static final String PROTOCOL_STATUS_NO_SEPARATOR = String.format("%s%c", HTTP_1_1.getProtocol(), INVALID_SEPARATOR);

    private static final String INVALID_STATUS_CHARACTERS = "AAA";

    private static final String PROTOCOL_STATUS_CHARACTERS = String.format("%s %s", HTTP_1_1.getProtocol(), INVALID_STATUS_CHARACTERS);

    private static final String INVALID_STATUS_LENGTH = "10";

    private static final String PROTOCOL_STATUS_INVALID_LENGTH = String.format("%s %s", HTTP_1_1.getProtocol(), INVALID_STATUS_LENGTH);

    private static final int STATUS = 200;

    private static final String REASON = "OK";

    private static final String HTTP_1_0_STATUS = String.format("%s %d %s\r\n", HTTP_1_0.getProtocol(), STATUS, REASON);

    private static final String HTTP_1_1_STATUS = String.format("%s %d %s\r\n", HTTP_1_1.getProtocol(), STATUS, REASON);

    private static final String PROTOCOL_STATUS_INVALID_SEPARATOR = String.format("%s %d%c", HTTP_1_1.getProtocol(), STATUS, INVALID_SEPARATOR);

    private static final String PROTOCOL_STATUS_END_STREAM = String.format("%s %d %s\r", HTTP_1_1.getProtocol(), STATUS, REASON);

    private HttpStatusLineDecoder decoder;

    @Mock
    private InputStream mockInputStream;

    @BeforeEach
    public void setDecoder() {
        decoder = new HttpStatusLineDecoder();
    }

    @Test
    public void testGetDecodedProtocolVersionException() throws IOException {
        when(mockInputStream.read(any())).thenThrow(new IOException());
        assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(mockInputStream));
    }

    @Test
    public void testGetDecodedProtocolVersionInvalidLength() {
        final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));
    }

    @Test
    public void testGetDecodedProtocolVersionUnsupported() {
        final InputStream inputStream = getInputStream(UNSUPPORTED_PROTOCOL);
        final PacketDecodingException exception = assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));

        assertTrue(exception.getMessage().contains(UNSUPPORTED_PROTOCOL));
    }

    @Test
    public void testGetDecodedStatusCodeSeparatorNotFound() {
        final InputStream inputStream = getInputStream(PROTOCOL_STATUS_NO_SEPARATOR);
        final PacketDecodingException exception = assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));

        final String character = Integer.toString(INVALID_SEPARATOR);
        assertTrue(exception.getMessage().contains(character));
    }

    @Test
    public void testGetDecodedStatusCodeInvalidLength() {
        final InputStream inputStream = getInputStream(PROTOCOL_STATUS_INVALID_LENGTH);
        final PacketDecodingException exception = assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));

        final String character = Integer.toString(INVALID_STATUS_LENGTH.length());
        assertTrue(exception.getMessage().contains(character));
    }

    @Test
    public void testGetDecodedStatusCodeNumberFormatException() {
        final InputStream inputStream = getInputStream(PROTOCOL_STATUS_CHARACTERS);
        final PacketDecodingException exception = assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));

        assertTrue(exception.getMessage().contains(INVALID_STATUS_CHARACTERS));
    }

    @Test
    public void testGetDecodedStatusCodeReasonSeparatorNotFound() {
        final InputStream inputStream = getInputStream(PROTOCOL_STATUS_INVALID_SEPARATOR);
        final PacketDecodingException exception = assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));

        final String character = Integer.toString(INVALID_SEPARATOR);
        assertTrue(exception.getMessage().contains(character));
    }

    @Test
    public void testGetDecodedStatusCodeReasonEndStream() {
        final InputStream inputStream = getInputStream(PROTOCOL_STATUS_END_STREAM);
        assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));
    }

    @Test
    public void testGetDecodedHttp10Status() {
        final InputStream inputStream = getInputStream(HTTP_1_0_STATUS);
        final HttpStatusLine httpStatusLine = decoder.getDecoded(inputStream);

        assertEquals(HTTP_1_0, httpStatusLine.getProtocolVersion());
        assertEquals(STATUS, httpStatusLine.getStatusCode());
        assertEquals(REASON, httpStatusLine.getReasonPhrase());
    }

    @Test
    public void testGetDecodedHttp11Status() {
        final InputStream inputStream = getInputStream(HTTP_1_1_STATUS);
        final HttpStatusLine httpStatusLine = decoder.getDecoded(inputStream);

        assertEquals(HTTP_1_1, httpStatusLine.getProtocolVersion());
        assertEquals(STATUS, httpStatusLine.getStatusCode());
        assertEquals(REASON, httpStatusLine.getReasonPhrase());
    }

    private InputStream getInputStream(final String source) {
        return new ByteArrayInputStream(source.getBytes(CHARACTER_SET));
    }
}
