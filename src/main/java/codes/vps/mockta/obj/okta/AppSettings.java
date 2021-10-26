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

// https://developer.okta.com/docs/reference/api/apps/#app-names
// However, this data is different depending on the application type
// Since we only support OAuth2.0 application, the structure is hardcoded
// for that. Also see https://developer.okta.com/docs/reference/api/apps/#credentials
// However, the tables are not consistent with the request samples,
// and the actual responses that we see being returned. The code follows
// the actual observed data.
@Getter
public class AppSettings extends RepresentationModel<AppSettings> {

    private final OAuthClient oauthClient;

    @JsonCreator
    public AppSettings(OAuthClient oauthClient) {
        this.oauthClient = oauthClient;
    }
}
