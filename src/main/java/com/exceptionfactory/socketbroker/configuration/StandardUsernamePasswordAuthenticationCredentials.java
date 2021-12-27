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
package com.exceptionfactory.socketbroker.configuration;

import java.util.Arrays;
import java.util.Objects;

/**
 * Standard implementation of Username and Password Authentication Credentials
 */
public class StandardUsernamePasswordAuthenticationCredentials implements UsernamePasswordAuthenticationCredentials {
    private final String username;

    private final char[] password;

    /**
     * Standard Username and Password Authentication Strategy constructor with required properties
     *
     * @param username Username required
     * @param password Password required
     */
    public StandardUsernamePasswordAuthenticationCredentials(final String username, final char[] password) {
        this.username = Objects.requireNonNull(username, "Username required");
        this.password = Objects.requireNonNull(password, "Password required");
    }

    /**
     * Get Username
     *
     * @return Username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Get Password
     *
     * @return Password
     */
    @Override
    public char[] getPassword() {
        return Arrays.copyOf(password, password.length);
    }
}
