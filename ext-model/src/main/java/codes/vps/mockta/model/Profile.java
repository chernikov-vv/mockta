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

package codes.vps.mockta.model;

import java.util.HashMap;

public class Profile extends HashMap<String, String> {

	public String getLogin() {
		return get("login");
	}

	public String getEmail() {
		return get("email");
	}

	public String getFirstName() {
		return get("firstName");
	}

	public String getLastName() {
		return get("firstName");
	}

	public String getLocale() {
		return get("locale");
	}

	public String getTimeZone() {
		return get("timeZone");
	}

	public Profile setLogin(String login) {
		put("login", login);
		return this;
	}

	public Profile setEmail(String e) {
		put("email", e);
		return this;
	}

	public Profile setFirstName(String n) {
		put("firstName", n);
		return this;
	}

	public Profile setLastName(String n) {
		put("lastName", n);
		return this;
	}

	public Profile setLocale(String l) {
		put("locale", l);
		return this;
	}


	public Profile setTimeZone(String tz) {
		put("timeZone", tz);
		return this;
	}

}
