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

import com.exceptionfactory.socketbroker.protocol.socks.field.SocksRequestCommand;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * Standard implementation of SOCKS 5 Request
 */
class StandardSocksRequest implements SocksRequest {
    private final SocksRequestCommand requestCommand;

    private final InetSocketAddress socketAddress;

    /**
     * Request constructor with required properties
     *
     * @param requestCommand Request Command
     * @param socketAddress Internet Socket Address
     */
    StandardSocksRequest(final SocksRequestCommand requestCommand, final InetSocketAddress socketAddress) {
        this.requestCommand = Objects.requireNonNull(requestCommand);
        this.socketAddress = Objects.requireNonNull(socketAddress);
    }

    @Override
    public SocksRequestCommand getRequestCommand() {
        return requestCommand;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }
}
