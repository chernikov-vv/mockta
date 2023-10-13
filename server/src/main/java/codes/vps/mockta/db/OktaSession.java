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

import codes.vps.mockta.OurCookie;
import codes.vps.mockta.obj.okta.AMR;
import codes.vps.mockta.obj.okta.IDP;
import codes.vps.mockta.obj.okta.Session;
import codes.vps.mockta.obj.okta.SessionStatus;
import codes.vps.mockta.util.Util;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.http.SameSiteCookies;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

@Getter
public class OktaSession implements Serializable {

	public final static String COOKIE_NAME = "sid"; // what Okta uses

	private final String id;
	private final String userId;
	private Date expires;
	private final IDP idp;
	@Setter
	private String token;
	private final Date created = new Date();
	private final String login;

	public OktaSession(OktaUser who) {
		this.id = Util.randomId();
		this.userId = who.getId();
		this.login = who.getUserName();
		refresh();
		idp = IDPDB.getIdp().represent();
		token = Util.randomId();
	}

	public Session represent() {
		return new Session(id, login, userId, expires, SessionStatus.ACTIVE, null, null, Collections.singletonList(AMR.pwd), idp);
	}

	public boolean isValid() {

		return new Date().before(expires);

	}

	public void refresh() {
		expires = new Date(System.currentTimeMillis() + 3600 * 1000);
	}

	public void setCookie(HttpServletResponse response) {

		// Tomcat cookies don't support SameSite, so we use a set-cookie header.

		OurCookie c = new OurCookie(COOKIE_NAME, id);
		c.setVersion(1);
		c.setPath("/"); // $TODO - really?
		// c.setSecure(true);
		c.setSecure(false); // since we only support HTTP; though it looks like Chrome doesn't care
		c.setHttpOnly(true);
		c.setSameSite(SameSiteCookies.LAX);
		response.addCookie(c);

	}

}
