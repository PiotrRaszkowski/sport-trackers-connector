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
package pl.raszkowski.sporttrackersconnector.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TestPropertiesLoader {
	private static final String CREDENTIALS_FILE_PATH = "src/test/resources/garminConnectCredentials.properties";

	private static final String CORRECT_USERNAME_KEY = "correct.username";
	private static final String CORRECT_PASSWORD_KEY = "correct.password";

	private static final TestPropertiesLoader INSTANCE = new TestPropertiesLoader();

	private Properties testProperties;

	private   TestPropertiesLoader() {
		try {
			testProperties = new Properties();
			testProperties.load(new FileReader(new File(CREDENTIALS_FILE_PATH)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static TestPropertiesLoader getInstance() {
		return INSTANCE;
	}

	public TestCredentials loadTestCredentials() {
		TestCredentials testCredentials = new TestCredentials();
		testCredentials.setCorrectUsername(testProperties.getProperty(CORRECT_USERNAME_KEY));
		testCredentials.setCorrectPassword(testProperties.getProperty(CORRECT_PASSWORD_KEY));
		return testCredentials;
	}

}
