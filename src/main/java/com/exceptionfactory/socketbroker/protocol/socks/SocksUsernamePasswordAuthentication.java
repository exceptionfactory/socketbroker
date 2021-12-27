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
package com.exceptionfactory.socketbroker.protocol.socks;

/**
 * SOCKS 5 Username Password Authentication defined in RFC 1929 Section 2
 */
public interface SocksUsernamePasswordAuthentication {
    /**
     * Get Username for authentication with a maximum length of 255
     *
     * @return Username
     */
    String getUsername();

    /**
     * Get Password byte array with a maximum length of 255
     *
     * @return Password
     */
    byte[] getPassword();
}
