/*
 * Copyright 2016 Piotr Raszkowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.raszkowski.sporttrackersconnector.garminconnect;

import java.util.HashMap;
import java.util.Map;

import pl.raszkowski.sporttrackersconnector.configuration.ConnectorsConfiguration;
import pl.raszkowski.sporttrackersconnector.rest.APIHandler;
import pl.raszkowski.sporttrackersconnector.rest.RESTExecutor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GarminAPIHandler extends APIHandler {

	private static final String ACTIVITIES_RESOURCE = "activities";

	private static final String START_PARAMETER = "start";
	private static final String LIMIT_PARAMETER = "limit";

	private static final String RESULTS_JSON_KEY = "results";
	private static final String ACTIVITIES_JSON_KEY = "activities";

	private ConnectorsConfiguration connectorsConfiguration = ConnectorsConfiguration.getInstance();

	GarminAPIHandler(RESTExecutor restExecutor) {
		super(restExecutor);
	}

	public JsonArray getActivities(int start, int limit) {
		Map<String, String> parameters = prepareGetActivitiesParameters(start, limit);

		String response = restExecutor.executeGET(connectorsConfiguration.getGarminConnectRESTActivitySearchService(), ACTIVITIES_RESOURCE, parameters);

		return parseGetActivitiesResponse(response);
	}

	private Map<String, String> prepareGetActivitiesParameters(int start, int limit) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put(START_PARAMETER, ""+start);
		parameters.put(LIMIT_PARAMETER, ""+limit);
		return parameters;
	}

	private JsonArray parseGetActivitiesResponse(String response) {
		JsonObject json = responseJsonParser.parseAsJsonObject(response);

		if (json.has(RESULTS_JSON_KEY)) {
			JsonObject results = json.getAsJsonObject(RESULTS_JSON_KEY);

			if (results.has(ACTIVITIES_JSON_KEY)) {
				return results.getAsJsonArray(ACTIVITIES_JSON_KEY);
			}
		}

		return new JsonArray();
	}
}
