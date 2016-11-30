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
package pl.raszkowski.sporttrackersconnector.rest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import pl.raszkowski.sporttrackersconnector.ConnectorException;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RESTUriBuilderTest {

	@InjectMocks
	private RESTUriBuilder restUriBuilder;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void buildGivenNoParameters() {
		URI result = restUriBuilder.build("path/to/resource", Collections.emptyMap());

		assertEquals("path/to/resource", result.toString());
	}

	@Test
	public void buildWhenNotParsablePathToResource() {
		expectedException.expect(ConnectorException.class);
		expectedException.expectMessage("Cannot build URI for resourcePath = -----&^ulalala");

		restUriBuilder.build("-----&^ulalala", new GetParameters());
	}

	@Test
	public void buildGivenParameters() {
		Map<String, String> parameters = new TreeMap<>();
		parameters.put("parameter1", "value1");
		parameters.put("parameter2", "value2");
		parameters.put("parameter3", "value3");

		URI result = restUriBuilder.build("path/to/resource", parameters);

		assertEquals("path/to/resource?parameter1=value1&parameter2=value2&parameter3=value3", result.toString());
	}

	@Test
	public void buildGivenCustomParameters() throws UnsupportedEncodingException {
		GetParameters getParameters = new GetParameters();
		getParameters.addCustomParameter("parameter1", ">", "value1");
		getParameters.addCustomParameter("parameter2", "=", "value2");
		getParameters.addCustomParameter("parameter3", "<=", "value3");

		URI result = restUriBuilder.build("path/to/resource", getParameters);

		assertEquals("path/to/resource?parameter1%3Evalue1&parameter2=value2&parameter3%3C=value3", result.toString());
	}

	@Test
	public void buildGivenCustomParametersAndParameters() {
		GetParameters getParameters = new GetParameters();
		getParameters.addCustomParameter("parameter1", ">", "value1");
		getParameters.addCustomParameter("parameter2", "=", "value2");
		getParameters.addCustomParameter("parameter3", "<=", "value3");
		getParameters.addParameter("parameter4", "value4");
		getParameters.addParameter("parameter5", "value5");

		URI result = restUriBuilder.build("path/to/resource", getParameters);

		assertEquals("path/to/resource?parameter4=value4&parameter5=value5&parameter1%3Evalue1&parameter2=value2&parameter3%3C=value3", result.toString());
	}
}