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
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAddressType;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksReplyStatus;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksReservedField;
import com.exceptionfactory.socketbroker.protocol.socks.field.SocksVersion;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Packet Decoder for SOCKS 5 Reply messages described in RFC 1928 Section 6
 */
class SocksReplyDecoder implements PacketDecoder<SocksReply> {
    private static final int IPV4_ADDRESS_LENGTH = 4;

    private static final int IPV6_ADDRESS_LENGTH = 16;

    private static final Charset CHARACTER_SET = StandardCharsets.US_ASCII;

    private static final SocksMessageCodeDecoder<SocksVersion> VERSION_DECODER = new SocksMessageCodeDecoder<>(SocksVersion.values());

    private static final SocksMessageCodeDecoder<SocksReservedField> RESERVED_DECODER = new SocksMessageCodeDecoder<>(SocksReservedField.values());

    private static final SocksMessageCodeDecoder<SocksReplyStatus> REPLY_STATUS_DECODER = new SocksMessageCodeDecoder<>(SocksReplyStatus.values());

    private static final SocksMessageCodeDecoder<SocksAddressType> ADDRESS_TYPE_DECODER = new SocksMessageCodeDecoder<>(SocksAddressType.values());

    /**
     * Get Decoded SOCKS 5 Reply including version and address
     *
     * @param inputStream Encoded Input Stream
     * @return SOCKS 5 Reply
     */
    @Override
    public SocksReply getDecoded(final InputStream inputStream) {
        VERSION_DECODER.getDecoded(inputStream);
        final SocksReplyStatus replyStatus = REPLY_STATUS_DECODER.getDecoded(inputStream);
        RESERVED_DECODER.getDecoded(inputStream);
        try {
            final InetSocketAddress socketAddress = readSocketAddress(inputStream);
            return new StandardSocksReply(replyStatus, socketAddress);
        } catch (final IOException e) {
            throw new PacketDecodingException("Read SOCKS Server Bound Address failed", e);
        }
    }

    private InetSocketAddress readSocketAddress(final InputStream inputStream) throws IOException {
        final SocksAddressType addressType = ADDRESS_TYPE_DECODER.getDecoded(inputStream);
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        return readSocketAddress(dataInputStream, addressType);
    }

    private InetSocketAddress readSocketAddress(final DataInputStream dataInputStream, final SocksAddressType socksAddressType) throws IOException {
        final byte[] address;
        if (SocksAddressType.IP_V6_ADDRESS == socksAddressType) {
            address = new byte[IPV6_ADDRESS_LENGTH];
        } else if (SocksAddressType.DOMAIN_NAME == socksAddressType) {
            final int length = dataInputStream.read();
            address = new byte[length];
        } else {
            address = new byte[IPV4_ADDRESS_LENGTH];
        }
        dataInputStream.readFully(address);
        final int port = dataInputStream.readUnsignedShort();

        if (SocksAddressType.DOMAIN_NAME == socksAddressType) {
            final String host = new String(address, CHARACTER_SET);
            return InetSocketAddress.createUnresolved(host, port);
        } else {
            final InetAddress boundAddress = InetAddress.getByAddress(address);
            return new InetSocketAddress(boundAddress, port);
        }
    }
}
