package codes.vps.mockta;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.google.gson.Gson;

import codes.vps.mockta.obj.okta.User;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserTests extends WebTests {

	static int noOfUsers = 10;
	static Map<GetNotNullString, User> users = null;
	static String baseURL = "/api/v1/users";

	@Test
	@Order(1)
	public void deleteAllUser() throws Exception {

		Response response = admin().delete(baseURL);
		int statusCode = response.getStatusCode();
		assertEquals(statusCode, 200);

	}

	@Test
	@Order(2)
	public void addUsers() throws Exception {

		users = GenerateRandomData.generateUsers(noOfUsers);

		for (Map.Entry<GetNotNullString, User> entry : users.entrySet()) {
			User user = entry.getValue();
			String jsonStr = new Gson().toJson(user);
			//	String jsonStr = "{\"label\":\"Juana Bea\",\"name\":\"Lia\",\"profile\":\"Kilback\",\"signOnMode\":\"BASIC_AUTH\",\"status\":\"ACTIVE\",\"AppSettings\":{\"oauthClient\":{\"redirectUris\":[\"http://localhost\"]}}}";
				System.out.println(jsonStr);
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
	public void getUser() throws Exception {

		for (Map.Entry<GetNotNullString, User> entry : users.entrySet()) {
			User user = entry.getValue();
			GetNotNullString userId = entry.getKey();
			Response response = admin().get(baseURL + "/{userId}", userId.getRecorded());
			String login = response.jsonPath().getString("profile.login");
			assertEquals(login, user.getProfile().getLogin());
		}

	}

	@Test
	@Order(4)
	public void getAllUsers() throws Exception {

		Response response = admin().get(baseURL);
		JsonPath jsonPathEvaluator = response.jsonPath();
		System.out.println(response.getBody().asString());
		List<String> logins = jsonPathEvaluator.getList("userName");
		System.out.println("Get all Users");
		for (String login : logins) {
			System.out.println("Login : " + login);
		}
		assertEquals(noOfUsers, logins.size());

	}

	@Test
	@Order(5)
	public void deleteUser() throws Exception {

		for (Map.Entry<GetNotNullString, User> entry : users.entrySet()) {
			User user = entry.getValue();
			GetNotNullString userId = entry.getKey();

			Response response = admin().delete(baseURL + "/{userId}", userId.getRecorded());
			int statusCode = response.getStatusCode();
			assertEquals(statusCode, 200);

			break;
		}

	}

	@Test
	@Order(6)
	public void getAlluserPostDelete() throws Exception {
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
