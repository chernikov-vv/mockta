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

import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.AppSettings;
import codes.vps.mockta.obj.okta.Credentials;
import codes.vps.mockta.obj.okta.OAuthClient;
import codes.vps.mockta.obj.okta.Password;
import codes.vps.mockta.obj.okta.Profile;
import codes.vps.mockta.obj.okta.User;
import com.github.javafaker.Faker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

	public static App generateApp() {

		Faker faker = new Faker();

		return new App(faker.funnyName().name(), faker.name().firstName(), faker.name().lastName(),
				new AppSettings(new OAuthClient(Collections.singletonList("http://localhost"))));
	}

	public static User generateUser() {

		Faker faker = new Faker();
		ArrayList<String> roles = new ArrayList<>();
		roles.add("role1");
		roles.add("role2");
		roles.add("role3");
		roles.add("role4");
		//tenancylst.add(tenancyInfo);

		return new User(
				new Profile(faker.internet().emailAddress(), faker.internet().emailAddress(), faker.name().firstName(),
						faker.name().lastName(), null, null),
				new Credentials(new Password(faker.lorem().characters(10, 15, true, true))));
	}

	public static Map<GetNotNullString, User> generateUsers(int size) {

		Faker faker = new Faker();

		HashMap<GetNotNullString, User> users = new HashMap<>();
		for (int i = 0; i < size; i++) {
			ArrayList<String> roles = new ArrayList<>();
			roles.add("role1");
			roles.add("role2");
			roles.add("role3");
			roles.add("role4");

			User user = new User(
					new Profile(faker.internet().emailAddress(), faker.internet().emailAddress(),
							faker.name().firstName(), faker.name().lastName(), null, null),
					new Credentials(new Password(faker.lorem().characters(10, 15, true, true))));
			 System.out.println(new Gson().toJson(user));
			GetNotNullString appId = new GetNotNullString();
			users.put(appId, user);
		}

		return users;
	}
}
