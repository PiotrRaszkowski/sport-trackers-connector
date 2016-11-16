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

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import pl.raszkowski.sporttrackersconnector.Connector;
import pl.raszkowski.sporttrackersconnector.configuration.ConnectorsConfiguration;
import pl.raszkowski.sporttrackersconnector.garminconnect.exception.GarminConnectAuthorizationException;
import pl.raszkowski.sporttrackersconnector.rest.RESTExecutor;

/**
 *
 * http://sergeykrasnov.ru/subsites/dev/garmin-connect-statisics/
 *
 * https://github.com/dawguk/php-garmin-connect/blob/master/src/dawguk/GarminConnect.php
 *
 * https://github.com/cpfair/tapiriik/blob/master/tapiriik/services/GarminConnect/garminconnect.py
 *
 * @author Piotr Raszkowski
 */
public class GarminConnectConnector implements Connector {

	private static final String AUTHORIZATION_FAILED_ERROR_MESSAGE = "Authorization failed! Please check logs, this is unexpected situation!";

	private HttpClient httpClient;

	private Authorizer authorizer;

	private ConnectorsConfiguration connectorsConfiguration = ConnectorsConfiguration.getInstance();

	public GarminConnectConnector() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		httpClient = httpClientBuilder
				.disableRedirectHandling()
				.build();

		authorizer = new Authorizer(httpClient);
	}

	public void authorize(GarminConnectCredentials credentials) {
		authorizer.authorize(credentials);

		if (authorizer.isNotAuthorized()) {
			throw new GarminConnectAuthorizationException(AUTHORIZATION_FAILED_ERROR_MESSAGE);
		}
	}

	@Override
	public RESTExecutor getRESTExecutor() {
		return new GarminRESTExecutor(httpClient);
	}

	@Override
	public GarminAPIHandler getAPIHandler() {
		RESTExecutor restExecutor = getRESTExecutor();
		return new GarminAPIHandler(restExecutor);
	}
}
