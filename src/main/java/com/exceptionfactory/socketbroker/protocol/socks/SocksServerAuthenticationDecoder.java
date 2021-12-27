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
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationMethod;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksVersion;

import java.io.InputStream;

/**
 * Packet Decoder for SOCKS 5 Server Authentication responses containing version and selected authentication method
 */
class SocksServerAuthenticationDecoder implements PacketDecoder<SocksServerAuthentication> {
    private static final SocksMessageCodeDecoder<SocksAuthenticationMethod> AUTHENTICATION_METHOD_DECODER = new SocksMessageCodeDecoder<>(SocksAuthenticationMethod.values());

    private static final SocksMessageCodeDecoder<SocksVersion> VERSION_DECODER = new SocksMessageCodeDecoder<>(SocksVersion.values());

    /**
     * Get Decoded Server Authentication reading version and supported method
     *
     * @param inputStream Encoded Input Stream
     * @return SOCKS Server Authentication
     */
    @Override
    public SocksServerAuthentication getDecoded(final InputStream inputStream) {
        VERSION_DECODER.getDecoded(inputStream);
        final SocksAuthenticationMethod authenticationMethod = AUTHENTICATION_METHOD_DECODER.getDecoded(inputStream);
        return new StandardSocksServerAuthentication(authenticationMethod);
    }
}
