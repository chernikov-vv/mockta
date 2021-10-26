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

package codes.vps.mockta.ws.okta;

import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.AppUser;
import codes.vps.mockta.obj.okta.ErrorObject;
import codes.vps.mockta.db.AppsDB;
import codes.vps.mockta.db.OktaApp;
import codes.vps.mockta.db.OktaAppUser;
import codes.vps.mockta.db.OktaUser;
import codes.vps.mockta.db.UserDB;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/apps")
public class AppsController implements AdminService {

    @PostMapping
    public ResponseEntity<App> addApplication(@RequestBody App app) {
        return ResponseEntity.ok(AppsDB.addApp(app).represent());
    }

    @PostMapping("{appId}/users")
    public ResponseEntity<AppUser> assignUser(@PathVariable String appId, @RequestBody AppUser appUser) {

        OktaApp app = AppsDB.getApp(appId);
        OktaUser user = UserDB.getUser(appUser.getId());

        AppUser ret = app.getUsers().compute(user.getId(), (k, v)->{
            if (v != null) { throw ErrorObject.duplicate("app user registration "+k).boom(); }
            return new OktaAppUser(user, app, appUser);
        }).represent();

        return ResponseEntity.ok(ret);

    }

}
