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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standard implementation of Authentication Challenge Parser supporting one scheme and multiple parameters
 */
public class StandardAuthenticationChallengeParser implements AuthenticationChallengeParser {
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^(?<scheme>\\S+)\\s?(?<parameters>.*)");

    private static final Pattern PARAMETERS_PATTERN = Pattern.compile("(?<name>[^=]+)=\"?(?<value>[^\"]+)[\"|\\s]?");

    private static final String SCHEME_GROUP = "scheme";

    private static final String PARAMETERS_GROUP = "parameters";

    private static final String NAME_GROUP = "name";

    private static final String VALUE_GROUP = "value";

    /**
     * Default constructor for Standard Authentication Challenge Parser
     */
    public StandardAuthenticationChallengeParser() {

    }

    /**
     * Get Authentication Challenge from challenge source containing scheme and parameters
     *
     * @param challenge HTTP header value required
     * @return Authentication Challenge or empty when pattern not matched
     */
    @Override
    public Optional<AuthenticationChallenge> getAuthenticationChallenge(final String challenge) {
        Objects.requireNonNull(challenge, "Challenge required");
        final Matcher schemeMatcher = SCHEME_PATTERN.matcher(challenge);
        return schemeMatcher.matches() ? Optional.of(getAuthenticationChallenge(schemeMatcher)) : Optional.empty();
    }

    private AuthenticationChallenge getAuthenticationChallenge(final Matcher schemeMatcher) {
        final String scheme = schemeMatcher.group(SCHEME_GROUP);
        final String parameters = schemeMatcher.group(PARAMETERS_GROUP);

        final List<AuthenticationParameter> authenticationParameters = getAuthenticationParameters(parameters);
        return new StandardAuthenticationChallenge(scheme, authenticationParameters);
    }

    private List<AuthenticationParameter> getAuthenticationParameters(final String parameters) {
        final List<AuthenticationParameter> authenticationParameters = new ArrayList<>();

        final Matcher parametersMatcher = PARAMETERS_PATTERN.matcher(parameters);
        while (parametersMatcher.find()) {
            final String name = parametersMatcher.group(NAME_GROUP);
            final String value = parametersMatcher.group(VALUE_GROUP);

            final AuthenticationParameter parameter = new StandardAuthenticationParameter(name, value);
            authenticationParameters.add(parameter);
        }

        return authenticationParameters;
    }
}
