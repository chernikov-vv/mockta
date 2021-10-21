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

import codes.vps.mockta.obj.okta.ErrorObject;
import codes.vps.mockta.obj.okta.Session;
import codes.vps.mockta.state.OktaSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final HttpSession session;

    @Autowired
    public SessionController(HttpSession session) {
        this.session = session;
    }

    @GetMapping("/me")
    public HttpEntity<Session> currentSession() {

        OktaSession oktaSession = getOktaSession();
        if (oktaSession == null || !oktaSession.isValid()) {
            throw ErrorObject.RESOURCE_NOT_FOUND.boom();
        }

        return new ResponseEntity<>(oktaSession.represent(), HttpStatus.OK);

    }

    private OktaSession getOktaSession() {

        if (session == null) { return null; }
        return (OktaSession) session.getAttribute("okta-session");

    }

}
