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
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksReplyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SocksReplyDecoderTest {
    @Mock
    private InputStream mockInputStream;

    private static final int SOCKS_VERSION = 5;

    private static final int SOCKS_RESERVED = 0;

    private static final int SOCKS_IP_VERSION_4 = 1;

    private static final int SOCKS_DOMAIN_NAME = 3;

    private static final int SOCKS_IP_VERSION_6 = 4;

    private static final int SOCKS_SUCCEEDED = 0;

    private static final int SOCKS_PORT_UNSIGNED = 0;

    private static final String LOCALHOST = "127.0.0.1";

    private static final String LOCALHOST_VERSION_6 = "0:0:0:0:0:0:0:1";

    private static final int REMOTE_PORT = 80;

    private static final String DOMAIN_NAME = "local";

    private static final int DOMAIN_NAME_LENGTH = 5;

    private static final byte[] REPLY_DOMAIN_NAME = new byte[]{
            SOCKS_VERSION,
            SOCKS_SUCCEEDED,
            SOCKS_RESERVED,
            SOCKS_DOMAIN_NAME,
            DOMAIN_NAME_LENGTH,
            108, 111, 99, 97, 108,
            SOCKS_PORT_UNSIGNED,
            REMOTE_PORT
    };

    private static final byte[] REPLY_IP_VERSION_4 = new byte[]{
            SOCKS_VERSION,
            SOCKS_SUCCEEDED,
            SOCKS_RESERVED,
            SOCKS_IP_VERSION_4,
            127, 0, 0, 1,
            SOCKS_PORT_UNSIGNED,
            REMOTE_PORT
    };

    private static final byte[] REPLY_IP_VERSION_6 = new byte[]{
            SOCKS_VERSION,
            SOCKS_SUCCEEDED,
            SOCKS_RESERVED,
            SOCKS_IP_VERSION_6,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
            SOCKS_PORT_UNSIGNED,
            REMOTE_PORT
    };

    private SocksReplyDecoder decoder;

    @BeforeEach
    public void setDecoder() {
        decoder = new SocksReplyDecoder();
    }

    @Test
    public void testGetEncodedException() throws IOException {
        when(mockInputStream.read()).thenReturn(SOCKS_VERSION, SOCKS_RESERVED, SOCKS_SUCCEEDED, SOCKS_DOMAIN_NAME).thenThrow(new IOException());
        assertThrows(PacketDecodingException.class, () -> decoder.getDecoded(mockInputStream));
    }

    @Test
    public void testGetDecodedInternetProtocolVersion4() {
        final InputStream inputStream = new ByteArrayInputStream(REPLY_IP_VERSION_4);
        final SocksReply socksReply = decoder.getDecoded(inputStream);
        assertEquals(SocksReplyStatus.SUCCEEDED, socksReply.getReplyStatus());

        final InetSocketAddress socketAddress = new InetSocketAddress(LOCALHOST, REMOTE_PORT);
        assertEquals(socketAddress, socksReply.getSocketAddress());
    }

    @Test
    public void testGetDecodedInternetProtocolVersion6() {
        final InputStream inputStream = new ByteArrayInputStream(REPLY_IP_VERSION_6);
        final SocksReply socksReply = decoder.getDecoded(inputStream);
        assertEquals(SocksReplyStatus.SUCCEEDED, socksReply.getReplyStatus());

        final InetSocketAddress socketAddress = new InetSocketAddress(LOCALHOST_VERSION_6, REMOTE_PORT);
        assertEquals(socketAddress, socksReply.getSocketAddress());
    }

    @Test
    public void testGetDecodedDomainName() {
        final InputStream inputStream = new ByteArrayInputStream(REPLY_DOMAIN_NAME);
        final SocksReply socksReply = decoder.getDecoded(inputStream);
        assertEquals(SocksReplyStatus.SUCCEEDED, socksReply.getReplyStatus());

        final InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(DOMAIN_NAME, REMOTE_PORT);
        assertEquals(socketAddress, socksReply.getSocketAddress());
    }
}
