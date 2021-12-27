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

import com.exceptionfactory.socketbroker.protocol.PacketEncoder;
import com.exceptionfactory.socketbroker.protocol.UnicodeStandardStringEncoder;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksUsernamePasswordVersion;

import java.io.ByteArrayOutputStream;

/**
 * Packet Encoder for SOCKS 5 Username and Password authentication requests
 */
class SocksUsernamePasswordAuthenticationEncoder implements PacketEncoder<SocksUsernamePasswordAuthentication> {
    private static final int START_INDEX = 0;

    private static final PacketEncoder<String> STRING_ENCODER = new UnicodeStandardStringEncoder();

    /**
     * Get Encoded username and password starting with version
     *
     * @param authentication Username and Password Authentication
     * @return Encoded username and password prefixed with version and length fields
     */
    @Override
    public byte[] getEncoded(final SocksUsernamePasswordAuthentication authentication) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(SocksUsernamePasswordVersion.VERSION_1.getCode());

        final String username = authentication.getUsername();
        final byte[] usernameEncoded = STRING_ENCODER.getEncoded(username);

        final byte[] password = authentication.getPassword();
        outputStream.write(usernameEncoded.length);
        outputStream.write(usernameEncoded, START_INDEX, usernameEncoded.length);
        outputStream.write(password.length);
        outputStream.write(password, START_INDEX, password.length);
        return outputStream.toByteArray();
    }
}
