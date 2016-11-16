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
package pl.raszkowski.sporttrackersconnector.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorsConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectorsConfiguration.class);

	private static final ConnectorsConfiguration INSTANCE = new ConnectorsConfiguration();

	private Properties properties;

	ConnectorsConfiguration() {
		try {
			properties = new Properties();
			InputStream inputStream = ConnectorsConfiguration.class.getClassLoader().getResourceAsStream("configuration.properties");
			properties.load(inputStream);
		} catch (IOException e) {
			LOG.error("Unable to load configuration from file = {}.", "configuration.properties", e);
			throw new Error(String.format("Unable to load configuration from file = %s.", "configuration.properties"));
		}

	}

	public static ConnectorsConfiguration getInstance() {
		return INSTANCE;
	}

	public String getGarminConnectSSOLoginURI() {
		return properties.getProperty("garminconnect.uri.ssologin", "https://sso.garmin.com/sso/login");
	}

	public String getGarminConnectLoginServiceURI() {
		return properties.getProperty("garminconnect.uri.loginService", "https://connect.garmin.com/post-auth/login");
	}

	public String getGarminConnectRESTPrefixURI() {
		return properties.getProperty("garminconnect.uri.restPrefix", "https://connect.garmin.com/proxy/");
	}

	public String getGarminConnectRESTUserService() {
		return properties.getProperty("garminconnect.rest.userService", "user-service-1.0");
	}


	public String getGarminConnectRESTActivitySearchService() {
		return properties.getProperty("garminconnect.rest.activitySearchService", "activity-search-service-1.2");
	}
}
