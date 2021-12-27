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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StandardAuthenticationChallengeParserTest {
    private static final String BASIC_SCHEME = "Basic";

    private static final String PROTECTED_REALM = "protected";

    private static final String REALM_PARAMETER = "realm";

    private static final String BASIC_REALM_CHALLENGE = String.format("Basic %s=\"%s\"", REALM_PARAMETER, PROTECTED_REALM);

    private static final String LEADING_SPACES = " Basic";

    private StandardAuthenticationChallengeParser parser;

    @BeforeEach
    public void setParser() {
        parser = new StandardAuthenticationChallengeParser();
    }

    @Test
    public void testGetAuthenticationChallengeLeadingSpacesNotMatched() {
        final Optional<AuthenticationChallenge> challengeParsed = parser.getAuthenticationChallenge(LEADING_SPACES);

        assertFalse(challengeParsed.isPresent());
    }

    @Test
    public void testGetAuthenticationChallengeBasicWithoutParameters() {
        final Optional<AuthenticationChallenge> challengeParsed = parser.getAuthenticationChallenge(BASIC_SCHEME);

        assertTrue(challengeParsed.isPresent());
        final AuthenticationChallenge challenge = challengeParsed.get();
        assertEquals(BASIC_SCHEME, challenge.getAuthenticationScheme());
        assertTrue(challenge.getAuthenticationParameters().isEmpty());
        assertEquals(BASIC_SCHEME, challenge.toString());
    }

    @Test
    public void testGetAuthenticationChallengeBasicWithParameters() {
        final Optional<AuthenticationChallenge> challengeParsed = parser.getAuthenticationChallenge(BASIC_REALM_CHALLENGE);

        assertTrue(challengeParsed.isPresent());
        final AuthenticationChallenge challenge = challengeParsed.get();
        assertEquals(BASIC_SCHEME, challenge.getAuthenticationScheme());

        final List<AuthenticationParameter> authenticationParameters = challenge.getAuthenticationParameters();
        assertFalse(authenticationParameters.isEmpty());

        final AuthenticationParameter parameter = authenticationParameters.iterator().next();
        assertEquals(REALM_PARAMETER, parameter.getName());
        assertEquals(PROTECTED_REALM, parameter.getValue());
    }
}
