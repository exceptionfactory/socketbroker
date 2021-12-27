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

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Unicode Standard String Encoder converts provided sources to byte arrays using UTF-8 encoding
 */
public class UnicodeStandardStringEncoder implements PacketEncoder<String> {
    /**
     * Get bytes encoded using UTF-8
     *
     * @param packet Packet to be encoded
     * @return Bytes encoded using UTF-8
     */
    @Override
    public byte[] getEncoded(final String packet) {
        return Objects.requireNonNull(packet, "Packet required").getBytes(StandardCharsets.UTF_8);
    }
}
