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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(AdminAuthInterceptor.class);
    private MocktaApplication application;

    public AdminAuthInterceptor() {
        log.info("interceptor up");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("Handler:"+handler.getClass().getName());
        if (handler instanceof HandlerMethod) {
            if (((HandlerMethod) handler).getBean() instanceof AdminService) {

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

            }
        }
        return true;

    }

    @Autowired
    public void setApplication(MocktaApplication application) {
        this.application = application;
    }
}
