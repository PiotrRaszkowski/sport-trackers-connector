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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.raszkowski.sporttrackersconnector.ConnectorException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ResponseJsonParser {

	private static final String CANNOT_PARSE_JSON_ERROR_MESSAGE = "Cannot parse given Json!.";

	private static final Logger LOG = LoggerFactory.getLogger(ResponseJsonParser.class);

	public JsonObject parseAsJsonObject(String result) {
		JsonElement jsonElement = parseAsJson(result);

		if (jsonElement.isJsonObject()) {
			return jsonElement.getAsJsonObject();
		}

		LOG.error("Cannot parse given Json as JsonObject. Given json = {}.", result);

		return new JsonObject();
	}

	public JsonElement parseAsJson(String result) {
		try {
			JsonParser jsonParser = new JsonParser();
			return jsonParser.parse(result);
		} catch (JsonSyntaxException e) {
			LOG.error("Cannot parse given Json. Given json = {}.", result, e);
			throw new ConnectorException(CANNOT_PARSE_JSON_ERROR_MESSAGE, e);
		}
	}
}
