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

package codes.vps.mockta.ws.okta;

import codes.vps.mockta.db.OktaSession;
import codes.vps.mockta.db.SessionDB;
import codes.vps.mockta.model.SessionToken;
import codes.vps.mockta.obj.okta.Session;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/sessions")
public class SessionController extends UserAuthenticatedService {

    @PostMapping()
    @IsSkipAuth
    public HttpEntity<Session> createSession(@RequestBody SessionToken token) {
        return ResponseEntity.ok(SessionDB.getByTokenOnce(token.getSessionToken()).represent());
    }

    @GetMapping("/me")
    public HttpEntity<Session> currentSession() {
        return ResponseEntity.ok(session.represent());
    }

    @PostMapping("/{sessionId}/lifecycle/refresh")
    @IsSkipAuth
    public HttpEntity<Session> refresh(@PathVariable String sessionId) {
        OktaSession s = SessionDB.getByCookie(sessionId);
        s.refresh();
        return ResponseEntity.ok(s.represent());
    }

}
