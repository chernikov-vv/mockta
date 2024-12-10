package codes.vps.mockta;

import codes.vps.mockta.model.*;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.commons.lang3.mutable.MutableObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class OIDCTest extends WebTests {

    @Test
    public void oidcTest(){

        String login ="test1@test1.test";

        Profile p = new Profile().setEmail(login).setLogin(login).setFirstName("Mr").setLastName("Test");
        User user = new User(p, new Credentials(new Password("SecretPassword123")));
        GetNotNullString userId = new GetNotNullString();

        login = login.toLowerCase();

        adminJson()
                .body(mapToJson(user))
                .post("/api/v1/users")
                .then()
                .statusCode(200)
                .body("id", userId)
                .body("profile.login", is(login))
                .body("profile.email", is(user.getProfile().getEmail()))
                .body("profile.firstName", is(user.getProfile().getFirstName()))
                .body("profile.lastName", is(user.getProfile().getLastName()))
                .body("profile.locale", is("en_US"))
                .body("profile.timeZone", is("Pacific/Honolulu"));

        App app = App.builder()
                .signOnMode(SignOnMode.OPENID_CONNECT)
                .label("test1.label")
                .name("test1")
                .profile("test1")
                .settings(new AppSettings(new OAuthClient(Collections.singletonList("http://localhost")))).build();

        GetNotNullString appId = new GetNotNullString();

        adminJson().body(mapToJson(app)).post("/api/v1/apps").then()
                .statusCode(200)
                .body("id", appId)
                .body("label", is(app.getLabel()))
                .body("name", is(app.getName()))
                .body("settings.oauthClient.redirect_uris", is(app.getSettings().getOauthClient().getRedirectUris()));

        Map<String, String> profile = new LinkedHashMap<>();
        profile.put("foo", "bar");
        AppUser association = new AppUser(userId.getRecorded(), profile);

        adminJson()
                .body(mapToJson(association))
                .post("/api/v1/apps/{appId}/users", appId.getRecorded())
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("profile.foo", is(profile.get("foo")));

        String baseUrl = "http://localhost:" + serverPort;

        GetSpecificString issuerId = new GetSpecificString(baseUrl);
        GetNotNullString tokenUrl = new GetNotNullString();
        GetNotNullString revokeUrl = new GetNotNullString();
        GetNotNullString introspectUrl = new GetNotNullString();
        GetNotNullString userInfoURL = new GetNotNullString();

        String wkc = "/.well-known/openid-configuration";

        userJson()
                .get(wkc)
                .then()
                .statusCode(200)
                .body("issuer", issuerId)
                .body("revocation_endpoint", revokeUrl)
                .body("introspection_endpoint", introspectUrl)
                .body("userinfo_endpoint", userInfoURL)
                .body("token_endpoint", tokenUrl);

        MutableObject<String> accessToken = new MutableObject<>();

        Map<String, String> tokenParams = new HashMap<>();
        tokenParams.put("grant_type", "password");
        tokenParams.put("client_id", appId.getRecorded());
        tokenParams.put("username", login);
        tokenParams.put("password", "SecretPassword123");

        ExtractableResponse<Response> r = user().queryParams(tokenParams)
                .post(tokenUrl.getRecorded())
                .then()
                .statusCode(200)
                .extract();
        accessToken.setValue(r.path("access_token"));

        Map<String, String> introspectParams = new HashMap<>();
        introspectParams.put("token", accessToken.getValue());

        boolean active = userJson().queryParams(introspectParams)
                .post(introspectUrl.getRecorded())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getBoolean("active");

        Assertions.assertTrue(active);

        user()
                .header(new Header("authorization", "Bearer " + accessToken.getValue()))
                .get(userInfoURL.getRecorded())
                .then()
                .statusCode(200)
                .body("email", Matchers.equalTo(login))
                .body("name", Matchers.equalTo("Mr"))
                .body("family_name", Matchers.equalTo("Test"));

        Map<String, String> revokeParams = new HashMap<>();
        revokeParams.put("token", accessToken.getValue());
        user().queryParams(revokeParams)
                .post(revokeUrl.getRecorded())
                .then()
                .statusCode(200);

        boolean active1 = userJson().queryParams(introspectParams)
                .post(introspectUrl.getRecorded())
                .then()
                .statusCode(200).extract()
                .jsonPath()
                .getBoolean("active");

        Assertions.assertFalse(active1);

        user()
                .header(new Header("authorization", "Bearer " + accessToken.getValue()))
                .get(userInfoURL.getRecorded())
                .then()
                .statusCode(401);

    }

}
