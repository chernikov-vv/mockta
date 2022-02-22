package codes.vps.mockta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.github.javafaker.Faker;
import com.google.gson.Gson;

import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.AppSettings;
import codes.vps.mockta.obj.okta.Credentials;
import codes.vps.mockta.obj.okta.EsAppData2;
import codes.vps.mockta.obj.okta.OAuthClient;
import codes.vps.mockta.obj.okta.Password;
import codes.vps.mockta.obj.okta.Profile;
import codes.vps.mockta.obj.okta.Tenancy;
import codes.vps.mockta.obj.okta.TenancyInfo;
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
		ArrayList<String> roles = new ArrayList<>();
		roles.add("role1");
		roles.add("role2");
		roles.add("role3");
		roles.add("role4");
		Tenancy tenancyInfo = new Tenancy("tenancy String", roles);
		ArrayList<Tenancy> tenancylst = new ArrayList<Tenancy>();
		tenancylst.add(tenancyInfo);
		//tenancylst.add(tenancyInfo);
		TenancyInfo rf = new TenancyInfo(tenancylst);
	
		Map<String, TenancyInfo> esAppData2 = new ConcurrentHashMap<>();
	//	EsAppData2 espAppData2 = new EsAppData2("cliendID value ", tenancylst);
		esAppData2.put("cliendID value ", rf);
		esAppData2.put("cliendID value2 ", rf);
		User user = new User(
				new Profile(faker.internet().emailAddress(), faker.internet().emailAddress(), faker.name().firstName(),
						faker.name().lastName(), null, null, esAppData2),
				new Credentials(new Password(faker.lorem().characters(10, 15, true, true))));

		return user;
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
			Tenancy tenancyInfo = new Tenancy("tenancy String", roles);
			ArrayList<Tenancy> tenancylst = new ArrayList<Tenancy>();
			tenancylst.add(tenancyInfo);
			//tenancylst.add(tenancyInfo);
			TenancyInfo rf = new TenancyInfo(tenancylst);
		
			Map<String, TenancyInfo> esAppData2 = new ConcurrentHashMap<>();
		//	EsAppData2 espAppData2 = new EsAppData2("cliendID value ", tenancylst);
			esAppData2.put("cliendID value ", rf);
			esAppData2.put("cliendID value2 ", rf);

			User user = new User(
					new Profile(faker.internet().emailAddress(), faker.internet().emailAddress(),
							faker.name().firstName(), faker.name().lastName(), null, null, esAppData2),
					new Credentials(new Password(faker.lorem().characters(10, 15, true, true))));
			Gson gd = new Gson();
			 System.out.println(new Gson().toJson(user));
			GetNotNullString appId = new GetNotNullString();
			users.put(appId, user);
		}

		return users;
	}
}
