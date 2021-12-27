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
package com.exceptionfactory.socketbroker.protocol.socks.field;

/**
 * SOCKS 5 Reply Status defined in RFC 1928 Section 6
 */
public enum SocksReplyStatus implements SocksMessageCode {
    SUCCEEDED(0),

    GENERAL_SERVER_FAILURE(1),

    CONNECTION_NOT_ALLOWED(2),

    NETWORK_UNREACHABLE(3),

    HOST_UNREACHABLE(4),

    CONNECTION_REFUSED(5),

    TIME_TO_LIVE_EXPIRED(6),

    COMMAND_NOT_SUPPORTED(7),

    ADDRESS_TYPE_NOT_SUPPORTED(8);

    private final int code;

    SocksReplyStatus(final int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
