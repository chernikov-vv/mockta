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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import codes.vps.mockta.MocktaApplication;
import codes.vps.mockta.db.OktaSession;
import codes.vps.mockta.db.SessionDB;
import codes.vps.mockta.obj.okta.ErrorObject;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	private MocktaApplication application;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (handler instanceof HandlerMethod) {
			Object bean = ((HandlerMethod) handler).getBean();
			if (bean instanceof AdminService) {

//				 boolean authOk = false;
//				//
//				 do {
//				
//				 String auth = request.getHeader("Authorization");
//				
//				 if (auth == null) {
//				 break;
//				 }
//				
//				 if (!auth.startsWith("SSWS ")) {
//				 break;
//				 }
//				
//				 if (!application.getApiTokens().contains(auth.substring(5))) {
//				 break;
//				 }
//				
//				 authOk = true;
//				
//				 } while (false);
//				
//				 if (!authOk) {
//				 response.sendError(401, "Authentication token invalid or missing");
//				 return false;
//				 }

			} else if (bean instanceof UserAuthenticatedService) {

				OktaSession oktSession = getSessionFromCookie(request);
				if (oktSession == null) {
					oktSession = getSessionFromRequest(request);
				}
				if (oktSession == null) {
					throw ErrorObject.notFound("No session value found in request or Cookie").boom();
				}

				((UserAuthenticatedService) bean).setSession(oktSession);

			}

		}
		return true;

	}

	public static OktaSession getSessionFromCookie(HttpServletRequest request) {

		Cookie sid = null;
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (Objects.equals(OktaSession.COOKIE_NAME, cookie.getName())) {
					sid = cookie;
					break;
				}
			}
		}

		if (sid == null || sid.getValue() == null) {
			return null;
		}
		OktaSession session = SessionDB.getByCookie(sid.getValue());
		
		
		return session;

	}

	public static OktaSession getSessionFromRequest(HttpServletRequest request) {

		String sid = request.getParameter("sessionToken");
		String bodyJson = getBody(request);
		
		if (sid == null && bodyJson!=null && bodyJson.length()>0) {
			
			System.out.println("get body " +bodyJson );
			JsonObject jsonObject =JsonParser.parseString(bodyJson).getAsJsonObject();
			sid =jsonObject.get("sessionToken").getAsString();
			
		}
		if (sid == null) {
			return null;
		}

		return SessionDB.getByCookie(sid);

	}

	@Autowired
	public void setApplication(MocktaApplication application) {
		this.application = application;
	}

	public static String getBody(HttpServletRequest request) {

		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			// throw ex;
			return "";
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {

				}
			}
		}

		body = stringBuilder.toString();
		return body;
	}

}