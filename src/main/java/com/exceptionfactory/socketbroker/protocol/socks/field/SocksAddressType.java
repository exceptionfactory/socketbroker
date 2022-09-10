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
 * SOCKS 5 Address Type defined in RFC 1928 Section 5
 */
public enum SocksAddressType implements SocksMessageCode {
    /** Internet Protocol Version 4 Address */
    IP_V4_ADDRESS(1),

    /** Domain Name Service Address */
    DOMAIN_NAME(3),

    /** Internet Protocol Version 6 Address */
    IP_V6_ADDRESS(4);

    private final int code;

    SocksAddressType(final int code) {
        this.code = code;
    }

    /**
     * Get address type code as represented in packets
     *
     * @return Address type code
     */
    @Override
    public int getCode() {
        return code;
    }
}
