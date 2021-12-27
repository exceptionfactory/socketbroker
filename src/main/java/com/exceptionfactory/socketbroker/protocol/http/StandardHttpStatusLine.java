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
 * Standard implementation of HTTP Status Line with required properties
 */
class StandardHttpStatusLine implements HttpStatusLine {
    private final ProtocolVersion protocolVersion;

    private final int statusCode;

    private final String reasonPhrase;

    StandardHttpStatusLine(final ProtocolVersion protocolVersion, final int statusCode, final String reasonPhrase) {
        this.protocolVersion = Objects.requireNonNull(protocolVersion);
        this.statusCode = statusCode;
        this.reasonPhrase = Objects.requireNonNull(reasonPhrase);
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
