/*
 * Copyright (c) 2021-2022 Pawel S. Veselov
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

package codes.vps.mockta.ws.okta;

import codes.vps.mockta.db.AppsDB;
import codes.vps.mockta.db.IDPDB;
import codes.vps.mockta.db.KeysDB;
import codes.vps.mockta.db.OktaApp;
import codes.vps.mockta.db.OktaAppUser;
import codes.vps.mockta.db.OktaSession;
import codes.vps.mockta.db.OktaUser;
import codes.vps.mockta.db.SessionDB;
import codes.vps.mockta.db.UserDB;
import codes.vps.mockta.obj.model.AuthInfo;
import codes.vps.mockta.obj.okta.ErrorObject;
import codes.vps.mockta.obj.okta.OpenIDMetaData;
import codes.vps.mockta.util.Util;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.util.Objects;

@Controller
@RequestMapping(path = {"/oauth2/v1/authorize", "/oauth2/{authServer}/v1/authorize"})
public class AuthorizationController {

	// https://developer.okta.com/docs/reference/api/oidc/#authorize
	@GetMapping
	public ModelAndView authorize(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable(required = false) String authServer,
			@RequestParam(name="client_id") String clientId,
			@RequestParam(required = false) Prompt prompt,
			@RequestParam(name = "redirect_uri") String redirectURI,
			@RequestParam(name = "response_type") String responseType,
			@RequestParam(name = "response_mode", required = false) ResponseMode responseMode,
			@RequestParam String scope, // space separated
			@RequestParam(required = false) String sessionToken,
			@RequestParam String state,
			@RequestParam String nonce,
			Model model
	) throws Exception {

		if (responseMode == null) { responseMode = ResponseMode.FRAGMENT; }
		if (responseMode != ResponseMode.OKTA_POST_MESSAGE) {
			// it's unclear which response code to use here. These are OIDC errors,
			// but we don't implement redirect responses.... It's a mess.
			// return new ModelAndView(null, HttpStatus.INTERNAL_SERVER_ERROR);
			throw new ChokeException();
		}

		AuthInfo authInfo = null;
		String error;
		String errorDescription;
		String frameUrl;

		do {

			// first we need to determine the frame URL, which we can only source
			// from the redirect URL (which is not used for anything else)

			URI uri = new URI(redirectURI);
			frameUrl = new DefaultUriBuilderFactory().builder().scheme(uri.getScheme()).host(uri.getHost()).port(uri.getPort()).build().toString();
			// now we can at least generate error responses.

			if (sessionToken == null) {
				error = "no-session-token"; // $TODO
				errorDescription = "Only session token authorization is supported";
				break;
			}

			try (OktaApp app = AppsDB.getApp(clientId)) {

				OktaSession session = SessionDB.getByTokenOnce(sessionToken);

				boolean uriOK = false;

				// OK, let's validate that the caller is from an authorized website.
				for (String allowedURI : app.getRedirectUris()) {
					if (redirectURI.startsWith(allowedURI)) {
						uriOK = true;
						break;
					}
				}

				if (!uriOK) {
					error = "uri-not-matched"; // $TODO
					errorDescription = "The URI of the authenticating website is not registered";
					break;
				}

				OktaUser user = UserDB.getUser(session.getUserId());
				OktaAppUser link = app.getUsers().get(user.getId());
				if (link == null) {
					error = "user-not-associated"; // $TODO
					errorDescription = "User not associated with the application";
					break;
				}

				if (!Objects.equals(responseType, "id_token")) {
					error = "bad-response-type";
					errorDescription = String.format("I only know how to respond to response_type 'id_token', not %s", responseType);
					break;
				}

				// OK, our main performance - generate the id_token value
				// Sample token seen Okta use:
				// {"kid":"S_GrqnSp7DSWRY7uhiDdpTEhfVLnD3ld7-fAEXsZYCk","alg":"RS256"}
                /*
                {
                  "sub": "user-id",
                  "email": "pawel.veselov@xxx",
                  "ver": 1,
                  "iss": "https://xxx",
                  "aud": "client-id",
                  "iat": 1635181190,
                  "exp": 1635184790,
                  "jti": "ID.nrdU_s_YwY7D6JruXlkhxX-s485g_rP6MK5tNicpa_0",
                  "amr": [
                    "pwd"
                  ],
                  "idp": "idp-id",
                  "nonce": "rqi8cN20xc3yQl8OhbPnpnrxwYKR0ktdeevjBofBaJwkwEPI0YG6sFQZinpRQQ1Y",
                  "email_verified": true,
                  "auth_time": 1635181153
                }
                 */

				OpenIDMetaData metaData = new OpenIDMetaData(request, authServer);
				JwtClaims claims = new JwtClaims();
				claims.setSubject(user.getId());
				claims.setClaim("email", user.getUserName());
				claims.setClaim("ver", 1);
				claims.setIssuer(metaData.getIssuer());
				claims.setAudience(clientId);
				claims.setIssuedAtToNow();
				NumericDate expiration = NumericDate.now();
				expiration.addSeconds(3600);
				claims.setExpirationTime(expiration);
				claims.setGeneratedJwtId();
				claims.setStringListClaim("amr", "pwd"); // password auth
				claims.setClaim("idp", IDPDB.getIdp().getId());
				claims.setClaim("nonce", nonce);
				claims.setClaim("email_verified", true);
				claims.setClaim("auth_time", session.getCreated().getTime());

				JsonWebSignature jws = new JsonWebSignature();
				jws.setPayload(claims.toJson());

				JsonWebKey jwk = KeysDB.getKey();

				jws.setKey(((RsaJsonWebKey)jwk).getRsaPrivateKey());
				jws.setKeyIdHeaderValue(jwk.getKeyId());
				jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

				String idToken = jws.getCompactSerialization();
				authInfo = AuthInfo.onAuth(frameUrl, state, idToken);

				session.setCookie(response);

			} catch (ErrorObject.MyException e) {
				// we have to report errors using javascript here.
				// it's unclear how we convert them properly.
				error = e.getObject().getErrorCode();
				errorDescription = e.getObject().getErrorSummary();
				break;
			}

			error = null;
			errorDescription = null;

		} while (false);

		if (authInfo == null) {
			authInfo = AuthInfo.onError(frameUrl, state, error, errorDescription);
		}

		response.addHeader("x-mockta-auth-error", Util.makeNotNull(error, ()->"<none>"));
		model.addAttribute("auth", authInfo);

		return new ModelAndView("postMessage", model.asMap());

	}

	enum Prompt {
		@JsonProperty("none")
		NONE
	}

	enum ResponseMode {
		@JsonProperty("okta_post_message")
		OKTA_POST_MESSAGE,
		@JsonProperty("fragment)")
		FRAGMENT
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	static class ChokeException extends RuntimeException {}

}
