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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import codes.vps.mockta.model.App;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTests extends WebTests {

    static int noOfApps = 10;
    static Map<GetNotNullString, App> apps = null;
    static String baseURL = "/api/v1/apps";

    @Test
    @Order(1)
    public void deleteAllApp() {

        Response response = admin().delete(baseURL);
        int statusCode = response.getStatusCode();
        assertEquals(statusCode, 200);

    }

    @Test
    @Order(2)
    public void addApp() {

        apps = GenerateRandomData.generateApps(10);

        for (Map.Entry<GetNotNullString, App> entry : apps.entrySet()) {
            App app = entry.getValue();
            adminJson().body(mapToJson(app)).post(baseURL).then().statusCode(200).body("id", entry.getKey()).body("label", is(app.getLabel())).body("name", is(app.getName())).body("settings.oauthClient.redirect_uris", is(app.getSettings().getOauthClient().getRedirectUris()));
        }

    }

    @Test
    @Order(3)
    public void getApp() {

        for (Map.Entry<GetNotNullString, App> entry : apps.entrySet()) {
            App app = entry.getValue();
            GetNotNullString id = entry.getKey();
            Response response = admin().get(baseURL + "/{appId}", id.getRecorded());
            String label = response.jsonPath().getString("label");
            assertEquals(label, app.getLabel());
        }

    }

    @Test
    @Order(4)
    public void getAllApp() {

        Response response = admin().get(baseURL);
        JsonPath jsonPathEvaluator = response.jsonPath();

        List<String> labels = jsonPathEvaluator.getList("label");
        System.out.println("Get all App");
        for (String label : labels) {
            System.out.println("Label: " + label);
        }
        assertEquals(noOfApps, labels.size());

    }

    @Test
    @Order(5)
    public void deleteApp() {

        for (Map.Entry<GetNotNullString, App> entry : apps.entrySet()) {
            GetNotNullString id = entry.getKey();

            Response response = admin().delete(baseURL + "/{appId}", id.getRecorded());
            int statusCode = response.getStatusCode();
            assertEquals(statusCode, 200);

            break;
        }

    }

    @Test
    @Order(6)
    public void getAllAppPostDelete() {

        Response response = admin().get(baseURL);
        JsonPath jsonPathEvaluator = response.jsonPath();
        List<String> labels = jsonPathEvaluator.getList("label");
        System.out.println("Get all App1");
        for (String label : labels) {
            System.out.println("Label: " + label);
        }
        assertEquals(noOfApps - 1, labels.size());

    }

    @Test
    @Order(7)
    public void display() {
        admin().get(baseURL + "/display");
    }
}
