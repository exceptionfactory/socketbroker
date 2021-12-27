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

import com.exceptionfactory.socketbroker.protocol.socks.field.SocksReplyStatus;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * Standard implementation of SOCKS 5 Reply
 */
class StandardSocksReply implements SocksReply {
    private final SocksReplyStatus replyStatus;

    private final InetSocketAddress socketAddress;

    /**
     * Reply constructor with required properties
     *
     * @param replyStatus Reply Status
     * @param socketAddress Reply Socket Address
     */
    StandardSocksReply(final SocksReplyStatus replyStatus, final InetSocketAddress socketAddress) {
        this.replyStatus = Objects.requireNonNull(replyStatus, "Reply Status required");
        this.socketAddress = Objects.requireNonNull(socketAddress, "Socket Address required");
    }

    @Override
    public SocksReplyStatus getReplyStatus() {
        return replyStatus;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }
}
