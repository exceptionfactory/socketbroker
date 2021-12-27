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
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationStatus;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksUsernamePasswordVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SocksUsernamePasswordStatusDecoderTest {
    private static final int VERSION = 1;

    private static final byte[] SUCCESS = new byte[]{VERSION, 0};

    @Mock
    private InputStream mockInputStream;

    private SocksUsernamePasswordStatusDecoder decoder;

    @BeforeEach
    public void setDecoder() {
        decoder = new SocksUsernamePasswordStatusDecoder();
    }

    @Test
    public void testGetDecoded() {
        final InputStream inputStream = new ByteArrayInputStream(SUCCESS);
        final SocksUsernamePasswordStatus usernamePasswordStatus = decoder.getDecoded(inputStream);

        assertEquals(SocksUsernamePasswordVersion.VERSION_1, usernamePasswordStatus.getVersion());
        assertEquals(SocksAuthenticationStatus.SUCCESS.getCode(), usernamePasswordStatus.getStatus());
    }

    @Test
    public void testGetDecodedReadStatusException() throws IOException {
        when(mockInputStream.read()).thenReturn(VERSION).thenThrow(new IOException());
        assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(mockInputStream));
    }
}
