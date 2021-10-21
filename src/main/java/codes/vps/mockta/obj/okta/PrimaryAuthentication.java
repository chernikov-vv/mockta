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

// https://developer.okta.com/docs/reference/api/authn/#primary-authentication
@Getter
public class PrimaryAuthentication {

    private final String audience; // deprecated
    private final Context context;
    private final Options options;
    private final String password;
    private final String token;
    private final String username;

    @JsonCreator
    public PrimaryAuthentication(String audience, Context context, Options options, String password, String token, String username) {
        this.audience = audience;
        this.context = context;
        this.options = options;
        this.password = password;
        this.token = token;
        this.username = username;
    }
}
