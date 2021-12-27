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
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksUsernamePasswordVersion;

import java.io.IOException;
import java.io.InputStream;

/**
 * SOCKS 5 Username and Password Packet Decoder expects a supported version with any status
 */
class SocksUsernamePasswordStatusDecoder implements PacketDecoder<SocksUsernamePasswordStatus> {
    private static final SocksMessageCodeDecoder<SocksUsernamePasswordVersion> VERSION_DECODER = new SocksMessageCodeDecoder<>(SocksUsernamePasswordVersion.values());

    /**
     * Get Decoded version and status fields
     *
     * @param inputStream Encoded Input Stream
     * @return SOCKS Username Password Status
     */
    @Override
    public SocksUsernamePasswordStatus getDecoded(final InputStream inputStream) {
        final SocksUsernamePasswordVersion version = VERSION_DECODER.getDecoded(inputStream);
        try {
            final int status = inputStream.read();
            return new StandardSocksUsernamePasswordStatus(version, status);
        } catch (final IOException e) {
            throw new PacketDecodingException("SOCKS Authentication Status read failed", e);
        }
    }
}
