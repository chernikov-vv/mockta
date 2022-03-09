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

package codes.vps.mockta.ws.okta;

import codes.vps.mockta.db.OktaSession;
import codes.vps.mockta.db.SessionDB;
import codes.vps.mockta.obj.okta.ErrorObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

@RestController
@RequestMapping(path = { "/oauth2/v1/logout", "/oauth2/{authServer}/v1/logout" })
public class LogoutController {

	@GetMapping
	public void logout(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(required = false) String authServer, @RequestParam(required = false) String state,
			@RequestParam(required = false, name = "post_logout_redirect_uri") String redirectUri) throws IOException {

		// we really don't care about anything here
		try {
			OktaSession session = AuthInterceptor.getSessionFromCookie(request);
			SessionDB.remove(session);
		} catch (ErrorObject.MyException ignored) {
		}

		UriBuilder b = new DefaultUriBuilderFactory().uriString(URLDecoder.decode(redirectUri, "UTF-8"));
		b.replaceQueryParam("state", state);

		response.sendRedirect(b.build().toString());

	}

}
