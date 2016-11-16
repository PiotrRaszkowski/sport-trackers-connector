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
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.raszkowski.sporttrackersconnector.ConnectorException;
import pl.raszkowski.sporttrackersconnector.helper.HttpResponseConverter;
import pl.raszkowski.sporttrackersconnector.helper.HttpResponseVerifier;

public abstract class RESTExecutor {

	private static final String CANNOT_BUILD_URI_ERROR_MESSAGE = "Cannot build URI for service = %s, resource = %s.";
	private static final String UNABLE_TO_EXECUTE_REQUEST_ERROR_MESSAGE = "Unable to execute request!";
	private static final String WRONG_RESPONSE_STATUS_CODE_ERROR_MESSAGE = "Wrong response status code = %s, expected = %s, for URI = %s.";

	private static final Logger LOG = LoggerFactory.getLogger(RESTExecutor.class);

	private HttpClient httpClient;

	public RESTExecutor(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public String executeGET(String service, String resource) {
		return executeGET(service, resource, Collections.emptyMap());
	}

	public String executeGET(String service, String resource, Map<String, String> parameters) {
		try {
			URI uri = buildURI(service, resource, parameters);

			HttpGet httpGet = new HttpGet(uri);

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

	private URI buildURI(String service, String resource, Map<String, String> parameters) {
		String resourceURI = translateResourceToURI(service, resource);

		try {
			URIBuilder uriBuilder = new URIBuilder(resourceURI);
			parameters.entrySet().forEach(entry -> uriBuilder.setParameter(entry.getKey(), entry.getValue()));
			return uriBuilder.build();
		} catch (URISyntaxException e) {
			LOG.error("Cannot build URI for service = {}, resource = {}.", service, resource);
			throw new ConnectorException(String.format(CANNOT_BUILD_URI_ERROR_MESSAGE, service, resource), e);
		}
	}



	public abstract String translateResourceToURI(String service, String resource);

	public String executePOST() {
		throw new NotImplementedException("Not yet implemented!");
	}
}
