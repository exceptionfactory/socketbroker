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

import java.util.Objects;

/**
 * Standard implementation of SOCKS 5 Username Password Authentication defined in RFC 1929 Section 2
 */
class StandardSocksUsernamePasswordAuthentication implements SocksUsernamePasswordAuthentication {
    private final String username;

    private final byte[] password;

    /**
     * Username Password Authentication constructor with Username and Password
     *
     * @param username Username
     * @param password Password byte array
     */
    StandardSocksUsernamePasswordAuthentication(final String username, final byte[] password) {
        this.username = Objects.requireNonNull(username, "Username required");
        this.password = Objects.requireNonNull(password, "Password required");
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public byte[] getPassword() {
        return password;
    }
}
