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

import java.util.HashMap;
import java.util.Map;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class Profile extends RepresentationModel<Profile> {

	private final String login;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final String locale;
	private final String timeZone;
	@JsonProperty("esAppData2")
	private Map<String,TenancyInfo> esAppData2 = new HashMap();

	public Profile(String login, String email, String firstName, String lastName, String locale, String timeZone,
			Map<String, TenancyInfo> esAppData2) {
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.locale = locale;
		this.timeZone = timeZone;
		this.email = email;
		this.esAppData2 = esAppData2;

	}

	@JsonCreator
	public Profile(String login, String email, String firstName, String lastName,
			Map<String, TenancyInfo> esAppData2) {
		this(login, email, firstName, lastName, null, null, esAppData2);
	}

	public String getLogin() {
		return login;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getLocale() {
		return locale;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public String getEmail() {
		return email;
	}

	public Map<String, TenancyInfo> getEsAppData2() {
		return esAppData2;
	}

	public void setEsAppData2(Map<String, TenancyInfo> esAppData2) {
		this.esAppData2 = esAppData2;
	}

}
