/*
 * ============LICENSE_START=======================================================
 * oom-certservice-client
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.oom.certservice.client.configuration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UriValidatorTest {

    /**
     * scheme = ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
     */

    @ParameterizedTest
    @ValueSource(strings = {"http:/", "http:", "http://", "h4ttp://"})
    void shouldTrueForValidScheme(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"example.com", "www.example.com", "0.0.0.0", "[2001:0db8:85a3:0000:0000:8a2e:0370:7334]"})
    void shouldFalseForUriWithoutScheme(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"*http://", "_http://", "?http://", "4http://"})
    void shouldFalseForUriWithInvalidScheme(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isFalse();
    }

    /**
     * authority   = [ userinfo "@" ] host [ ":" port ]
     * <p>
     * userinfo    = *( unreserved / pct-encoded / sub-delims / ":" )
     * <p>
     * host        = IP-literal / IPv4address / reg-name
     */

    @ParameterizedTest
    @ValueSource(strings = {
        "http://user:password@example.com",
        "http://user@example.com",
        "http://user:password:test@example.com",
        "http://user-info:password@example.com"})
    void shouldTrueForValidUserInAuthority(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http://user:password",
        "http://user:password:test:"})
    void shouldFalseForMissingHostInAuthority(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]/test",
        "https://[2001:db8:85a3:8d3:1319:8a2e:370:7348]/",
        "http://8.8.8.8/",
        "http://8.8.8.8/test"})
    void shouldTrueForUriContainsIP(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:443/test",
        "https://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:443/",
        "http://8.8.8.8:8080/test",
        "https://8.8.8.8:443/"})
    void shouldTrueForUriContainsIPAndPort(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http:/path.to.file",
        "http:/file",
        "http:/ptah/to/file"})
    void shouldTrueForMissingAuthority(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isTrue();
    }

    /**
     * PATH QUERY FRAGMENT
     */

    @ParameterizedTest
    @ValueSource(strings = {
        "http://example.com/path/to/file",
        "http://example.com/path",
        "http://example.com/",})
    void shouldTrueForPathWithAuthority(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http:/path/to/file",
        "http:/path",
        "http:/",})
    void shouldTrueForPathWithoutAuthority(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isTrue();
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "http://example.com/test.txt?test=test1&test2=test3#onap",
        "http://example.com?",
        "http://example.com?test=tes1&#",
        "http://example.com#onap"})
    void shouldTrueForUriWithQueryAndFragmentInPath(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http://example.com/test.txt?#onap#?",
        "http://example.com?##",
        "http://www.example.com/file%GF.html"})
    void shouldFalseForUriWithWrongQueryOrWrongFragmentInPath(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ftp://ftp.is.co.za/rfc/rfc1808.txt",
        "http://www.ietf.org/rfc/rfc2396.txt",
        "ldap://[2001:db8::7]/c=GB?objectClass?one",
        "mailto:John.Doe@example.com",
        "news:comp.infosystems.www.servers.unix",
        "tel:+1-816-555-1212",
        "telnet://192.0.2.16:80/",
        "urn:oasis:names:specification:docbook:dtd:xml:4.1.2"})
    void shouldTrueForRFC3986Examples(String uri) {
        assertThat(UriValidator.isValidUri(uri)).isTrue();
    }
}
