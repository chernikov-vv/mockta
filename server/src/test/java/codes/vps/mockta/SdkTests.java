/*
 * Copyright (c) 2022 Pawel S. Veselov
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

package codes.vps.mockta;

import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.AuthorizationMode;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;

public class SdkTests extends Tests {

    protected Client getSdkClient() {

        return Clients.builder()
                .setOrgUrl(String.format("http://localhost:%d/", serverPort))  // e.g. https://dev-123456.okta.com
                .setAuthorizationMode(AuthorizationMode.SSWS)
                .setConnectionTimeout(10)
                .setClientCredentials(new TokenClientCredentials("FireAxe"))
                .build();

    }


}
