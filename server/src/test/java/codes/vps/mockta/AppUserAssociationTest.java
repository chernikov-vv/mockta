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
	static GetNotNullString appId2 = null;
	static String baseAppURL = "/api/v1/apps";

	@Test
	@Order(1)
	public void addApp1() throws Exception {

		Response response = admin().get("/api/v1/apps/display");
		appId = new GetNotNullString();
		App app1 = GenerateRandomData.generateApp(appId);

		adminJson().body(mapToJson(app1)).post(baseAppURL).then().statusCode(200).body("id", appId)
				.body("label", is(app1.getLabel())).body("name", is(app1.getName()))
				.body("settings.oauthClient.redirect_uris", is(app1.getSettings().getOauthClient().getRedirectUris()));

		GetNotNullString userId = new GetNotNullString();
		User user = GenerateRandomData.generateUser(userId);
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

		response = admin().get(baseAppURL + "/{appId}", appId.getRecorded());

		System.out.println(response.getBody().asPrettyString());
		// Duplicate test

		userId = new GetNotNullString();
		user = GenerateRandomData.generateUser(userId);

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

		response = admin().get(baseAppURL + "/{appId}", appId.getRecorded());

		System.out.println(response.getBody().asPrettyString());

	}

}