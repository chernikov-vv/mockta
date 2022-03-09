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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.AppUser;
import codes.vps.mockta.obj.okta.User;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserAssociationTest extends WebTests {

    static GetNotNullString appId = null;

    @Test
    @Order(1)
    public void addApp1() {

        admin().get("/api/v1/apps/display");
        appId = new GetNotNullString();
        App app1 = GenerateRandomData.generateApp();

        adminJson().body(mapToJson(app1)).post("/api/v1/apps").then().statusCode(200).body("id", appId)
                .body("label", is(app1.getLabel())).body("name", is(app1.getName()))
                .body("settings.oauthClient.redirect_uris", is(app1.getSettings().getOauthClient().getRedirectUris()));

        GetNotNullString userId = new GetNotNullString();
        User user = GenerateRandomData.generateUser();
        adminJson().body(mapToJson(user)).post("/api/v1/users").then().statusCode(200).body("id", userId)
                .body("profile.login", is(user.getProfile().getLogin()))
                .body("profile.firstName", is(user.getProfile().getFirstName()))
                .body("profile.lastName", is(user.getProfile().getLastName())).body("profile.locale", is("en_US"))
                .body("profile.timeZone", is("Pacific/Honolulu"));

        Map<String, String> profile = new LinkedHashMap<>();
        profile.put("foo", "bar");
        AppUser association = new AppUser(userId.getRecorded(), profile);

        adminJson().body(mapToJson(association)).post("/api/v1/apps/{appId}/users", appId.getRecorded()).then()
                .statusCode(200).body("id", notNullValue()).body("profile.foo", is(profile.get("foo")));

        // get App

        Response response = admin().get("/api/v1/apps/{appId}", appId.getRecorded());

        System.out.println(response.getBody().asPrettyString());
        // Duplicate test

        userId = new GetNotNullString();
        user = GenerateRandomData.generateUser();

        adminJson().body(mapToJson(user)).post("/api/v1/users").then().statusCode(200).body("id", userId)
                .body("profile.login", is(user.getProfile().getLogin()))
                .body("profile.firstName", is(user.getProfile().getFirstName()))
                .body("profile.lastName", is(user.getProfile().getLastName())).body("profile.locale", is("en_US"))
                .body("profile.timeZone", is("Pacific/Honolulu"));

        profile = new LinkedHashMap<>();
        profile.put("joy", "fun");
        association = new AppUser(userId.getRecorded(), profile);

        adminJson().body(mapToJson(association)).post("/api/v1/apps/{appId}/users", appId.getRecorded()).then()
                .statusCode(200).body("id", notNullValue());
        // .body("profile.joy", is(profile.get("fun")));

        response = admin().get("/api/v1/apps/{appId}", appId.getRecorded());

        System.out.println(response.getBody().asPrettyString());

    }

}
