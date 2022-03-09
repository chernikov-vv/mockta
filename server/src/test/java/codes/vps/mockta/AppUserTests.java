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

import codes.vps.mockta.obj.okta.User;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserTests extends WebTests {

    static int noOfUsers = 10;
    static Map<GetNotNullString, User> users = null;
    static String baseURL = "/api/v1/users";

    @Test
    @Order(1)
    public void deleteAllUser() {

        Response response = admin().delete(baseURL);
        int statusCode = response.getStatusCode();
        assertEquals(statusCode, 200);

    }

    @Test
    @Order(2)
    public void addUsers() {

        users = GenerateRandomData.generateUsers(noOfUsers);

        for (Map.Entry<GetNotNullString, User> entry : users.entrySet()) {
            User user = entry.getValue();
            GetNotNullString userId = entry.getKey();
            adminJson().body(mapToJson(user)).post(baseURL).then().statusCode(200).body("id", userId)
                    .body("profile.login", is(user.getProfile().getLogin()))
                    .body("profile.firstName", is(user.getProfile().getFirstName()))
                    .body("profile.lastName", is(user.getProfile().getLastName())).body("profile.locale", is("en_US"))
                    .body("profile.timeZone", is("Pacific/Honolulu"));
        }

    }

    @Test
    @Order(3)
    public void getUser() {

        for (Map.Entry<GetNotNullString, User> entry : users.entrySet()) {
            User user = entry.getValue();
            GetNotNullString userId = entry.getKey();
            Response response = admin().get(baseURL + "/{userId}", userId.getRecorded());
            System.out.println("Get Arvind" + response.getBody().asString());
            String login = response.jsonPath().getString("profile.login");
            assertEquals(login, user.getProfile().getLogin());
        }

    }

    @Test
    @Order(4)
    public void getAllUsers() {

        Response response = admin().get(baseURL);
        JsonPath jsonPathEvaluator = response.jsonPath();
        //System.out.println("Arvind" + response.getBody().asString());
        List<String> logins = jsonPathEvaluator.getList("userName");
        System.out.println("Get all Users");
        for (String login : logins) {
            System.out.println("Login : " + login);
        }
        assertEquals(noOfUsers, logins.size());

    }

    @Test
    @Order(5)
    public void deleteUser() {

        for (Map.Entry<GetNotNullString, User> entry : users.entrySet()) {
            GetNotNullString userId = entry.getKey();

            Response response = admin().delete(baseURL + "/{userId}", userId.getRecorded());
            int statusCode = response.getStatusCode();
            assertEquals(statusCode, 200);

            break;
        }

    }

    @Test
    @Order(6)
    public void getAllUserPostDelete() {
        Response response = admin().get(baseURL);
        JsonPath jsonPathEvaluator = response.jsonPath();
        List<String> logins = jsonPathEvaluator.getList("userName");
        System.out.println("Get all Users");
        for (String login : logins) {
            System.out.println("Login : " + login);
        }
        assertEquals(noOfUsers - 1, logins.size());

    }
}
