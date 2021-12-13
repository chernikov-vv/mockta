package codes.vps.mockta;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.AppSettings;
import codes.vps.mockta.obj.okta.AppUser;
import codes.vps.mockta.obj.okta.Credentials;
import codes.vps.mockta.obj.okta.OAuthClient;
import codes.vps.mockta.obj.okta.Password;
import codes.vps.mockta.obj.okta.Profile;
import codes.vps.mockta.obj.okta.User;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserAssociationTest extends WebTests {

	static GetNotNullString appId = null;
	static GetNotNullString appId2 = null;

	@Test
	@Order(1)
	public void addApp1() throws Exception {

		 Response response = admin().get("/api/v1/apps/display");
		App app1 = new App("test2.label", "test1", "test1",
				new AppSettings(new OAuthClient(Collections.singletonList("http://localhost"))));

		appId = new GetNotNullString();

		adminJson().body(mapToJson(app1)).post("/api/v1/apps").then().statusCode(200).body("id", appId)
				.body("label", is(app1.getLabel())).body("name", is(app1.getName()))
				.body("settings.oauthClient.redirect_uris", is(app1.getSettings().getOauthClient().getRedirectUris()));

	
		
		
		User user = new User(new Profile("test2016@codes.vps", "Guy", "BlueShirt", null, null), new Credentials(new Password("BubbleGumIceCream")));
        GetNotNullString userId = new GetNotNullString();

        adminJson()
                .body(mapToJson(user))
                .post("/api/v1/users")
                .then()
                .statusCode(200)
                .body("id", userId)
                .body("profile.login", is(user.getProfile().getLogin()))
                .body("profile.firstName", is(user.getProfile().getFirstName()))
                .body("profile.lastName", is(user.getProfile().getLastName()))
                .body("profile.locale", is("en_US"))
                .body("profile.timeZone", is("Pacific/Honolulu"));
        
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
        
        //Duplicate test
        
         user = new User(new Profile("test2018@codes.vps", "Arvind", "Kapse", null, null), new Credentials(new Password("BubbleGumIceCream")));
         userId = new GetNotNullString();

        adminJson()
                .body(mapToJson(user))
                .post("/api/v1/users")
                .then()
                .statusCode(200)
                .body("id", userId)
                .body("profile.login", is(user.getProfile().getLogin()))
                .body("profile.firstName", is(user.getProfile().getFirstName()))
                .body("profile.lastName", is(user.getProfile().getLastName()))
                .body("profile.locale", is("en_US"))
                .body("profile.timeZone", is("Pacific/Honolulu"));
        
       profile = new LinkedHashMap<>();
        profile.put("joy", "fun");
         association = new AppUser(userId.getRecorded(), profile);

        adminJson()
                .body(mapToJson(association))
                .post("/api/v1/apps/{appId}/users", appId.getRecorded())
                .then()
                .statusCode(200)
                .body("id", notNullValue());
                //.body("profile.joy", is(profile.get("fun")));
                
         response = admin().get("/api/v1/apps/display");

	}

	
}
