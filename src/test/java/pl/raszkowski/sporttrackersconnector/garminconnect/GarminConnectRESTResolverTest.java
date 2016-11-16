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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.raszkowski.sporttrackersconnector.configuration.ConnectorsConfiguration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class GarminConnectRESTResolverTest {
	private static final String PREFIX = "PREFIX";
	private static final String SERVICE = "SERVICE";
	private static final String RESOURCE = "RESOURCE";
	private static final String USER_SERVICE = "USER_SERVICE";

	@Mock
	private ConnectorsConfiguration connectorsConfiguration;

	@InjectMocks
	private GarminConnectRESTResolver garminConnectRESTResolver;

	@Before
	public void setUp() {
		doReturn(PREFIX).when(connectorsConfiguration).getGarminConnectRESTPrefixURI();
		doReturn(USER_SERVICE).when(connectorsConfiguration).getGarminConnectRESTUserService();
	}

	@Test
	public void getJsonUserService() {
		String result = garminConnectRESTResolver.getJsonUserService(RESOURCE);

		assertEquals(PREFIX + USER_SERVICE + "/json/" + RESOURCE, result);
	}

	@Test
	public void getJsonService() {
		String result = garminConnectRESTResolver.getJsonService(SERVICE, RESOURCE);

		assertEquals(PREFIX + SERVICE + "/json/" + RESOURCE, result);
	}
}