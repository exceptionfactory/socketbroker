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
package com.exceptionfactory.socketbroker.protocol.http.authorization;

import com.exceptionfactory.socketbroker.configuration.AuthenticationCredentials;

import java.util.Optional;

/**
 * Proxy Authorization Provider supplies credentials for HTTP Request Proxy-Authorization Headers
 */
public interface ProxyAuthorizationProvider {
    /**
     * Get Proxy Authorization as described in RFC 7235 Section 4.4
     *
     * @param authenticationCredentials Authentication Credentials
     * @return Proxy Authorization Header credentials or empty when not supported
     */
    Optional<String> getCredentials(AuthenticationCredentials authenticationCredentials);
}
