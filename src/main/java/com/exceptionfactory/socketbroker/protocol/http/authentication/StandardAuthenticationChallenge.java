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
package com.exceptionfactory.socketbroker.protocol.http.authentication;

import java.util.List;
import java.util.Objects;

/**
 * Standard implementation of Authentication Challenge with associated properties
 */
class StandardAuthenticationChallenge implements AuthenticationChallenge {
    private final String authenticationScheme;

    private final List<AuthenticationParameter> authenticationParameters;

    /**
     * Standard Authentication Challenge constructor with required properties
     *
     * @param authenticationScheme Authentication Scheme required
     * @param authenticationParameters Authentication Parameters required
     */
    StandardAuthenticationChallenge(final String authenticationScheme, final List<AuthenticationParameter> authenticationParameters) {
        this.authenticationScheme = Objects.requireNonNull(authenticationScheme, "Scheme required");
        this.authenticationParameters = Objects.requireNonNull(authenticationParameters, "Parameters required");
    }

    /**
     * Get Authentication Scheme required portion of header
     *
     * @return Authentication Scheme
     */
    @Override
    public String getAuthenticationScheme() {
        return authenticationScheme;
    }

    /**
     * Get Authentication Parameters optional portion of header
     *
     * @return Authentication Parameters not null and can be empty when not provided
     */
    @Override
    public List<AuthenticationParameter> getAuthenticationParameters() {
        return authenticationParameters;
    }

    @Override
    public String toString() {
        return authenticationScheme;
    }
}
