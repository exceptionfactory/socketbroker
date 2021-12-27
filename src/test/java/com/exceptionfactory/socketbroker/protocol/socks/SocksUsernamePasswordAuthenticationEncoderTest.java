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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SocksUsernamePasswordAuthenticationEncoderTest {
    private static final String USERNAME = "user";

    private static final int USERNAME_LENGTH = 4;

    private static final String PASSWORD = "word";

    private static final byte[] PASSWORD_BYTES = PASSWORD.getBytes(StandardCharsets.US_ASCII);

    private static final int PASSWORD_LENGTH = 4;

    private static final int VERSION = 1;

    private static final byte[] EXPECTED = new byte[]{VERSION, USERNAME_LENGTH, 117, 115, 101, 114, PASSWORD_LENGTH, 119, 111, 114, 100};

    @Mock
    private SocksUsernamePasswordAuthentication authentication;

    private SocksUsernamePasswordAuthenticationEncoder encoder;

    @BeforeEach
    public void setEncoder() {
        encoder = new SocksUsernamePasswordAuthenticationEncoder();
    }

    @Test
    public void testGetEncoded() {
        when(authentication.getUsername()).thenReturn(USERNAME);
        when(authentication.getPassword()).thenReturn(PASSWORD_BYTES);
        final byte[] encoded = encoder.getEncoded(authentication);

        assertArrayEquals(EXPECTED, encoded);
    }
}
