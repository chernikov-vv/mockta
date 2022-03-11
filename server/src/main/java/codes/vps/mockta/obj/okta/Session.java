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

package codes.vps.mockta.obj.okta;

import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

// https://developer.okta.com/docs/reference/api/sessions/#session-properties
@Getter
public class Session extends RepresentationModel<Session> {

    private final String id;
    private final String login;
    private final String userId;
    private final Date expiresAt;
    private final SessionStatus status;
    private final Date lastPasswordVerification;
    private final Date lastFactorVerification;
    private final List<AMR> amr;
    private final IDP idp;
    private final boolean mfaActive = false;

    public Session(String id, String login, String userId, Date expiresAt, SessionStatus status, Date lastPasswordVerification, Date lastFactorVerification, List<AMR> amr, IDP idp) {
        this.id = id;
        this.login = login;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.status = status;
        this.lastPasswordVerification = lastPasswordVerification;
        this.lastFactorVerification = lastFactorVerification;
        this.amr = amr;
        this.idp = idp;
    }
}
