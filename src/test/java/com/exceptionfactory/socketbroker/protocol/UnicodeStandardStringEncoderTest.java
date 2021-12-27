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
package com.exceptionfactory.socketbroker.protocol;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class UnicodeStandardStringEncoderTest {
    private static final String WORD = "WORD";

    private static final byte[] WORD_ENCODED = new byte[]{87, 79, 82, 68};

    private static final byte[] UNICODE_ENCODED = new byte[]{-50, -111, -50, -87};

    private static final String UNICODE_STRING = new String(UNICODE_ENCODED, StandardCharsets.UTF_8);

    private final UnicodeStandardStringEncoder encoder = new UnicodeStandardStringEncoder();

    @Test
    public void testGetEncodedWord() {
        final byte[] encoded = encoder.getEncoded(WORD);
        assertArrayEquals(WORD_ENCODED, encoded);
    }

    @Test
    public void testGetEncodedUnicodeCharacter() {
        final byte[] encoded = encoder.getEncoded(UNICODE_STRING);
        assertArrayEquals(UNICODE_ENCODED, encoded);
    }
}
