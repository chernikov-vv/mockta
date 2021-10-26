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

import codes.vps.mockta.MocktaApplication;
import codes.vps.mockta.obj.okta.ErrorObject;
import codes.vps.mockta.db.OktaSession;
import codes.vps.mockta.db.SessionDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private MocktaApplication application;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            Object bean = ((HandlerMethod) handler).getBean();
            if (bean instanceof AdminService) {

                boolean authOk = false;

                do {

                    String auth = request.getHeader("Authorization");

                    if (auth == null) { break; }

                    if (!auth.startsWith("SSWS ")) {
                        break;
                    }

                    if (!application.getApiTokens().contains(auth.substring(5))) {
                        break;
                    }

                    authOk = true;

                } while (false);

                if (!authOk) {
                    response.sendError(401, "Authentication token invalid or missing");
                    return false;
                }

            } else if (bean instanceof UserAuthenticatedService) {

                ((UserAuthenticatedService)bean).setSession(getSessionFromCookie(request));

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
            throw ErrorObject.notFound("no session cookie value").boom();
        }

        return SessionDB.getByCookie(sid.getValue());

    }

    @Autowired
    public void setApplication(MocktaApplication application) {
        this.application = application;
    }

}
