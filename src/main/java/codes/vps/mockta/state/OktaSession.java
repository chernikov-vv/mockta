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

package codes.vps.mockta.state;

import codes.vps.mockta.Util;
import codes.vps.mockta.obj.okta.AMR;
import codes.vps.mockta.obj.okta.IDP;
import codes.vps.mockta.obj.okta.IDPType;
import codes.vps.mockta.obj.okta.Session;
import codes.vps.mockta.obj.okta.SessionStatus;
import codes.vps.mockta.userdb.OktaUser;
import lombok.Getter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

@Getter
public class OktaSession implements Serializable {

    private final String id;
    private final String userId;
    private final Date expires;
    private final IDP idp;
    private final String token;

    public OktaSession(OktaUser who) {
        this.id = Util.randomId();
        this.userId = who.getId();
        expires = new Date(System.currentTimeMillis() + 3600);
        idp = new IDP(IDPType.OKTA);
        token = Util.randomId();
    }

    public Session represent() {
        return new Session(id, userId, userId, expires, SessionStatus.ACTIVE, null, null, Collections.singletonList(AMR.pwd), idp);
    }

    public boolean isValid() {

        return new Date().before(expires);

    }
}
