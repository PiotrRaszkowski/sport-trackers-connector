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

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.raszkowski.sporttrackersconnector.ConnectorException;
import pl.raszkowski.sporttrackersconnector.helper.HttpResponseConverter;
import pl.raszkowski.sporttrackersconnector.helper.HttpResponseVerifier;

public abstract class RESTExecutor {

	private static final String UNABLE_TO_EXECUTE_REQUEST_ERROR_MESSAGE = "Unable to execute request!";
	private static final String WRONG_RESPONSE_STATUS_CODE_ERROR_MESSAGE = "Wrong response status code = %s, expected = %s, for URI = %s.";

	private static final Logger LOG = LoggerFactory.getLogger(RESTExecutor.class);

	private HttpClient httpClient;

	private RESTUriBuilder restUriBuilder = new RESTUriBuilder();

	public RESTExecutor(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public String executeGET(String service, String resource) {
		return executeGET(service, resource, Collections.emptyMap());
	}

	public String executeGET(String service, String resource, Map<String, String> parameters) {
		GetParameters getParameters = new GetParameters();
		getParameters.setParameters(parameters);

		return executeGET(service, resource, getParameters);
	}

	public String executeGET(String service, String resource, GetParameters getParameters) {
		try {
			URI uri = buildURI(service, resource, getParameters);

			HttpGet httpGet = new HttpGet(uri);

			LOG.debug("Executing GET request = {}.", httpGet.getURI());

			HttpResponse response = httpClient.execute(httpGet);

			if (HttpResponseVerifier.isNotOk(response)) {
				LOG.error("Wrong response status code = {}, expected = {}, for URI = {}.", response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, uri);
				throw new ConnectorException(String.format(WRONG_RESPONSE_STATUS_CODE_ERROR_MESSAGE, response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, uri));
			}

			return HttpResponseConverter.getAsString(response);
		} catch (IOException e) {
			LOG.error("Unable to execute request for service = {}, resurce = {}.", service, resource, e);
			throw new ConnectorException(UNABLE_TO_EXECUTE_REQUEST_ERROR_MESSAGE, e);
		}
	}

	private URI buildURI(String service, String resource, GetParameters getParameters) {
		String resourceURI = translateResourceToURI(service, resource);

		return restUriBuilder.build(resourceURI, getParameters);
	}

	public abstract String translateResourceToURI(String service, String resource);
}
