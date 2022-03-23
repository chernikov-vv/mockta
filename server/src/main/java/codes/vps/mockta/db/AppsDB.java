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

package codes.vps.mockta.db;

import codes.vps.mockta.model.App;
import codes.vps.mockta.obj.okta.ErrorObject;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AppsDB {

    public final static Map<String, OktaApp> apps = new ConcurrentHashMap<>();
    public final static Map<String, OktaApp> appsById = new ConcurrentHashMap<>();

    public static OktaApp addApp(App app) {

        OktaApp oktaApp = new OktaApp(app);

        apps.compute(oktaApp.getName(), (k, v) -> {
            if (v != null) {
                throw ErrorObject.duplicate("app name " + k).boom();
            }
            return oktaApp;
        });

        appsById.put(oktaApp.getId(), oktaApp);
        return oktaApp;

    }

    public static OktaApp updateApp(String id, App app) {

        try (OktaApp oktaApp = getApp(id)) {

            apps.compute(app.getName(), (k, v) -> {
                if (v != null && v != oktaApp) {
                    throw ErrorObject.duplicate("app name " + k).boom();
                }
                return oktaApp;
            });

            oktaApp.updateFrom(app);

            if (!Objects.equals(oktaApp.getName(), oktaApp.getSavedName())) {
                apps.remove(oktaApp.getSavedName());
                oktaApp.setSavedName(oktaApp.getName());
            }

            return oktaApp;

        }

    }

    @NonNull
    public static OktaApp getApp(String id) {

        OktaApp app = appsById.get(id);
        if (app == null) {
            throw ErrorObject.notFound("app id " + id).boom();
        }
        app.checkOut();
        return app;

    }

    public static boolean deleteApp(String id) {

        OktaApp app = appsById.get(id);
        if (app == null) {
            throw ErrorObject.notFound("app id " + id).boom();
        }
        appsById.remove(id);
        apps.remove(app.getName());
        return true;

    }

    // $TODO: REMOVE THIS
    public static boolean deleteAllApp() {

        appsById.clear();
        apps.clear();

        return true;

    }

    @NonNull
    public static List<OktaApp> getAllApps() {

        if (appsById.values().size() == 0) {
            return Collections.emptyList();
        }

        return new ArrayList<>(appsById.values());

    }

}
