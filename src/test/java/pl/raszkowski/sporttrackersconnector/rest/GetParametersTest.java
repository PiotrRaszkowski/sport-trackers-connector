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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GetParametersTest {

	private static final String PARAMETER_NAME = "parameter";
	private static final String PARAMETER_VALUE = "value";
	private static final String PARAMETER_OPERATOR = "operator";
	private static final String END_SIGN = "&";
	private static final String EQUAL_SIGN = "=";

	@InjectMocks
	private GetParameters getParameters;

	@Test
	public void addParameterThenAdded() {
		getParameters.addParameter(PARAMETER_NAME, PARAMETER_VALUE);

		assertEquals(PARAMETER_VALUE, getParameters.getParameters().get(PARAMETER_NAME));
	}

	@Test
	public void addCustomParameterThenAdded() {
		getParameters.addCustomParameter(PARAMETER_NAME, PARAMETER_OPERATOR, PARAMETER_VALUE);

		assertEquals(PARAMETER_NAME + PARAMETER_OPERATOR + PARAMETER_VALUE, getParameters.getCustomParameters().get(0).get());
	}

	@Test
	public void addCustomParameterAsObjectThenAdded() {
		GetParameters.CustomParameter customParameter = new GetParameters.CustomParameter();
		customParameter.setName(PARAMETER_NAME);
		customParameter.setOperator(PARAMETER_OPERATOR);
		customParameter.setValue(PARAMETER_VALUE);

		getParameters.addCustomParameter(customParameter);

		assertEquals(PARAMETER_NAME + PARAMETER_OPERATOR + PARAMETER_VALUE, getParameters.getCustomParameters().get(0).get());
	}

	@Test
	public void hasParametersWhenNoParametersThenFalse() {
		boolean result = getParameters.hasParameters();

		assertFalse(result);
	}

	@Test
	public void hasParametersWhenParametersThenTrue() {
		getParameters.addParameter(PARAMETER_NAME, PARAMETER_VALUE);

		boolean result = getParameters.hasParameters();

		assertTrue(result);
	}

	@Test
	public void hasParametersWhenCustomParametersThenTrue() {
		getParameters.addCustomParameter(PARAMETER_NAME, PARAMETER_OPERATOR, PARAMETER_VALUE);

		boolean result = getParameters.hasParameters();

		assertTrue(result);
	}

	@Test
	public void hasParametersWhenParametersAndCustomParametersThenTrue() {
		getParameters.addParameter(PARAMETER_NAME, PARAMETER_VALUE);
		getParameters.addCustomParameter(PARAMETER_NAME, PARAMETER_OPERATOR, PARAMETER_VALUE);

		boolean result = getParameters.hasParameters();

		assertTrue(result);
	}

	@Test
	public void getCustomQueryWhenNoParametersThenEmpty() {
		String customQuery = getParameters.getCustomQuery();

		assertEquals("", customQuery);
	}

	@Test
	public void getCustomQueryWhenParametersThenNotEmpty() {
		getParameters.addParameter(PARAMETER_NAME, PARAMETER_VALUE);

		String customQuery = getParameters.getCustomQuery();

		assertEquals(PARAMETER_NAME + EQUAL_SIGN + PARAMETER_VALUE, customQuery);
	}

	@Test
	public void getCustomQueryWhenCustomParametersThenNotEmpty() {
		getParameters.addCustomParameter(PARAMETER_NAME, PARAMETER_OPERATOR, PARAMETER_VALUE);

		String customQuery = getParameters.getCustomQuery();

		assertEquals(PARAMETER_NAME + PARAMETER_OPERATOR + PARAMETER_VALUE, customQuery);
	}

	@Test
	public void getCustomQueryWhenParametersAndCustomParametersThenNotEmpty() {
		getParameters.addParameter(PARAMETER_NAME, PARAMETER_VALUE);
		getParameters.addCustomParameter(PARAMETER_NAME, PARAMETER_OPERATOR, PARAMETER_VALUE);

		String customQuery = getParameters.getCustomQuery();

		assertEquals(PARAMETER_NAME + EQUAL_SIGN + PARAMETER_VALUE + END_SIGN + PARAMETER_NAME + PARAMETER_OPERATOR + PARAMETER_VALUE, customQuery);
	}
}