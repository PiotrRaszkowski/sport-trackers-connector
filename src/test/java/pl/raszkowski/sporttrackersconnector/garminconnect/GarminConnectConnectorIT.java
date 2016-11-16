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

import pl.raszkowski.sporttrackersconnector.configuration.ConnectorsConfiguration;
import pl.raszkowski.sporttrackersconnector.json.ResponseJsonParser;
import pl.raszkowski.sporttrackersconnector.rest.RESTExecutor;
import pl.raszkowski.sporttrackersconnector.test.TestCredentials;
import pl.raszkowski.sporttrackersconnector.test.TestPropertiesLoader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static org.junit.Assert.assertNotNull;

public class GarminConnectConnectorIT {

	private static final String SEARCHABLE_FIELDS_RESOURCE = "searchable_fields";

	private TestPropertiesLoader testPropertiesLoader = TestPropertiesLoader.getInstance();

	private TestCredentials testCredentials;

	private ConnectorsConfiguration connectorsConfiguration = ConnectorsConfiguration.getInstance();

	private ResponseJsonParser responseJsonParser = new ResponseJsonParser();

	private GarminConnectConnector garminConnectConnector = new GarminConnectConnector();

	@Before
	public void setUp() {
		testCredentials = testPropertiesLoader.loadTestCredentials();
	}

	@Test
	public void authorize() {
		GarminConnectCredentials credentials = buildCredentials();

		garminConnectConnector.authorize(credentials);
	}

	private GarminConnectCredentials buildCredentials() {
		GarminConnectCredentials credentials = new GarminConnectCredentials();
		credentials.setUsername(testCredentials.getCorrectUsername());
		credentials.setPassword(testCredentials.getCorrectPassword());
		return credentials;
	}

	@Test
	public void executeGET() {
		GarminConnectCredentials credentials = buildCredentials();
		garminConnectConnector.authorize(credentials);

		RESTExecutor restExecutor = garminConnectConnector.getRESTExecutor();
		String result = restExecutor.executeGET(connectorsConfiguration.getGarminConnectRESTActivitySearchService(), SEARCHABLE_FIELDS_RESOURCE);

		JsonObject jsonObject = responseJsonParser.parseAsJsonObject(result);
		assertNotNull(jsonObject);
	}

	@Test
	public void getActivities() {
		GarminConnectCredentials credentials = buildCredentials();
		garminConnectConnector.authorize(credentials);

		GarminAPIHandler apiHandler = garminConnectConnector.getAPIHandler();
		JsonArray activities = apiHandler.getActivities(0, 5);

		assertNotNull(activities);
	}

}