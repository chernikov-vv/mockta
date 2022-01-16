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

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public class User extends RepresentationModel<User> {

	// https://developer.okta.com/docs/reference/api/users/#user-properties
	private String id;
	private Date created;
	private Date activated;
	private Date statusChanged;
	private Date lastLogin;
	private Date passwordChanged;
	private UserType type;
	private String transitioningToStatus = null;
	private Profile profile;
	private Credentials credentials;
	private String status = "ACTIVE";

	public User(String id, Date passwordChanged, Profile profile) {
		this.id = id;
		this.passwordChanged = passwordChanged;
		this.profile = profile;
	}

	// only create with not-readonly properties
	@JsonCreator
	public User(Profile profile, Credentials credentials) {
		this.profile = profile;
		this.credentials = credentials;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getActivated() {
		return activated;
	}

	public void setActivated(Date activated) {
		this.activated = activated;
	}

	public Date getStatusChanged() {
		return statusChanged;
	}

	public void setStatusChanged(Date statusChanged) {
		this.statusChanged = statusChanged;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(Date passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public String getTransitioningToStatus() {
		return transitioningToStatus;
	}

	public void setTransitioningToStatus(String transitioningToStatus) {
		this.transitioningToStatus = transitioningToStatus;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
