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
package com.exceptionfactory.socketbroker.protocol.http;

/**
 * HTTP Response Header defined in supporting protocol standards
 */
public enum ResponseHeader {
    /** RFC 7235 Section 4.3 */
    PROXY_AUTHENTICATE("Proxy-Authenticate");

    private final String header;

    ResponseHeader(final String header) {
        this.header = header;
    }

    /**
     * Get HTTP header as represented in responses
     *
     * @return HTTP header
     */
    public String getHeader() {
        return header;
    }
}
