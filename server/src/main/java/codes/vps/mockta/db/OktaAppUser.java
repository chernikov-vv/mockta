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

package codes.vps.mockta.db;

import codes.vps.mockta.obj.okta.AppUser;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class OktaAppUser {

	private final Date created = new Date();
	private final OktaUser user;
	private final OktaApp app;

	private Date lastUpdated = new Date();
	private Map<String, String> profile;
	private final Date statusChanges = new Date(); // because we don't support changing status

	public OktaAppUser(OktaUser user, OktaApp app) {
		this.user = user;
		this.app = app;
	}

	public OktaAppUser(OktaUser user, OktaApp app, AppUser appUser) {
		this(user, app);
		profile = appUser.getProfile();
	}

	public AppUser represent() {

		return new AppUser(created, user.getId(), lastUpdated, profile, statusChanges);

	}

	@Override
	public String toString() {
		return "OktaAppUser [user=" + user + " for app "+app+", profile=" + profile + "]";
	}

}
