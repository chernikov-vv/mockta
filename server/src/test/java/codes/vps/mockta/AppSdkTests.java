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

import com.okta.sdk.resource.application.Application;
import com.okta.sdk.resource.application.ApplicationBuilder;
import com.okta.sdk.resource.application.ApplicationSignOnMode;
import com.okta.sdk.resource.application.OpenIdConnectApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class AppSdkTests extends SdkTests {

    @Test
    public void testAdd() {

        String name = "name";
        String label = "label";
        String rUrl = "https://google.com";

        String id;

        {

            Application app = ApplicationBuilder.instance()
                    .setName(name)
                    .setLabel(label)
                    .setSignOnMode(ApplicationSignOnMode.OPENID_CONNECT)
                    .buildAndCreate(getSdkClient());

            id = app.getId();

        }

        OpenIdConnectApplication oApp = (OpenIdConnectApplication) getSdkClient().getApplication(id);
        oApp.getSettings().getOAuthClient().setRedirectUris(Collections.singletonList(rUrl));
        oApp.update();

        oApp = (OpenIdConnectApplication) getSdkClient().getApplication(id);

        Assertions.assertEquals(name, oApp.getName());
        Assertions.assertEquals(label, oApp.getLabel());
        Assertions.assertEquals(rUrl, oApp.getSettings().getOAuthClient().getRedirectUris().get(0));

    }

}
