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

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ByteBufferEncoderTest {
    private static final byte[] BYTES = new byte[]{1, 2, 3, 4, 5};

    private final ByteBufferEncoder encoder = new ByteBufferEncoder();

    @Test
    public void testGetEncoded() {
        final ByteBuffer buffer = ByteBuffer.wrap(BYTES);
        final byte[] encoded = encoder.getEncoded(buffer);
        assertArrayEquals(BYTES, encoded);
    }

    @Test
    public void testGetEncodedAllocatePutBytes() {
        final ByteBuffer buffer = ByteBuffer.allocate(BYTES.length);
        buffer.put(BYTES);
        final byte[] encoded = encoder.getEncoded(buffer);
        assertArrayEquals(BYTES, encoded);
    }
}
