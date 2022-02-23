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

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import codes.vps.mockta.db.OktaAppUser;
import lombok.Getter;

// https://developer.okta.com/docs/reference/api/apps/#application-object
@Getter
public class App extends RepresentationModel<App> {

	private final Date created;
	private final String id;
	private final String label;
	private final Date lastUpdated;
	private final String name;
	private final String profile;
	private final SignOnMode signOnMode = SignOnMode.BASIC_AUTH; // right?
	private final String status = "ACTIVE";
	private final AppSettings settings;
	private Map<String, OktaAppUser> users = new ConcurrentHashMap<>();

	public App(Date created, String id, String label, Date lastUpdated, String name, String profile,
			Map<String, OktaAppUser> users, AppSettings settings) {
		this.created = created;
		this.id = id;
		this.label = label;
		this.lastUpdated = lastUpdated;
		this.name = name;
		this.profile = profile;
		this.settings = settings;
		this.users = users;
	}

	@JsonCreator
	public App(String label, String name, String profile, @JsonProperty("settings") AppSettings settings) {
		this(null, null, label, null, name, profile, null, settings);
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public String getName() {
		return name;
	}

	public String getProfile() {
		return profile;
	}

	public SignOnMode getSignOnMode() {
		return signOnMode;
	}

	public String getStatus() {
		return status;
	}

	public AppSettings getSettings() {
		return settings;
	}

}