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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.raszkowski.sporttrackersconnector.ConnectorException;

public class RESTUriBuilder {

	private static final String CANNOT_BUILD_URI_ERROR_MESSAGE = "Cannot build URI for resourcePath = %s.";

	private static final Logger LOG = LoggerFactory.getLogger(RESTUriBuilder.class);

	public URI build(String resourcePath, Map<String, String> parameters) {
		GetParameters getParameters = new GetParameters();
		getParameters.setParameters(parameters);
		return build(resourcePath, getParameters);
	}

	public URI build(String resourcePath, GetParameters getParameters) {
		try {
			URIBuilder uriBuilder = new URIBuilder(resourcePath);
			if (getParameters.hasParameters()) {
				uriBuilder.setCustomQuery(getParameters.getCustomQuery());
			}
			return uriBuilder.build();
		} catch (URISyntaxException e) {
			LOG.error("Cannot build URI for resourcePath = {}.", resourcePath);
			throw new ConnectorException(String.format(CANNOT_BUILD_URI_ERROR_MESSAGE, resourcePath), e);
		}
	}
}
