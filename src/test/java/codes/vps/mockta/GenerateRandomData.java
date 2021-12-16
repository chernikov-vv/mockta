package codes.vps.mockta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.javafaker.Faker;

import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.AppSettings;
import codes.vps.mockta.obj.okta.Credentials;
import codes.vps.mockta.obj.okta.OAuthClient;
import codes.vps.mockta.obj.okta.Password;
import codes.vps.mockta.obj.okta.Profile;
import codes.vps.mockta.obj.okta.User;

public class GenerateRandomData {

	public static Map<GetNotNullString, App> generateApps(int size) {

		Faker faker = new Faker();
		HashMap<GetNotNullString, App> apps = new HashMap<>();
		for (int i = 0; i < size; i++) {

			App app = new App(faker.funnyName().name(), faker.name().firstName(), faker.name().lastName(),
					new AppSettings(new OAuthClient(Collections.singletonList("http://localhost"))));

			GetNotNullString appId = new GetNotNullString();
			apps.put(appId, app);
		}

		return apps;
	}

	public static App generateApp(GetNotNullString appId) {

		Faker faker = new Faker();

		App app = new App(faker.funnyName().name(), faker.name().firstName(), faker.name().lastName(),
				new AppSettings(new OAuthClient(Collections.singletonList("http://localhost"))));

		return app;
	}

	public static User generateUser(GetNotNullString userId) {

		Faker faker = new Faker();

		User user = new User(new Profile(faker.internet().emailAddress(), faker.name().firstName(),
				faker.name().lastName(), null, null),
				new Credentials(new Password(faker.lorem().characters(10, 15, true, true))));

		return user;
	}

	public static Map<GetNotNullString, User> generateUsers(int size) {

		Faker faker = new Faker();

		HashMap<GetNotNullString, User> users = new HashMap<>();
		for (int i = 0; i < size; i++) {
			User user = new User(new Profile(faker.internet().emailAddress(), faker.name().firstName(),
					faker.name().lastName(), null, null),
					new Credentials(new Password(faker.lorem().characters(10, 15, true, true))));

			GetNotNullString appId = new GetNotNullString();
			users.put(appId, user);
		}

		return users;
	}
}
