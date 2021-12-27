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

import com.exceptionfactory.socketbroker.protocol.socks.field.SocksRequestCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

public class SocksRequestEncoderTest {
    private static final int VERSION = 5;

    private static final int CONNECT = 1;

    private static final int RESERVED = 0;

    private static final int DOMAIN_TYPE = 3;

    private static final String DOMAIN_NAME = "local";

    private static final int DOMAIN_NAME_LENGTH = 5;

    private static final String IP_VERSION_4 = "127.0.0.1";

    private static final int IP_VERSION_4_TYPE = 1;

    private static final String IP_VERSION_6 = "0:0:0:0:0:0:0:1";

    private static final int IP_VERSION_6_TYPE = 4;

    private static final int PORT = 80;

    private static final int MAXIMUM_PORT = 65535;

    private static final byte[] CONNECT_DOMAIN_ENCODED = new byte[]{VERSION, CONNECT, RESERVED, DOMAIN_TYPE, DOMAIN_NAME_LENGTH, 108, 111, 99, 97, 108, 0, PORT};

    private static final byte[] CONNECT_IPV4_ENCODED = new byte[]{VERSION, CONNECT, RESERVED, IP_VERSION_4_TYPE, 127, 0, 0, 1, -1, -1};

    private static final byte[] CONNECT_IPV6_ENCODED = new byte[]{VERSION, CONNECT, RESERVED, IP_VERSION_6_TYPE, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, PORT};

    private SocksRequestEncoder encoder;

    @BeforeEach
    public void setEncoder() {
        encoder = new SocksRequestEncoder();
    }

    @Test
    public void testGetEncodedDomainName() {
        final SocksRequestCommand command = SocksRequestCommand.CONNECT;
        final InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(DOMAIN_NAME, PORT);
        final StandardSocksRequest request = new StandardSocksRequest(command, socketAddress);
        final byte[] encoded = encoder.getEncoded(request);

        Assertions.assertArrayEquals(CONNECT_DOMAIN_ENCODED, encoded);
    }

    @Test
    public void testGetEncodedInternetProtocolVersion4() {
        final SocksRequestCommand command = SocksRequestCommand.CONNECT;
        final InetSocketAddress socketAddress = new InetSocketAddress(IP_VERSION_4, MAXIMUM_PORT);
        final StandardSocksRequest request = new StandardSocksRequest(command, socketAddress);
        final byte[] encoded = encoder.getEncoded(request);

        Assertions.assertArrayEquals(CONNECT_IPV4_ENCODED, encoded);
    }

    @Test
    public void testGetEncodedInternetProtocolVersion6() {
        final SocksRequestCommand command = SocksRequestCommand.CONNECT;
        final InetSocketAddress socketAddress = new InetSocketAddress(IP_VERSION_6, PORT);
        final StandardSocksRequest request = new StandardSocksRequest(command, socketAddress);
        final byte[] encoded = encoder.getEncoded(request);

        Assertions.assertArrayEquals(CONNECT_IPV6_ENCODED, encoded);
    }
}
