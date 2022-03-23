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

import codes.vps.mockta.model.App;
import codes.vps.mockta.model.AppSettings;
import codes.vps.mockta.model.Credentials;
import codes.vps.mockta.model.OAuthClient;
import codes.vps.mockta.model.Password;
import codes.vps.mockta.model.Profile;
import codes.vps.mockta.model.SignOnMode;
import codes.vps.mockta.model.User;
import com.github.javafaker.Faker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GenerateRandomData {

	public static Map<GetNotNullString, App> generateApps(int size) {

		Faker faker = new Faker();
		HashMap<GetNotNullString, App> apps = new HashMap<>();
		for (int i = 0; i < size; i++) {

			App app = generateApp();

			GetNotNullString appId = new GetNotNullString();
			apps.put(appId, app);
		}

		return apps;
	}

	public static App generateApp() {

		Faker faker = new Faker();

		return App.builder()
				.signOnMode(SignOnMode.OPENID_CONNECT)
				.name(faker.funnyName().name())
				.label(faker.name().firstName())
				.profile(faker.name().lastName())
				.settings(new AppSettings(new OAuthClient(Collections.singletonList("http://localhost"))))
				.build();
	}

	public static User generateUser() {

		Faker faker = new Faker();

		return new User(
				new Profile()
						.setEmail(faker.internet().emailAddress())
						.setLogin(faker.internet().emailAddress())
						.setFirstName(faker.name().firstName())
						.setLastName(faker.name().lastName()),
				new Credentials(new Password(faker.lorem().characters(10, 15, true, true))));

	}

	public static Map<GetNotNullString, User> generateUsers(int size) {

		Faker faker = new Faker();

		HashMap<GetNotNullString, User> users = new HashMap<>();
		for (int i = 0; i < size; i++) {

			User user = new User(
					new Profile().setEmail(faker.internet().emailAddress()).setLogin(faker.internet().emailAddress()).setFirstName(faker.name().firstName()).setLastName(faker.name().lastName()),
 					new Credentials(new Password(faker.lorem().characters(10, 15, true, true))));
			GetNotNullString appId = new GetNotNullString();
			users.put(appId, user);
		}

		return users;
	}
}
