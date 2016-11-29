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

import org.junit.Before;
import org.junit.Test;

import pl.raszkowski.sporttrackersconnector.test.TestCredentials;
import pl.raszkowski.sporttrackersconnector.test.TestPropertiesLoader;

import com.google.gson.JsonArray;

import static org.junit.Assert.assertNotNull;

public class GarminAPIHandlerIT {
	private TestPropertiesLoader testPropertiesLoader = TestPropertiesLoader.getInstance();

	private TestCredentials testCredentials;

	private GarminConnectConnector garminConnectConnector = new GarminConnectConnector();

	private GarminAPIHandler garminAPIHandler;

	@Before
	public void setUp() {
		testCredentials = testPropertiesLoader.loadTestCredentials();

		GarminConnectCredentials credentials = buildCredentials();
		garminConnectConnector.authorize(credentials);

		garminAPIHandler = garminConnectConnector.getAPIHandler();
	}

	private GarminConnectCredentials buildCredentials() {
		GarminConnectCredentials credentials = new GarminConnectCredentials();
		credentials.setUsername(testCredentials.getCorrectUsername());
		credentials.setPassword(testCredentials.getCorrectPassword());
		return credentials;
	}

	@Test
	public void getActivitiesGivenStartAndLimit() {
		JsonArray activities = garminAPIHandler.getActivities(0, 5);

		assertNotNull(activities);
	}

	@Test
	public void getActivitiesGivenSortFieldAndSortOrder() {
		ActivitiesSearchFields activitiesSearchFields = new ActivitiesSearchFields();
		activitiesSearchFields.setStart(0);
		activitiesSearchFields.setLimit(5);
		activitiesSearchFields.setSortField("beginTimestamp");
		activitiesSearchFields.setSortOrder(ActivitiesSearchFields.SortOrder.DESC);

		JsonArray activities = garminAPIHandler.getActivities(activitiesSearchFields);

		assertNotNull(activities);
	}

}
