package codes.vps.mockta;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import codes.vps.mockta.obj.okta.Credentials;
import codes.vps.mockta.obj.okta.Password;
import codes.vps.mockta.obj.okta.Profile;
import codes.vps.mockta.obj.okta.User;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests extends WebTests {

	static GetNotNullString userId = null;
	static GetNotNullString userId2 = null;

	@Test
	@Order(1)
	public void addUsers() throws Exception {

		User user = new User(new Profile("t105@codes.vps", "Guy1", "BlueShirt1", null, null),
				new Credentials(new Password("BubbleGumIceCream1")));

		userId = new GetNotNullString();

		adminJson().body(mapToJson(user)).post("/api/v1/users").then().statusCode(200).body("id", userId)
				.body("profile.login", is(user.getProfile().getLogin()))
				.body("profile.firstName", is(user.getProfile().getFirstName()))
				.body("profile.lastName", is(user.getProfile().getLastName())).body("profile.locale", is("en_US"))
				.body("profile.timeZone", is("Pacific/Honolulu"));

		User user2 = new User(new Profile("t201@codes.vps", "Arvind", "Kapse", null, null),
				new Credentials(new Password("Zipphy12654645")));

		userId2 = new GetNotNullString();

		adminJson().body(mapToJson(user2)).post("/api/v1/users").then().statusCode(200).body("id", userId2)
				.body("profile.login", is(user2.getProfile().getLogin()))
				.body("profile.firstName", is(user2.getProfile().getFirstName()))
				.body("profile.lastName", is(user2.getProfile().getLastName())).body("profile.locale", is("en_US"))
				.body("profile.timeZone", is("Pacific/Honolulu"));
	}

	@Test
	@Order(2)
	public void getUser() throws Exception {
		System.out.println("APPD    " + userId.getRecorded());

		Response response = admin().get("/api/v1/users/{userId}", userId.getRecorded());
		String login = response.jsonPath().getString("profile.login");
		assertEquals(login, "t105@codes.vps");

		response = admin().get("/api/v1/users/{appId}", userId2.getRecorded());
		login = response.jsonPath().getString("profile.login");
		assertEquals(login, "t201@codes.vps");

	}

	@Test
	@Order(3)
	public void getAllUsers() throws Exception {
		System.out.println("APPD    " + userId.getRecorded());

		Response response = admin().get("/api/v1/users");
		JsonPath jsonPathEvaluator = response.jsonPath();

		List<String> labels = jsonPathEvaluator.getList("login");

		for (String label : labels) {
			System.out.println("Label: " + label);
		}
		assertEquals(labels.size(), 3);

	}

	@Test
	@Order(4)
	public void deleteUser() throws Exception {
		System.out.println("APPD    " + userId.getRecorded());

		Response response = admin().delete("/api/v1/users/{userId}", userId.getRecorded());
		int statusCode = response.getStatusCode();
		assertEquals(statusCode, 200);

	}

	@Test
	@Order(5)
	public void getAlluser1() throws Exception {
		System.out.println("APPD    " + userId.getRecorded());

		Response response = admin().get("/api/v1/users");
		JsonPath jsonPathEvaluator = response.jsonPath();
		List<String> labels = jsonPathEvaluator.getList("login");

		for (String label : labels) {
			System.out.println("Label: " + label);
		}
		assertEquals(labels.size(), 2);

	}
}
