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
 * SOCKS 5 Authentication Methods according to RFC 1928 Section 3
 */
public enum SocksAuthenticationMethod implements SocksMessageCode {
    /** No Authentication Required */
    NO_AUTHENTICATION_REQUIRED(0),

    /** General Security Services Application Program Interface method */
    GSSAPI(1),

    /** Username and Password method */
    USERNAME_PASSWORD(2),

    /** No Acceptable Methods */
    NO_ACCEPTABLE_METHODS(255);

    private final int code;

    SocksAuthenticationMethod(final int code) {
        this.code = code;
    }

    /**
     * Get authentication method code as represented in packets
     *
     * @return Authentication methode code
     */
    public int getCode() {
        return code;
    }
}
