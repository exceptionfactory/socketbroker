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
import java.util.Objects;

/**
 * ByteBuffer Encoder reads populated byte array contents of the provided buffer
 */
public class ByteBufferEncoder implements PacketEncoder<ByteBuffer> {
    /**
     * Get encoded buffer after rewinding and read byte array based on the buffer limit length
     *
     * @param buffer Byte Buffer to be read
     * @return Bytes read
     */
    @Override
    public byte[] getEncoded(final ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "Buffer required");
        buffer.rewind();
        final int length = buffer.limit();
        final byte[] encoded = new byte[length];
        buffer.get(encoded);
        return encoded;
    }
}
