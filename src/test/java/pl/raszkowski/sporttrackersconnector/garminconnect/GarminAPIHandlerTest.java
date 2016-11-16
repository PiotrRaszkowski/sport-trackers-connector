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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.raszkowski.sporttrackersconnector.configuration.ConnectorsConfiguration;
import pl.raszkowski.sporttrackersconnector.rest.RESTExecutor;

import com.google.gson.JsonArray;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GarminAPIHandlerTest {

	private static final String ACTIVITY_SEARCH_SERVICE = "ACTIVITY_SEARCH_SERVICE";

	private static final String ACTIVITIES_RESOURCE = "activities";

	private RESTExecutor restExecutor;

	@Mock
	private ConnectorsConfiguration connectorsConfiguration;

	@InjectMocks
	private GarminAPIHandler garminAPIHandler;

	@Before
	public void setUp() {
		restExecutor = mock(RESTExecutor.class);

		garminAPIHandler = new GarminAPIHandler(restExecutor);

		MockitoAnnotations.initMocks(this);

		doReturn(ACTIVITY_SEARCH_SERVICE).when(connectorsConfiguration).getGarminConnectRESTActivitySearchService();
	}

	@Test
	public void getActivitiesWhenEmptyJson() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("start", ""+0);
		parameters.put("limit", ""+20);

		doReturn("{}").when(restExecutor).executeGET(ACTIVITY_SEARCH_SERVICE, ACTIVITIES_RESOURCE, parameters);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(0, json.size());
	}

	@Test
	public void getActivitiesWhenEmptyResponse() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("start", ""+0);
		parameters.put("limit", ""+20);

		doReturn("").when(restExecutor).executeGET(ACTIVITY_SEARCH_SERVICE, ACTIVITIES_RESOURCE, parameters);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(0, json.size());
	}

	@Test
	public void getActivitiesWhenNoResultsElement() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("start", ""+0);
		parameters.put("limit", ""+20);

		doReturn("{ response : \"content\"}").when(restExecutor).executeGET(ACTIVITY_SEARCH_SERVICE, ACTIVITIES_RESOURCE, parameters);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(0, json.size());
	}

	@Test
	public void getActivitiesWhenNoActivitiesElement() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("start", ""+0);
		parameters.put("limit", ""+20);

		doReturn("{ results : {}}").when(restExecutor).executeGET(ACTIVITY_SEARCH_SERVICE, ACTIVITIES_RESOURCE, parameters);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(0, json.size());
	}

	@Test
	public void getActivitiesWhenEmptyActivities() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("start", ""+0);
		parameters.put("limit", ""+20);

		doReturn("{ results : { activities : [] } }" ).when(restExecutor).executeGET(ACTIVITY_SEARCH_SERVICE, ACTIVITIES_RESOURCE, parameters);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(0, json.size());
	}

	@Test
	public void getActivitiesWhenNotEmptyActivities() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("start", ""+0);
		parameters.put("limit", ""+20);

		doReturn("{ results : { activities : [{}] } }" ).when(restExecutor).executeGET(ACTIVITY_SEARCH_SERVICE, ACTIVITIES_RESOURCE, parameters);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(1, json.size());
	}
}