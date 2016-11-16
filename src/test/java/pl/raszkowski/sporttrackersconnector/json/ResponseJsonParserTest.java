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
package pl.raszkowski.sporttrackersconnector.json;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pl.raszkowski.sporttrackersconnector.ConnectorException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ResponseJsonParserTest {

	private ResponseJsonParser responseJsonParser = new ResponseJsonParser();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void parseAsJsonGivenJson() {
		JsonElement result = responseJsonParser.parseAsJson("{ parameter : \"value\" }");

		assertNotNull(result);
	}

	@Test
	public void parseAsJsonGivenNotJson() {
		expectedException.expect(ConnectorException.class);
		expectedException.expectMessage("Cannot parse given Json!.");

		responseJsonParser.parseAsJson("not json, no");
	}

	@Test
	public void parseAsJsonObjectGivenJsonObject() {
		JsonObject result = responseJsonParser.parseAsJsonObject("{ parameter : \"value\" }");

		assertNotNull(result);
	}

	@Test
	public void parseAsJsonObjectGivenJsonArray() {
		JsonObject result = responseJsonParser.parseAsJsonObject("[ 100, 500, 300, 200, 400 ]");

		assertNotNull(result);
	}
}