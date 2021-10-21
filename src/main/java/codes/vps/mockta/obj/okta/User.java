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

package codes.vps.mockta.obj.okta;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

@Getter
public class User extends RepresentationModel<User> {

    // https://developer.okta.com/docs/reference/api/users/#user-properties
    private String id;
    private Date created;
    private Date activated;
    private Date statusChanged;
    private Date lastLogin;
    private Date passwordChanged;
    private UserType type;
    private String transitioningToStatus = null;
    private Profile profile;
    private Credentials credentials;

    public User(String id, Date passwordChanged, Profile profile) {
        this.id = id;
        this.passwordChanged = passwordChanged;
        this.profile = profile;
    }

    // only create with not-readonly properties
    @JsonCreator
    public User(Profile profile, Credentials credentials) {
        this.profile = profile;
        this.credentials = credentials;
    }

}
