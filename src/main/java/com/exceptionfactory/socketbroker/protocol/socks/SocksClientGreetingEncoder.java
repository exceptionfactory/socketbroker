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
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationMethod;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksVersion;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Packet Encoder for SOCKS 5 Client Greeting
 */
class SocksClientGreetingEncoder implements PacketEncoder<SocksClientGreeting> {
    /**
     * Get Encoded SOCKS 5 Client Greeting with version and authentication methods
     *
     * @param clientGreeting SOCKS Client Greeting
     * @return Encoded Client Greeting
     */
    @Override
    public byte[] getEncoded(final SocksClientGreeting clientGreeting) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(SocksVersion.VERSION_5.getCode());

        final List<SocksAuthenticationMethod> authenticationMethods = clientGreeting.getAuthenticationMethods();
        outputStream.write(authenticationMethods.size());
        authenticationMethods.stream().map(SocksAuthenticationMethod::getCode).forEach(outputStream::write);
        return outputStream.toByteArray();
    }
}
