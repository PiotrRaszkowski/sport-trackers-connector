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

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.raszkowski.sporttrackersconnector.ConnectorException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RESTExecutorTest {

	private static final String RESOURCE = "resource";
	private static final String SERVICE = "service";
	private static final String RESPONSE_CONTENT = "RESPONSE CONTENT";

	private HttpClient httpClient;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpResponse httpResponse;

	@InjectMocks
	private RESTExecutor restExecutor;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() {
		httpClient = mock(HttpClient.class, RETURNS_DEEP_STUBS);

		restExecutor = new RESTExecutor(httpClient) {
			@Override
			public String translateResourceToURI(String service, String resource) {
				return service + "/" + resource;
			}
		};

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void executeGETWhenCannotExecuteRequest() throws IOException {
		doThrow(IOException.class).when(httpClient).execute(any(HttpGet.class));

		expectedException.expect(ConnectorException.class);
		expectedException.expectMessage("Unable to execute request!");

		restExecutor.executeGET(SERVICE, RESOURCE);
	}

	@Test
	public void executeGETWhenResponseNotOK() throws IOException {
		doReturn(httpResponse).when(httpClient).execute(any(HttpGet.class));
		when(httpResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);

		expectedException.expect(ConnectorException.class);
		expectedException.expectMessage("Wrong response status code = 502, expected = 200, for URI = " + SERVICE + "/" + RESOURCE);

		restExecutor.executeGET(SERVICE, RESOURCE);
	}

	@Test
	public void executeGETWhenCorrectRequest() throws IOException {
		doReturn(httpResponse).when(httpClient).execute(any(HttpGet.class));
		when(httpResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(httpResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream(RESPONSE_CONTENT, "utf-8"));
		when(httpResponse.getEntity().getContentType()).thenReturn(null);

		String result = restExecutor.executeGET(SERVICE, RESOURCE);

		assertEquals(RESPONSE_CONTENT, result);
	}
}