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

import java.io.IOException;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;

import pl.raszkowski.sporttrackersconnector.test.TestCredentials;
import pl.raszkowski.sporttrackersconnector.test.TestPropertiesLoader;

import static org.junit.Assert.assertTrue;

public class AuthorizerIT {

	private CloseableHttpClient httpClient;

	private Authorizer authorizer;

	private TestPropertiesLoader testPropertiesLoader = TestPropertiesLoader.getInstance();

	private TestCredentials testCredentials;

	@Before
	public void setUp() throws IOException {
		buildHttpClient();

		authorizer = new Authorizer(httpClient);

		loadProperties();
	}

	private void buildHttpClient() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		CookieStore cookieStore = new BasicCookieStore();

		httpClient = httpClientBuilder
				.disableRedirectHandling()
				.setDefaultCookieStore(cookieStore)
				.build();
	}

	private void loadProperties() throws IOException {
		testCredentials = testPropertiesLoader.loadTestCredentials();
	}

	@Test
	public void authorize() throws IOException {
		GarminConnectCredentials credentials = new GarminConnectCredentials();
		credentials.setUsername(testCredentials.getCorrectUsername());
		credentials.setPassword(testCredentials.getCorrectPassword());

		authorizer.authorize(credentials);

		assertTrue(authorizer.isAuthorized());
	}
}