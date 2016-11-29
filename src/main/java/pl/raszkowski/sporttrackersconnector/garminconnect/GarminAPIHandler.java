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
	private static final String SORT_ORDER_PARAMETER = "sortOrder";
	private static final String SORT_FIELD_PARAMETER = "sortField";

	private static final String RESULTS_JSON_KEY = "results";
	private static final String ACTIVITIES_JSON_KEY = "activities";

	private ConnectorsConfiguration connectorsConfiguration = ConnectorsConfiguration.getInstance();

	GarminAPIHandler(RESTExecutor restExecutor) {
		super(restExecutor);
	}

	/**
	 * <pre>
	 *     Finds activities with start and limit offsets.
	 * </pre>
	 *
	 * @param start starting activity
	 * @param limit limit of activities to retrieve
	 * @return JsonArray with activities
	 */
	public JsonArray getActivities(int start, int limit) {
		ActivitiesSearchFields activitiesSearchFields = new ActivitiesSearchFields();
		activitiesSearchFields.setStart(start);
		activitiesSearchFields.setLimit(limit);
		return getActivities(activitiesSearchFields);
	}

	/**
	 * <pre>
	 * Finds activities using given fields from {@link ActivitiesSearchFields}.
	 *
	 * Possible fields configurations:
	 * @see <a href="https://connect.garmin.com/proxy/activity-search-service-1.0/axm/searchable_fields">searchable_fields</a>
	 * @see <a href="https://connect.garmin.com/proxy/activity-service-1.0/axm/activity_types">activity_types</a>
	 * @see <a href="https://connect.garmin.com/proxy/activity-service-1.0/axm/event_types">event_types</a>
	 * @see <a href="https://connect.garmin.com/proxy/activity-service-1.0/axm/units">units</a>
	 * @see <a href="https://connect.garmin.com/proxy/activity-search-service-1.0/axm/operators">operators</a>
	 * @see <a href="https://connect.garmin.com/proxy/activity-search-service-1.0/axm/query_options">query_options</a>
	 * </pre>

	 * @param activitiesSearchFields search details
	 * @return JsonArray with activities
	 */
	public JsonArray getActivities(ActivitiesSearchFields activitiesSearchFields) {
		Map<String, String> parameters = prepareGetActivitiesParameters(activitiesSearchFields);

		String response = restExecutor.executeGET(connectorsConfiguration.getGarminConnectRESTActivitySearchService(), ACTIVITIES_RESOURCE, parameters);

		return parseGetActivitiesResponse(response);
	}

	private Map<String, String> prepareGetActivitiesParameters(ActivitiesSearchFields activitiesSearchFields) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put(START_PARAMETER, ""+activitiesSearchFields.getStart());
		parameters.put(LIMIT_PARAMETER, ""+activitiesSearchFields.getLimit());
		if (activitiesSearchFields.getSortOrder() != null) {
			parameters.put(SORT_ORDER_PARAMETER, activitiesSearchFields.getSortOrder());
		}
		if (activitiesSearchFields.getSortField() != null) {
			parameters.put(SORT_FIELD_PARAMETER, activitiesSearchFields.getSortField());
		}
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
