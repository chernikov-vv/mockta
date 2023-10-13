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

import codes.vps.mockta.model.User;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserTests extends WebTests {

    static int noOfUsers = 20;
    static Map<GetNotNullString, User> users = null;
    static String baseURL = "/api/v1/users";

    @Test
    @Order(1)
    public void deleteAllUser() {

        Response response = admin().delete(baseURL);
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

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

        String url = baseURL;
        Set<String> userNames = new HashSet<>();

        do {

            Response response = admin().get(url);
            // response.then().log().all();
            JsonPath jsonPathEvaluator = response.jsonPath();
            List<String> logins = jsonPathEvaluator.getList("profile.login");
            userNames.addAll(logins);
            var links = getLinks(response);

            Response self = admin().get(links.get(IanaLinkRelations.SELF));
            Assertions.assertEquals(response.getBody().asString(), self.getBody().asString());

            url = links.get(IanaLinkRelations.NEXT);

        } while (url != null);

        // assertEquals(noOfUsers, userNames.size());
        assertEquals(users.values().stream().map(u->u.getProfile().getLogin()).collect(Collectors.toSet()), userNames);

    }


    private Map<LinkRelation, String> getLinks(Response r) {

        var links = r.getHeaders().getList("link");
        Map<LinkRelation, String> result = new HashMap<>();
        if (links != null) {
            for (Header h : links) {
                Link l = Link.valueOf(h.getValue());
                Assertions.assertFalse(result.containsKey(l.getRel()));
                result.put(l.getRel(), l.getHref());
            }
        }
        return result;

    }

    @Test
    @Order(5)
    public void deleteUser() {

        Map.Entry<GetNotNullString, User> sacrifice = users.entrySet().iterator().next();
        GetNotNullString userId = sacrifice.getKey();
        users.remove(userId);
        Response response = admin().delete(baseURL + "/{userId}", userId.getRecorded());
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

    }

    @Test
    @Order(6)
    public void getAllUserPostDelete() {
        getAllUsers();
    }
}
