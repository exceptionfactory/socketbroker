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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class UnicodeStandardCharacterArrayEncoderTest {
    private static final char[] WORD = new char[]{87, 79, 82, 68};

    private static final byte[] WORD_ENCODED = new byte[]{87, 79, 82, 68};

    private static final char[] UNICODE_WORD = new char[]{913, 937};

    private static final byte[] UNICODE_WORD_ENCODED = new byte[]{-50, -111, -50, -87};

    private final UnicodeStandardCharacterArrayEncoder encoder = new UnicodeStandardCharacterArrayEncoder();

    @Test
    public void testGetEncodedWord() {
        final byte[] encoded = encoder.getEncoded(WORD);
        assertArrayEquals(WORD_ENCODED, encoded);
    }

    @Test
    public void testGetEncodedUnicodeWord() {
        final byte[] encoded = encoder.getEncoded(UNICODE_WORD);
        assertArrayEquals(UNICODE_WORD_ENCODED, encoded);
    }
}
