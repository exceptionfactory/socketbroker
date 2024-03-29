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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Unicode Standard Character Array Encoder converts provided sources to byte arrays using UTF-8 encoding
 */
public class UnicodeStandardCharacterArrayEncoder implements PacketEncoder<char[]> {
    private static final PacketEncoder<ByteBuffer> BYTE_BUFFER_ENCODER = new ByteBufferEncoder();

    /**
     * Default constructor for Byte Buffer Encoder
     */
    public UnicodeStandardCharacterArrayEncoder() {

    }

    /**
     * Get bytes encoded using UTF-8
     *
     * @param characters Characters to be encoded
     * @return Bytes encoded using UTF-8
     */
    @Override
    public byte[] getEncoded(final char[] characters) {
        final CharBuffer buffer = CharBuffer.wrap(Objects.requireNonNull(characters));
        final ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(buffer);
        return BYTE_BUFFER_ENCODER.getEncoded(byteBuffer);
    }
}
