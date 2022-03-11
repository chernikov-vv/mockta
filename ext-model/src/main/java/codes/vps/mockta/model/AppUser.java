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

package codes.vps.mockta.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Map;

// https://developer.okta.com/docs/reference/api/apps/#application-user-object

@Getter
public class AppUser extends RepresentationModel<AppUser> {

    private final Date created;
    private final String id; // actually user's ID
    private final Date lastUpdated;
    private final Map<String, String> profile;
    private final String status = "ACTIVE";
    private final Date statusChanges;

    public AppUser(Date created, String id, Date lastUpdated, Map<String, String> profile, Date statusChanges) {
        this.created = created;
        this.id = id;
        this.lastUpdated = lastUpdated;
        this.profile = profile;
        this.statusChanges = statusChanges;
    }

    @JsonCreator
    public AppUser(String id, Map<String, String> profile) {
        this(null, id, null, profile, null);
    }

}
