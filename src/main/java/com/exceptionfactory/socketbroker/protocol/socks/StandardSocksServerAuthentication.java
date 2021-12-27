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

import com.exceptionfactory.socketbroker.protocol.socks.field.SocksAuthenticationMethod;

import java.util.Objects;

/**
 * Standard SOCKS 5 Server Authentication response containing selected method described in RFC 1928 Section 3
 */
class StandardSocksServerAuthentication implements SocksServerAuthentication {
    private final SocksAuthenticationMethod authenticationMethod;

    /**
     * Standard SOCKS Server Authentication with required properties
     *
     * @param authenticationMethod Selected server authentication method
     */
    StandardSocksServerAuthentication(final SocksAuthenticationMethod authenticationMethod) {
        this.authenticationMethod = Objects.requireNonNull(authenticationMethod);
    }

    @Override
    public SocksAuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }
}
