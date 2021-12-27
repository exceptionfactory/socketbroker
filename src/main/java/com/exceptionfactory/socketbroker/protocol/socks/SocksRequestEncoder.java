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
import com.exceptionfactory.socketbroker.protocol.StringEncoder;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAddressType;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksReservedField;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksVersion;

import java.io.ByteArrayOutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Packet Encoder for SOCKS 5 requests containing command and address
 */
class SocksRequestEncoder implements PacketEncoder<SocksRequest> {
    private static final int START_INDEX = 0;

    private static final byte UNSIGNED_SHORT_FIRST_BYTE = 8;

    private static final PacketEncoder<String> STRING_ENCODER = new StringEncoder();

    /**
     * Get SOCKS 5 encoded request with command
     *
     * @param request SOCKS 5 Request
     * @return Encoded Request starting with version and command
     */
    @Override
    public byte[] getEncoded(final SocksRequest request) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(SocksVersion.VERSION_5.getCode());
        outputStream.write(request.getRequestCommand().getCommand());
        outputStream.write(SocksReservedField.RESERVED.getCode());

        final InetSocketAddress socketAddress = request.getSocketAddress();
        final InetAddress destinationAddress = socketAddress.getAddress();
        if (socketAddress.isUnresolved()) {
            outputStream.write(SocksAddressType.DOMAIN_NAME.getCode());
            final String domainName = socketAddress.getHostString();
            final byte[] domainNameEncoded = STRING_ENCODER.getEncoded(domainName);
            outputStream.write(domainNameEncoded.length);
            outputStream.write(domainNameEncoded, START_INDEX, domainNameEncoded.length);
        } else if (destinationAddress instanceof Inet6Address) {
            outputStream.write(SocksAddressType.IP_V6_ADDRESS.getCode());
            final byte[] address = destinationAddress.getAddress();
            outputStream.write(address, START_INDEX, address.length);
        } else {
            outputStream.write(SocksAddressType.IP_V4_ADDRESS.getCode());
            final byte[] address = destinationAddress.getAddress();
            outputStream.write(address, START_INDEX, address.length);
        }

        final int port = socketAddress.getPort();
        outputStream.write(port >>> UNSIGNED_SHORT_FIRST_BYTE);
        outputStream.write(port);

        return outputStream.toByteArray();
    }
}
