/*
 * Copyright (c) 2021 Pawel S. Veselov
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

package codes.vps.mockta.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import codes.vps.mockta.obj.okta.App;
import codes.vps.mockta.obj.okta.ErrorObject;
import lombok.NonNull;

public class AppsDB {

	public final static Map<String, OktaApp> apps = new ConcurrentHashMap<>();
	public final static Map<String, OktaApp> appsById = new ConcurrentHashMap<>();

	public static OktaApp addApp(App app) {

		OktaApp oktaApp = new OktaApp(app);

		apps.compute(oktaApp.getName(), (k, v) -> {
			if (v != null) {
				throw ErrorObject.duplicate("app name " + k).boom();
			}
			return new OktaApp(app);
		});

		appsById.put(oktaApp.getId(), oktaApp);
		return oktaApp;

	}
	
	public static OktaApp updateApp(OktaApp oktaApp) {

		
		OktaApp oldOkaApp = apps.get(oktaApp.getName());

		apps.put(oktaApp.getName(), oktaApp);
		appsById.put(oktaApp.getId(), oktaApp);
		return oktaApp;

	}
	
	
	public static OktaApp updateApp(OktaApp oktaApp,String id) {

		OktaApp app = appsById.get(id);
		if (app == null) {
			throw ErrorObject.notFound("app id " + id).boom();
		}
		return app;

	}

	@NonNull
	public static OktaApp getApp(String id) {

		OktaApp app = appsById.get(id);
		if (app == null) {
			throw ErrorObject.notFound("app id " + id).boom();
		}
		return app;

	}

	public static boolean deleteApp(String id) {

		OktaApp app = appsById.get(id);
		if (app == null) {
			throw ErrorObject.notFound("app id " + id).boom();
		}
		appsById.remove(id);
		apps.remove(app.getName());
		return true;

	}

	@NonNull
	public static List<OktaApp> getAllApps() {

		List<OktaApp> apps = new ArrayList<OktaApp>(appsById.values());
		if (apps == null) {
			throw ErrorObject.notFound("No Apps  ").boom();
		}
		return apps;

	}
	
	@NonNull
	public static void display() {

		 System.out.println("Cal ########################################################" );
				 
				 for (Entry<String, OktaApp> entry : apps.entrySet()) {
				 String key = entry.getKey().toString();
				 OktaApp value = entry.getValue();
				 System.out.println("key, " + key + " value " + value);
				 }
	}

}
