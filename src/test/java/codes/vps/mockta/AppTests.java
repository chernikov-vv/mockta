package codes.vps.mockta;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.AppSettings;
import codes.vps.mockta.obj.okta.OAuthClient;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTests extends WebTests {

	static GetNotNullString appId = null;
	static GetNotNullString appId2 = null;

	@Test
	@Order(1)
	public void addApp() throws Exception {

		App app1 = new App("test2.label", "test1", "test1",
				new AppSettings(new OAuthClient(Collections.singletonList("http://localhost"))));

		appId = new GetNotNullString();

		adminJson().body(mapToJson(app1)).post("/api/v1/apps").then().statusCode(200).body("id", appId)
				.body("label", is(app1.getLabel())).body("name", is(app1.getName()))
				.body("settings.oauthClient.redirect_uris", is(app1.getSettings().getOauthClient().getRedirectUris()));

		App app2 = new App("test3.label", "test3", "test3",
				new AppSettings(new OAuthClient(Collections.singletonList("http://localhost"))));

		appId2 = new GetNotNullString();

		adminJson().body(mapToJson(app2)).post("/api/v1/apps").then().statusCode(200).body("id", appId2)
				.body("label", is(app2.getLabel())).body("name", is(app2.getName()))
				.body("settings.oauthClient.redirect_uris", is(app2.getSettings().getOauthClient().getRedirectUris()));

	}

	@Test
	@Order(2)
	public void getApp() throws Exception {
	
		Response response = admin().get("/api/v1/apps/{appId}", appId.getRecorded());
		String label = response.jsonPath().getString("label");
		assertEquals(label, "test2.label");

		response = admin().get("/api/v1/apps/{appId}", appId2.getRecorded());
		label = response.jsonPath().getString("label");
		assertEquals(label, "test3.label");

	}

	@Test
	@Order(3)
	public void getAllApp() throws Exception {
	
		Response response = admin().get("/api/v1/apps");
		JsonPath jsonPathEvaluator = response.jsonPath();
		List<String> labels = jsonPathEvaluator.getList("label");
		System.out.println("Get all App");
		for (String label : labels) {
			System.out.println("Label: " + label);
		}
		assertEquals(labels.size(), 2);

	}

	@Test
	@Order(4)
	public void deleteApp() throws Exception {
	
		Response response = admin().delete("/api/v1/apps/{appId}", appId.getRecorded());
		int statusCode = response.getStatusCode();
		assertEquals(statusCode, 200);

	}

	@Test
	@Order(5)
	public void getAllApp1() throws Exception {
	
		Response response = admin().get("/api/v1/apps");
		JsonPath jsonPathEvaluator = response.jsonPath();
		List<String> labels = jsonPathEvaluator.getList("label");
		System.out.println("Get all App1");
		for (String label : labels) {
			System.out.println("Label: " + label);
		}
		assertEquals(labels.size(), 1);

	}
	
	@Test
	@Order(6)
	public void display() throws Exception {
	
		Response response = admin().get("/api/v1/apps/display");
		

	}
}
