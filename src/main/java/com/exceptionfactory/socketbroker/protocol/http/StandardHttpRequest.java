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

import java.util.Objects;

/**
 * Standard HTTP Request with HTTP Protocol 1.1
 */
class StandardHttpRequest implements HttpRequest {
    private final RequestMethod requestMethod;

    private final String hostName;

    private final int port;

    private final ProtocolVersion protocolVersion = ProtocolVersion.HTTP_1_1;

    private final HttpHeaders headers;

    StandardHttpRequest(final RequestMethod requestMethod, final String hostName, final int port, final HttpHeaders headers) {
        this.requestMethod = Objects.requireNonNull(requestMethod, "Request Method required");
        this.hostName = Objects.requireNonNull(hostName, "Host Name required");
        this.port = port;
        this.headers = Objects.requireNonNull(headers, "Headers required");
    }

    @Override
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
