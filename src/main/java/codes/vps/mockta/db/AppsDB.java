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

package codes.vps.mockta.db;

import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.ErrorObject;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppsDB {

    public final static Map<String, OktaApp> apps = new ConcurrentHashMap<>();
    public final static Map<String, OktaApp> appsById = new ConcurrentHashMap<>();

    public static OktaApp addApp(App app) {

        OktaApp oktaApp = new OktaApp(app);

        apps.compute(oktaApp.getName(), (k,v)->{
            if (v != null) {
                throw ErrorObject.duplicate("app name "+k).boom();
            }
            return new OktaApp(app);
        });

        appsById.put(oktaApp.getId(), oktaApp);

        return oktaApp;

    }

    @NonNull
    public static OktaApp getApp(String id) {

        OktaApp app = appsById.get(id);
        if (app == null) {
            throw ErrorObject.notFound("app id "+id).boom();
        }
        return app;

    }

}
