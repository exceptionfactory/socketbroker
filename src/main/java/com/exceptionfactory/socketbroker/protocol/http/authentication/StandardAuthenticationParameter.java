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

import java.util.Objects;

/**
 * Standard implementation of Authentication Parameter with properties
 */
class StandardAuthenticationParameter implements AuthenticationParameter {
    private final String name;

    private final String value;

    /**
     * Standard Authentication Parameter constructor with required properties
     *
     * @param name Name required
     * @param value Value required
     */
    StandardAuthenticationParameter(final String name, final String value) {
        this.name = Objects.requireNonNull(name, "Name required");
        this.value = Objects.requireNonNull(value, "Value required");
    }

    /**
     * Get Authentication Parameter Name
     *
     * @return Authentication Parameter Name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get Authentication Parameter Value
     *
     * @return Authentication Parameter Value
     */
    @Override
    public String getValue() {
        return value;
    }
}
