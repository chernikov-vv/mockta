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

package codes.vps.mockta.obj.okta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class OAuthClient extends RepresentationModel<OAuthClient> {

	@JsonProperty("client_uri")
	private final String clientUri = null;
	@JsonProperty("logo_uri")
	private final String logoUri = null;
	@JsonProperty("redirect_uris")
	private final List<String> redirectUris;
	@JsonProperty("response_types")
	private final List<String> responseTypes = Arrays.asList("id_token", "token");
	@JsonProperty("grant_types")
	private final List<String> grantTypes = Collections.singletonList("implicit");
	@JsonProperty("application_type")
	private final String applicationType = "browser";

	@JsonProperty("wildcard_redirect")
	private final String wildCardRedirect = "DISABLED";

	@JsonCreator
	public OAuthClient(List<String> redirectUris) {
		this.redirectUris = new ArrayList<>(redirectUris);
	}

	public String getClientUri() {
		return clientUri;
	}

	public String getLogoUri() {
		return logoUri;
	}

	public List<String> getRedirectUris() {
		return redirectUris;
	}

	public List<String> getResponseTypes() {
		return responseTypes;
	}

	public List<String> getGrantTypes() {
		return grantTypes;
	}

	public String getApplicationType() {
		return applicationType;
	}

	public String getWildCardRedirect() {
		return wildCardRedirect;
	}

}
