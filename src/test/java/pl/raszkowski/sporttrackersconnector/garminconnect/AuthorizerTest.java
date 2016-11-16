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

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.raszkowski.sporttrackersconnector.configuration.ConnectorsConfiguration;
import pl.raszkowski.sporttrackersconnector.garminconnect.exception.GarminConnectAuthorizationException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizerTest {

	private static final String LOGIN_PAGE_RESPONSE_CONTENT = "<input type=\"hidden\" name=\"lt\" value=\"e1s1\" />";
	private static final String PERFORM_LOGIN_RESPONSE_CONTENT = "ticket=ABC12345DEF'";
	private static final String EMPTY_RESPONSE_CONTENT = "EMPTY RESPONSE";

	private static final String USERNAME_VALUE = "username_value";
	private static final String PASSWORD_VALUE = "password_value";

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpResponse loginPageResponse;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpResponse performLoginResponse;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpResponse authorizeWithTicketResponse;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpResponse followRedirectsResponse;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpResponse validateLoggedUserResponse;

	@Mock
	private GarminConnectCredentials garminConnectCredentials;

	@Mock
	private ConnectorsConfiguration connectorsConfiguration;

	@Mock
	private GarminConnectRESTResolver garminConnectRESTResolver;

	@InjectMocks
	private Authorizer authorizer;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws IOException {
		HttpClient httpClient = mock(HttpClient.class);

		authorizer = new Authorizer(httpClient);

		MockitoAnnotations.initMocks(this);

		when(httpClient.execute(any(HttpUriRequest.class)))
				.thenReturn(loginPageResponse)
				.thenReturn(performLoginResponse)
				.thenReturn(authorizeWithTicketResponse)
				.thenReturn(followRedirectsResponse)
				.thenReturn(validateLoggedUserResponse)
				.thenThrow(new RuntimeException("Wrong response!"));

		when(loginPageResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(loginPageResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream(LOGIN_PAGE_RESPONSE_CONTENT, "utf-8"));
		when(loginPageResponse.getEntity().getContentType()).thenReturn(null);

		when(performLoginResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(performLoginResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream(PERFORM_LOGIN_RESPONSE_CONTENT, "utf-8"));
		when(performLoginResponse.getEntity().getContentType()).thenReturn(null);

		when(authorizeWithTicketResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_MOVED_TEMPORARILY);
		when(authorizeWithTicketResponse.getFirstHeader("Location").getValue()).thenReturn("https://connect.garmin.com/modern/");

		when(followRedirectsResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_OK);

		when(validateLoggedUserResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(validateLoggedUserResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream("{account : {username : \"username_value\"}}", "utf-8"));
		when(validateLoggedUserResponse.getEntity().getContentType()).thenReturn(null);

		when(garminConnectCredentials.getUsername()).thenReturn(USERNAME_VALUE);
		when(garminConnectCredentials.getPassword()).thenReturn(PASSWORD_VALUE);

		when(garminConnectRESTResolver.getJsonUserService("account")).thenReturn("account_service");
	}

	@Test
	public void authorizeWhenNullUsername() throws IOException {
		when(garminConnectCredentials.getUsername()).thenReturn(null);

		expectedException.expectMessage("Username is empty. Please provide valid credentials!");
		expectedException.expect(GarminConnectAuthorizationException.class);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenEmptyUsername() throws IOException {
		when(garminConnectCredentials.getUsername()).thenReturn("");

		expectedException.expectMessage("Username is empty. Please provide valid credentials!");
		expectedException.expect(GarminConnectAuthorizationException.class);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenNullPassword() throws IOException {
		when(garminConnectCredentials.getPassword()).thenReturn(null);

		expectedException.expectMessage("Password is empty. Please provide valid credentials!");
		expectedException.expect(GarminConnectAuthorizationException.class);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenEmptyPassword() throws IOException {
		when(garminConnectCredentials.getPassword()).thenReturn("");

		expectedException.expectMessage("Password is empty. Please provide valid credentials!");
		expectedException.expect(GarminConnectAuthorizationException.class);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenLoginPageResponseNotOk() throws IOException {
		when(loginPageResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);

		expectedException.expectMessage("Cannot load the login page, status code: " + HttpStatus.SC_BAD_GATEWAY);
		expectedException.expect(GarminConnectAuthorizationException.class);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenNoLTParameterFound() throws IOException {
		when(loginPageResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream(EMPTY_RESPONSE_CONTENT, "utf-8"));
		when(loginPageResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_OK);

		expectedException.expectMessage("The \"lt\" parameter has not been found in the response.");
		expectedException.expect(GarminConnectAuthorizationException.class);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenPerformLoginResponseNotOk() throws IOException {
		when(performLoginResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);

		expectedException.expectMessage("Cannot login user, status code: " + HttpStatus.SC_BAD_GATEWAY);
		expectedException.expect(GarminConnectAuthorizationException.class);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenNoTicketFound() throws IOException {
		when(performLoginResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream(EMPTY_RESPONSE_CONTENT, "utf-8"));

		expectedException.expect(GarminConnectAuthorizationException.class);
		expectedException.expectMessage("The \"ticket\" parameter has not been found. Probably the login phase has failed.");

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenAuthorizeWithTicketResponseNotMovedTemporarily() throws IOException {
		when(authorizeWithTicketResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);

		expectedException.expect(GarminConnectAuthorizationException.class);
		expectedException.expectMessage("Cannot authorize/login with ticket = ABC12345DEF, status was = 502, expected = 302.");

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenValidateLoggedUserResponseNotOk() throws IOException {
		when(validateLoggedUserResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);

		expectedException.expect(GarminConnectAuthorizationException.class);
		expectedException.expectMessage("Unable to retrieve logged username, probably the login process has failed, status code = 502, expected = 200.");

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenValidateLoggedUserAndEmptyResponse() throws IOException {
		when(validateLoggedUserResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream(EMPTY_RESPONSE_CONTENT, "utf-8"));

		expectedException.expect(GarminConnectAuthorizationException.class);
		expectedException.expectMessage("Authorization validation failed. No username has been found, expected was = " + USERNAME_VALUE);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenValidateLoggedUserAndNoAccount() throws IOException {
		when(validateLoggedUserResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream("{data : {}}", "utf-8"));

		expectedException.expect(GarminConnectAuthorizationException.class);
		expectedException.expectMessage("Authorization validation failed. No username has been found, expected was = " + USERNAME_VALUE);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenValidateLoggedUserAndNoUsername() throws IOException {
		when(validateLoggedUserResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream("{account : {}}", "utf-8"));

		expectedException.expect(GarminConnectAuthorizationException.class);
		expectedException.expectMessage("Authorization validation failed. No username has been found, expected was = " + USERNAME_VALUE);

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenValidateLoggedUserAndUsernameNotMatch() throws IOException {
		when(validateLoggedUserResponse.getEntity().getContent()).thenReturn(IOUtils.toInputStream("{account : {username : \"some_username_value\"}}", "utf-8"));

		expectedException.expect(GarminConnectAuthorizationException.class);
		expectedException.expectMessage("Authorization validation failed. Username does not match, username = some_username_value, expected = username_value.");

		authorizer.authorize(garminConnectCredentials);
	}

	@Test
	public void authorizeWhenProcessSucceed() throws IOException {
		authorizer.authorize(garminConnectCredentials);

		assertTrue(authorizer.isAuthorized());
	}
}