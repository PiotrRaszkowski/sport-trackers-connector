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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;

import pl.raszkowski.sporttrackersconnector.test.TestCredentials;
import pl.raszkowski.sporttrackersconnector.test.TestPropertiesLoader;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
		assertTrue(activities.size() > 1);

		JsonPrimitive beginTimestamp1 = activities.get(0)
				.getAsJsonObject()
				.getAsJsonObject("activity")
				.getAsJsonObject("activitySummary")
				.getAsJsonObject("BeginTimestamp")
				.getAsJsonPrimitive("value");
		LocalDateTime beginDateTime1 = LocalDateTime.parse(beginTimestamp1.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

		JsonPrimitive beginTimestamp2 = activities.get(1)
				.getAsJsonObject()
				.getAsJsonObject("activity")
				.getAsJsonObject("activitySummary")
				.getAsJsonObject("BeginTimestamp")
				.getAsJsonPrimitive("value");
		LocalDateTime beginDateTime2 = LocalDateTime.parse(beginTimestamp2.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

		assertTrue(beginDateTime1.isAfter(beginDateTime2));
	}

	@Test
	public void getActivitiesGivenBeginTimestampCondition() {
		LocalDateTime filterDateTime = LocalDateTime.of(2015, 1, 1, 12, 0, 0);

		ActivitiesSearchFields activitiesSearchFields = new ActivitiesSearchFields();
		activitiesSearchFields.setStart(0);
		activitiesSearchFields.setLimit(5);
		activitiesSearchFields.setSortField("beginTimestamp");
		activitiesSearchFields.setSortOrder(ActivitiesSearchFields.SortOrder.ASC);
		activitiesSearchFields.addCondition(
				new ActivitiesSearchFields.Condition(
						"beginTimestamp",
						ActivitiesSearchFields.Operator.GREATER_THAN, filterDateTime
				)
		);

		JsonArray activities = garminAPIHandler.getActivities(activitiesSearchFields);

		assertNotNull(activities);
		assertTrue(activities.size() > 0);

		JsonPrimitive beginTimestamp1 = activities.get(0)
				.getAsJsonObject()
				.getAsJsonObject("activity")
				.getAsJsonObject("activitySummary")
				.getAsJsonObject("BeginTimestamp")
				.getAsJsonPrimitive("value");
		LocalDateTime beginDateTime1 = LocalDateTime.parse(beginTimestamp1.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

		assertTrue(beginDateTime1.isAfter(filterDateTime));
	}

}
