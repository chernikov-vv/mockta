/*
 * Copyright (c) 2021 Pawel S. Veselov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// https://developer.okta.com/docs/reference/api/oidc/#response-properties-9
package codes.vps.mockta.obj.okta;

import codes.vps.mockta.Util;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class OpenIDMetaData extends RepresentationModel<OpenIDMetaData> {

    @JsonProperty("authorization_endpoint")
    private final String authorizationEndpoint;
    @JsonProperty("device_authorization_endpoint")
    private final String deviceAuthorizationEndpoint = null;
    @JsonProperty("claims_supported")
    private final List<String> claimsSupported;
    @JsonProperty("code_challenge_methods_supported")
    private final List<String> codeChallengeMethodsSupported;
    @JsonProperty("end_session_endpoint")
    private final String endSessionEndpoint;
    @JsonProperty("grant_types_supported")
    private final List<String> grantTypesSupported;
    @JsonProperty("introspection_endpoint")
    private final String introspectionEndpoint;
    @JsonProperty("introspection_endpoint_auth_methods_supported")
    private final List<String> introspectionEndpointAuthMethodsSupported;
    @JsonProperty("issuer")
    private final String issuer;
    @JsonProperty("jwks_uri")
    private final String jwkURI;
    @JsonProperty("registration_endpoint")
    private final String registrationEndpoint;
    @JsonProperty("request_object_signing_alg_values_supported")
    private final List<String> requestObjectSigningAlgorithms;
    @JsonProperty("request_parameter_supported")
    private final boolean requestParameterSupported = true;
    @JsonProperty("response_modes_supported")
    private final List<String> responseModesSupported;
    @JsonProperty("response_types_supported")
    private final List<String> responseTypesSupported;
    @JsonProperty("revocation_endpoint")
    private final String revocationEndpoint;
    @JsonProperty("revocation_endpoint_auth_methods_supported")
    private final List<String> revocationEndpointAuthMethodsSupported;
    @JsonProperty("scopes_supported")
    private final List<String> scopesSupported;
    @JsonProperty("subject_types_supported")
    private final List<String> subjectTypesSupported;
    @JsonProperty("token_endpoint")
    private final String tokenEndpoint;
    @JsonProperty("token_endpoint_auth_methods_supported")
    private final List<String> tokenEndpointAuthMethodsSupported;
    @JsonProperty("id_token_signing_alg_values_supported")
    private final List<String> idTokenSigningAlgValuesSupported;
    @JsonProperty("userinfo_endpoint")
    private final String userInfoEndpoint;

    public OpenIDMetaData(HttpServletRequest request, String authServer) {

        UriBuilder ub = new DefaultUriBuilderFactory().builder();
        URI incoming = Util.reThrow(()->new URI(request.getRequestURL().toString()));
        ub.scheme(incoming.getScheme());
        ub.host(incoming.getHost());
        ub.port(incoming.getPort());

        ub.replacePath("/oauth2");
        if (authServer != null) {
            ub.pathSegment(authServer);
        }
        ub.pathSegment("v1", "authorize");

        authorizationEndpoint = ub.build().toString();

        claimsSupported = Arrays.asList("iss",
                "ver",
                "sub",
                "aud",
                "iat",
                "exp",
                "jti",
                "auth_time",
                "amr",
                "idp",
                "nonce",
                "name",
                "nickname",
                "preferred_username",
                "given_name",
                "middle_name",
                "family_name",
                "email",
                "email_verified",
                "profile",
                "zoneinfo",
                "locale",
                "address",
                "phone_number",
                "picture",
                "website",
                "gender",
                "birthdate",
                "updated_at",
                "at_hash",
                "c_hash"
        );

        ub.replacePath("/oauth2");
        if (authServer != null) {
            ub.pathSegment(authServer);
        }
        ub.pathSegment("v1", "logout");

        endSessionEndpoint = ub.build().toString();

        grantTypesSupported = Arrays.asList("authorization_code",
                "implicit",
                "refresh_token",
                "password"
        );

        introspectionEndpoint = ub.replacePath("oauth2/v1/introspect").build().toString();
        introspectionEndpointAuthMethodsSupported = Arrays.asList(    "client_secret_basic",
                "client_secret_post",
                "client_secret_jwt",
                "private_key_jwt",
                "none"
        );

        ub.replacePath(null);
        if (authServer != null) {
            ub.pathSegment("oauth2", "default");
        }

        issuer = ub.build().toString();

        jwkURI = ub.replacePath("/oauth2/v1/keys").build().toString();

        registrationEndpoint = ub.replacePath("/oauth/v1/clients").build().toString();

        requestObjectSigningAlgorithms = Arrays.asList("HS256",
                "HS384",
                "HS512",
                "RS256",
                "RS384",
                "RS512",
                "ES256",
                "ES384",
                "ES512"
        );

        /*
        responseModesSupported = Arrays.asList("query",
                "fragment",
                "form_post",
                "okta_post_message");

         */
        responseModesSupported = Collections.singletonList("okta_post_message");

        /*
        responseTypesSupported = Arrays.asList(    "code",
                "id_token",
                "code id_token",
                "code token",
                "id_token token",
                "code id_token token"
        );
         */

        responseTypesSupported = Collections.singletonList("id_token");

        revocationEndpoint = ub.replacePath("/oauth2/v1/revoke").build().toString();

        revocationEndpointAuthMethodsSupported = Arrays.asList(    "client_secret_basic",
                "client_secret_post",
                "client_secret_jwt",
                "private_key_jwt",
                "none"
        );

        scopesSupported = Arrays.asList(    "openid",
                "email",
                "profile",
                "address",
                "phone",
                "offline_access",
                "groups"
        );

        subjectTypesSupported = Collections.singletonList("public");

        idTokenSigningAlgValuesSupported = Collections.singletonList("RS256");

        tokenEndpoint = ub.replacePath("/oauth2/v1/token").build().toString();

        userInfoEndpoint = ub.replacePath("/oauth2/v1/userinfo").build().toString();

        tokenEndpointAuthMethodsSupported = Arrays.asList(    "client_secret_basic",
                "client_secret_post",
                "client_secret_jwt",
                "private_key_jwt",
                "none"
        );

        codeChallengeMethodsSupported = Collections.singletonList("S256");

    }


}
