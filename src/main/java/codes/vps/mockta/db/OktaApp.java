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

import codes.vps.mockta.Util;
import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.AppSettings;
import codes.vps.mockta.obj.okta.ErrorObject;
import codes.vps.mockta.obj.okta.OAuthClient;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class OktaApp {

    private final String id = Util.randomId();
    private final Date created = new Date();
    private final String name; // unique
    private Date lastUpdated;
    private String label;
    private String profile;
    private final Map<String, OktaAppUser> users = new ConcurrentHashMap<>();
    private final List<String> redirectUris = new ArrayList<>();

    public OktaApp(String name) {
        this.name = name;
    }

    public OktaApp(App app) {

        if (app.getName() == null) { throw ErrorObject.illegalArgument("need name").boom(); }
        name = app.getName();

        label = app.getLabel();
        lastUpdated = new Date();
        profile = app.getProfile();

        Util.whenNotNull(app.getSettings(), settings->
                Util.whenNotNull(settings.getOauthClient(), oAuthClient ->
                        Util.whenNotNull(oAuthClient.getRedirectUris(), redirectUris::addAll)));

    }

    public App represent() {

        return new App(created, id, label, lastUpdated, name, profile, new AppSettings(new OAuthClient(redirectUris)));

    }

}


