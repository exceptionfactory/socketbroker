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

import com.exceptionfactory.socketbroker.protocol.PacketDecoder;
import com.exceptionfactory.socketbroker.protocol.PacketDecodingException;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksMessageCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Generalized Decoder for Message Code enumerations
 *
 * @param <T> Message Code Type
 */
class SocksMessageCodeDecoder<T extends SocksMessageCode> implements PacketDecoder<T> {
    private final List<T> messageCodes;

    private final Class<? extends SocksMessageCode> messageCodeClass;

    /**
     * SOCKS Message Decoder Constructor with supported message codes required
     *
     * @param messageCodes Supported Message Codes
     */
    SocksMessageCodeDecoder(final T[] messageCodes) {
        this.messageCodes = Arrays.asList(messageCodes);
        this.messageCodeClass = messageCodes[0].getClass();
    }

    /**
     * Get Message Code based on code read from Input Stream
     *
     * @param inputStream Encoded Input Stream
     * @return Message Code
     */
    @Override
    public T getDecoded(final InputStream inputStream) {
        try {
            final int code = inputStream.read();
            return getMessageCode(code);
        } catch (final IOException e) {
            final String message = String.format("Read [%s] Message Code failed", messageCodeClass.getSimpleName());
            throw new PacketDecodingException(message, e);
        }
    }

    private T getMessageCode(final int code) {
        final Optional<T> foundMessageCode = messageCodes.stream()
                .filter(value -> value.getCode() == code)
                .findFirst();
        return foundMessageCode.orElseThrow(() -> {
            final String message = String.format("[%s] Message Code [%d] not supported", messageCodeClass.getSimpleName(), code);
            return new PacketDecodingException(message);
        });
    }
}
