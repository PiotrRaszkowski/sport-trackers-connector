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

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.raszkowski.sporttrackersconnector.JsonTestHelper;
import pl.raszkowski.sporttrackersconnector.configuration.ConnectorsConfiguration;
import pl.raszkowski.sporttrackersconnector.json.ResponseJsonParser;
import pl.raszkowski.sporttrackersconnector.rest.RESTExecutor;

import com.google.gson.JsonArray;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GarminAPIHandlerTest {

	private static final String ACTIVITY_SEARCH_SERVICE = "ACTIVITY_SEARCH_SERVICE";

	private static final String ACTIVITIES_RESOURCE = "activities";

	private static final String RESPONSE = "{ RESPONSE }";

	private RESTExecutor restExecutor;

	@Mock
	private ConnectorsConfiguration connectorsConfiguration;

	@Mock
	private ResponseJsonParser responseJsonParser;

	@InjectMocks
	private GarminAPIHandler garminAPIHandler;

	@Before
	public void setUp() {
		restExecutor = mock(RESTExecutor.class);

		garminAPIHandler = new GarminAPIHandler(restExecutor);

		MockitoAnnotations.initMocks(this);

		doReturn(RESPONSE).when(restExecutor).executeGET(eq(ACTIVITY_SEARCH_SERVICE), eq(ACTIVITIES_RESOURCE), any(Map.class));

		doReturn(ACTIVITY_SEARCH_SERVICE).when(connectorsConfiguration).getGarminConnectRESTActivitySearchService();
	}

	@Test
	public void getActivitiesWhenEmptyJson() {
		doReturn(JsonTestHelper.readJsonObject("{}")).when(responseJsonParser).parseAsJsonObject(RESPONSE);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(0, json.size());
	}

	@Test
	public void getActivitiesWhenNoResultsElement() {
		doReturn(JsonTestHelper.readJsonObject("{ response : \"content\"}")).when(responseJsonParser).parseAsJsonObject(RESPONSE);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(0, json.size());
	}

	@Test
	public void getActivitiesWhenNoActivitiesElement() {
		doReturn(JsonTestHelper.readJsonObject("{ results : {}}")).when(responseJsonParser).parseAsJsonObject(RESPONSE);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(0, json.size());
	}

	@Test
	public void getActivitiesWhenEmptyActivities() {
		doReturn(JsonTestHelper.readJsonObject("{ results : { activities : [] } }")).when(responseJsonParser).parseAsJsonObject(RESPONSE);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(0, json.size());
	}

	@Test
	public void getActivitiesWhenNotEmptyActivities() {
		doReturn(JsonTestHelper.readJsonObject("{ results : { activities : [{}] } }")).when(responseJsonParser).parseAsJsonObject(RESPONSE);

		JsonArray json = garminAPIHandler.getActivities(0, 20);

		assertNotNull(json);
		assertEquals(1, json.size());
	}
}