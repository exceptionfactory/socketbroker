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
package com.exceptionfactory.socketbroker.protocol.socks;

import com.exceptionfactory.socketbroker.protocol.PacketDecodingException;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SocksMessageCodeDecoderTest {
    private static final int UNSUPPORTED_CODE = 0;

    @Mock
    private InputStream inputStream;

    private SocksMessageCodeDecoder<SocksVersion> decoder;

    @BeforeEach
    public void setDecoder() {
        decoder = new SocksMessageCodeDecoder<>(SocksVersion.values());
    }

    @Test
    public void testGetDecodedPacketDecodingException() throws IOException {
        when(inputStream.read()).thenThrow(new IOException());
        assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));
    }

    @Test
    public void testGetDecodedCodeNotSupported() throws IOException {
        when(inputStream.read()).thenReturn(UNSUPPORTED_CODE);
        final PacketDecodingException exception = assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(inputStream));
        assertTrue(exception.getMessage().contains(Integer.toString(UNSUPPORTED_CODE)));
    }

    @Test
    public void testGetDecoded() throws IOException {
        final SocksVersion expected = SocksVersion.VERSION_5;
        when(inputStream.read()).thenReturn(expected.getCode());
        final SocksVersion version = decoder.getDecoded(inputStream);
        assertEquals(expected, version);
    }
}
