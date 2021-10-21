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

import codes.vps.mockta.obj.okta.PrimaryAuthentication;
import codes.vps.mockta.obj.okta.PrimaryAuthenticationResponse;
import codes.vps.mockta.obj.okta.User;
import codes.vps.mockta.state.OktaSession;
import codes.vps.mockta.state.SessionDB;
import codes.vps.mockta.userdb.OktaUser;
import codes.vps.mockta.userdb.UserDB;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authn")
public class AuthNController {

    @PostMapping
    public HttpEntity<?> post(@RequestBody PrimaryAuthentication authN) {

        OktaUser user = UserDB.authenticate(authN.getUsername(), authN.getPassword());

        OktaSession session = SessionDB.createSession(user);

        PrimaryAuthenticationResponse obj = new PrimaryAuthenticationResponse(session.getExpires(), session.getToken());
        RepresentationModel<User> model =
                HalModelBuilder.halModelOf(obj)
                .embed(user.represent(), LinkRelation.of("user"))
                .build();

        return new ResponseEntity<>(model, HttpStatus.OK);

    }

}
