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
 * Standard implementation of HTTP Header
 */
class StandardHttpHeader implements HttpHeader {
    private final String fieldName;

    private final String fieldValue;

    /**
     * Standard HTTP Header with required fields
     *
     * @param fieldName Header field name required
     * @param fieldValue Header field value required
     */
    StandardHttpHeader(final String fieldName, final String fieldValue) {
        this.fieldName = Objects.requireNonNull(fieldName);
        this.fieldValue = Objects.requireNonNull(fieldValue);
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getFieldValue() {
        return fieldValue;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", fieldName, fieldValue);
    }
}
