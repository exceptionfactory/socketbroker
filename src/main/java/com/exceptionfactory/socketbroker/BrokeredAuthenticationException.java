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
package com.exceptionfactory.socketbroker;

import java.net.ConnectException;

/**
 * Brokered Authentication Exception indicates that the proxy server did not accept the provided credentials
 */
public class BrokeredAuthenticationException extends ConnectException {
    /**
     * Brokered Authentication Exception constructor with failure message
     *
     * @param message Message describing failure reason
     */
    public BrokeredAuthenticationException(final String message) {
        super(message);
    }
}
