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
 * HTTP Protocol Version
 */
public enum ProtocolVersion {
    HTTP_1_0("HTTP/1.0"),

    HTTP_1_1("HTTP/1.1");

    private final String protocol;

    ProtocolVersion(final String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }
}
