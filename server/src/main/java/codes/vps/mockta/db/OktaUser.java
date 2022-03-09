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

package codes.vps.mockta.db;

import java.util.Date;

import codes.vps.mockta.Util;
import codes.vps.mockta.obj.okta.Credentials;
import codes.vps.mockta.obj.okta.ErrorObject;
import codes.vps.mockta.obj.okta.Profile;
import codes.vps.mockta.obj.okta.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OktaUser {

	private final String id = Util.randomId();
	private final String userName;
	private String password;
	private Date passwordChanged;
	private String firstName;
	private String lastName;
	private String email;
	private String locale;
	private String timeZone;
	private Profile profile;
	private String status;

	public OktaUser(User user) {
		Profile profile = user.getProfile();
		Credentials creds = user.getCredentials();

		// $TODO: It's unclear what errors to return when we don't have enough data
		if (creds == null) {
			throw ErrorObject.illegalArgument("no credentials").boom();
		}
		if (profile == null) {
			throw ErrorObject.illegalArgument("no profile").boom();
		}

		String password;
		if (creds.getPassword() == null || (password = Util.sTrim(creds.getPassword().getValue())) == null) {
			throw ErrorObject.illegalArgument("Only plain text password is supported for user creation").boom();
		}

		String userName = Util.sTrim(profile.getLogin());
		if (userName == null) {
			throw ErrorObject.illegalArgument("username must be specified").boom();
		}

		firstName = Util.makeNotNull(profile.getFirstName(), () -> "Jane");
		lastName = Util.makeNotNull(profile.getLastName(), () -> "Doe");
		locale = Util.makeNotNull(profile.getLocale(), () -> "en_US");
		timeZone = Util.makeNotNull(profile.getLocale(), () -> "Pacific/Honolulu");
		status = Util.makeNotNull(user.getStatus(), () -> "ACTIVE");
		this.profile = profile;
		email = Util.makeNotNull(profile.getEmail(), () -> "Dummy@test.com");

		this.userName = userName;
		setPassword(password);

	}

	public User represent() {

		return new User(id, passwordChanged,
				new Profile(userName, email, firstName, lastName, locale, timeZone));

	}

	public OktaUser setPassword(String password) {
		this.password = password;
		this.passwordChanged = new Date();
		return this;
	}

	@Override
	public String toString() {
		return "OktaUser [userName=" + userName + ", passwordChanged=" + passwordChanged + ", firstName=" + firstName
				+ ", lastName=" + lastName + "]";
	}

}
