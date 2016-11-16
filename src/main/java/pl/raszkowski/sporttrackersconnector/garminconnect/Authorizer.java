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
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.raszkowski.sporttrackersconnector.configuration.ConnectorsConfiguration;
import pl.raszkowski.sporttrackersconnector.helper.HttpResponseConverter;
import pl.raszkowski.sporttrackersconnector.helper.HttpResponseVerifier;
import pl.raszkowski.sporttrackersconnector.garminconnect.exception.GarminConnectAuthorizationException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

class Authorizer {

	private static final Logger LOG = LoggerFactory.getLogger(Authorizer.class);

	private static final String LOAD_LOGIN_PAGE_ERROR_MESSAGE = "Cannot load the login page, status code: %s.";
	private static final String CANNOT_LOGIN_USER_ERROR_MESSAGE = "Cannot login user, status code: %s.";
	private static final String TICKET_PARAMETER_NOT_FOUND_ERROR_MESSAGE = "The \"ticket\" parameter has not been found. Probably the login phase has failed.";
	private static final String LT_PARAMETER_NOT_FOUND_ERROR_MESSAGE = "The \"lt\" parameter has not been found in the response.";
	private static final String CANNOT_BUILD_AUTHORIZATION_URI_ERROR_MESSAGE = "Cannot build base authorization URI.";
	private static final String CANNOT_AUTHORIZE_WITH_TICKET_ERROR_MESSAGE = "Cannot authorize/login with ticket = %s, status was = %s, expected = %s.";
	private static final String EMPTY_USERNAME_ERROR_MESSAGE = "Username is empty. Please provide valid credentials!";
	private static final String EMPTY_PASSWORD_ERROR_MESSAGE = "Password is empty. Please provide valid credentials!";
	private static final String CANNOT_RETRIEVE_ACCOUNT_SERVICE_ERROR_MESSAGE = "Unable to retrieve logged username, probably the login process has failed, status code = %s, expected = %s.";
	private static final String USERNAME_IS_NOT_PRESENT_ERROR_MESSAGE = "Authorization validation failed. Username does not match, username = %s, expected = %s.";
	private static final String USERNAME_NOT_FOUND_ERROR_MESSAGE = "Authorization validation failed. No username has been found, expected was = %s.";

	private static final String LOCATION_HEADER_NAME = "Location";

	private static final String USERNAME_PARAMETER = "username";
	private static final String PASSWORD_PARAMETER = "password";
	private static final String EVENT_ID_PARAMETER = "_eventId";
	private static final String EMBED_PARAMETER = "embed";
	private static final String DISPLAY_NAME_REQUIRED_PARAMETER = "displayNameRequired";
	private static final String LT_PARAMETER = "lt";
	private static final String SERVICE_PARAMETER = "service";
	private static final String TICKET_PARAMETER = "ticket";
	private static final String CLIENT_ID_PARAMETER = "clientId";
	private static final String CONSUME_SERVICE_TICKET_PARAMETER = "consumeServiceTicket";

	private static final String EVENT_ID_PARAMETER_VALUE = "submit";
	private static final String EMBED_PARAMETER_VALUE = "true";
	private static final String CLIENT_ID_PARAMETER_VALUE = "GarminConnect";
	private static final String CONSUME_SERVICE_TICKET_PARAMETER_VALUE = "false";

	private static final String DISPLAY_NAME_REQUIRED_PARAMETER_VALUE = "false";
	private static final String LT_REGEX = "name=\"lt\"\\s+value=\"([^\"]+)\"";

	private static final String TICKET_REGEX = "ticket=([^']+)'";
	private static final String JSON_USERNAME_KEY = "username";
	private static final String JSON_ACCOUNT_KEY = "account";

	private static final String ACCOUNT_RESOURCE = "account";
	public static final String UNABLE_TO_EXECUTE_REQUEST_ERROR_MESSAGE = "Unable to execute request.";

	private ConnectorsConfiguration connectorsConfiguration = ConnectorsConfiguration.getInstance();

	private GarminConnectRESTResolver garminConnectRESTResolver = new GarminConnectRESTResolver();

	private HttpClient httpClient;

	private URI authorizationUri;

	private String lastPageContent;

	private boolean authorized;

	Authorizer(HttpClient httpClient) {
		this.httpClient = httpClient;
		this.authorized = false;

		buildBaseAuthorizationUri();
	}

	private void buildBaseAuthorizationUri() {
		try {
			this.authorizationUri = new URIBuilder(connectorsConfiguration.getGarminConnectSSOLoginURI())
					.setParameter(SERVICE_PARAMETER, connectorsConfiguration.getGarminConnectLoginServiceURI())
					.setParameter(CLIENT_ID_PARAMETER, CLIENT_ID_PARAMETER_VALUE)
					.setParameter(CONSUME_SERVICE_TICKET_PARAMETER, CONSUME_SERVICE_TICKET_PARAMETER_VALUE)
					.build();
		} catch (URISyntaxException e) {
			LOG.error("Cannot build base authorization URI.", e);
			throw new GarminConnectAuthorizationException(CANNOT_BUILD_AUTHORIZATION_URI_ERROR_MESSAGE, e);
		}
	}

	void authorize(GarminConnectCredentials credentials) {
		validateCredentials(credentials);

		goToLoginPage();

		performLogin(credentials);

		validateAuthorization();

		validateLoggedUser(credentials);
	}

	private void validateCredentials(GarminConnectCredentials credentials) {
		if (StringUtils.isEmpty(credentials.getUsername())) {
			LOG.debug("Credentials = {}.", credentials);
			throw new GarminConnectAuthorizationException(EMPTY_USERNAME_ERROR_MESSAGE);
		}

		if (StringUtils.isEmpty(credentials.getPassword())) {
			LOG.debug("Credentials = {}.", credentials);
			throw new GarminConnectAuthorizationException(EMPTY_PASSWORD_ERROR_MESSAGE);
		}
	}

	private void goToLoginPage() {
		try {
			HttpGet httpGet = new HttpGet(authorizationUri);

			HttpResponse response = httpClient.execute(httpGet);

			if (HttpResponseVerifier.isNotOk(response)) {
				LOG.error("Cannot load the login page, status code = {}, expected = {}.", response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
				LOG.debug(lastPageContent);
				throw new GarminConnectAuthorizationException(String.format(LOAD_LOGIN_PAGE_ERROR_MESSAGE, response.getStatusLine().getStatusCode()));
			}

			HttpEntity entity = response.getEntity();

			lastPageContent = EntityUtils.toString(entity);
		} catch (IOException e) {
			LOG.error("Unable to execute request loading login page, uri = {}.", authorizationUri,  e);
			throw new GarminConnectAuthorizationException(UNABLE_TO_EXECUTE_REQUEST_ERROR_MESSAGE, e);
		}
	}

	private void performLogin(GarminConnectCredentials credentials)  {
		try {
			List<NameValuePair> urlParameters = prepareLoginParameters(credentials);

			HttpPost httpPost = new HttpPost(authorizationUri);
			httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = httpClient.execute(httpPost);

			if (HttpResponseVerifier.isNotOk(response)) {
				LOG.error("Cannot login user, status code = {}, expected = {}.", response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
				LOG.debug("Credentials = {}.", credentials);
				throw new GarminConnectAuthorizationException(String.format(CANNOT_LOGIN_USER_ERROR_MESSAGE, response.getStatusLine().getStatusCode()));
			}

			HttpEntity entity = response.getEntity();

			lastPageContent = EntityUtils.toString(entity);
		} catch (IOException e) {
			LOG.error("Unable to execute request to login user, uri = {}.", authorizationUri);
			throw new GarminConnectAuthorizationException(UNABLE_TO_EXECUTE_REQUEST_ERROR_MESSAGE, e);
		}
	}

	private List<NameValuePair> prepareLoginParameters(GarminConnectCredentials credentials) {
		Optional<String> ltParameterValue = getLtParameterValue(lastPageContent);

		if (!ltParameterValue.isPresent()) {
			LOG.error("The \"lt\" parameter has not been found in the response.");
			LOG.debug("Credentials = {}.", credentials);
			LOG.debug(lastPageContent);
			throw new GarminConnectAuthorizationException(LT_PARAMETER_NOT_FOUND_ERROR_MESSAGE);
		}

		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair(USERNAME_PARAMETER, credentials.getUsername()));
		urlParameters.add(new BasicNameValuePair(PASSWORD_PARAMETER, credentials.getPassword()));
		urlParameters.add(new BasicNameValuePair(EVENT_ID_PARAMETER, EVENT_ID_PARAMETER_VALUE));
		urlParameters.add(new BasicNameValuePair(EMBED_PARAMETER, EMBED_PARAMETER_VALUE));
		urlParameters.add(new BasicNameValuePair(DISPLAY_NAME_REQUIRED_PARAMETER, DISPLAY_NAME_REQUIRED_PARAMETER_VALUE));
		urlParameters.add(new BasicNameValuePair(LT_PARAMETER, ltParameterValue.get()));
		return urlParameters;
	}

	private Optional<String> getLtParameterValue(String responseContent) {
		Pattern ltPattern = Pattern.compile(LT_REGEX);
		Matcher ltMatcher = ltPattern.matcher(responseContent);

		if (ltMatcher.find()) {
			return Optional.of(ltMatcher.group(1));
		}

		return Optional.empty();
	}

	private void validateAuthorization() {
		Optional<String> ticket = getTicketValue(lastPageContent);

		if (!ticket.isPresent()) {
			LOG.error("The \"ticket\" parameter has not been found. Probably the login phase has failed.");
			LOG.debug(lastPageContent);
			throw new GarminConnectAuthorizationException(TICKET_PARAMETER_NOT_FOUND_ERROR_MESSAGE);
		}

		addTicketToAuthorizationUri(ticket.get());

		HttpResponse response = authorizeWithTicket(ticket.get());

		followRedirectsToGetSessionCookie(response);
	}

	private Optional<String> getTicketValue(String responseContent) {
		Pattern ticketPattern = Pattern.compile(TICKET_REGEX);
		Matcher ticketMatcher = ticketPattern.matcher(responseContent);

		if (ticketMatcher.find()) {
			return Optional.of(ticketMatcher.group(1));
		}

		return Optional.empty();
	}

	private HttpResponse authorizeWithTicket(String ticket)  {
		try {
			HttpPost httpPost = new HttpPost(authorizationUri);

			HttpResponse response = httpClient.execute(httpPost);

			if (HttpResponseVerifier.isNotMovedTemporarily(response)) {
				LOG.error("Cannot authorize/login with ticket = {}, status code = {}, expected = {}.", ticket, response.getStatusLine().getStatusCode(), HttpStatus.SC_MOVED_TEMPORARILY);
				LOG.debug(lastPageContent);
				throw new GarminConnectAuthorizationException(String.format(CANNOT_AUTHORIZE_WITH_TICKET_ERROR_MESSAGE, ticket, response.getStatusLine().getStatusCode(), HttpStatus.SC_MOVED_TEMPORARILY));
			}

			return response;
		} catch (IOException e) {
			LOG.error("Unable to execute request to authorize the user with a ticket, uri = {}.", authorizationUri, e);
			throw new GarminConnectAuthorizationException(UNABLE_TO_EXECUTE_REQUEST_ERROR_MESSAGE, e);
		}
	}

	private void addTicketToAuthorizationUri(String ticket) {
		try {
			authorizationUri = new URIBuilder(authorizationUri)
					.setParameter(TICKET_PARAMETER, ticket)
					.build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private void followRedirectsToGetSessionCookie(HttpResponse response) {
		try {
			do {
				Header redirectLocation = response.getFirstHeader(LOCATION_HEADER_NAME);

				HttpGet httpGet = new HttpGet(redirectLocation.getValue());

				response = httpClient.execute(httpGet);
			} while (HttpResponseVerifier.isMovedTemporarily(response));
		} catch (IOException e) {
			LOG.error("Unable to execute request to retrieve session cookie.", e);
			throw new GarminConnectAuthorizationException(UNABLE_TO_EXECUTE_REQUEST_ERROR_MESSAGE, e);
		}
	}

	private void validateLoggedUser(GarminConnectCredentials credentials) {
		try {
			HttpGet httpGet = new HttpGet(garminConnectRESTResolver.getJsonUserService(ACCOUNT_RESOURCE));

			HttpResponse response = httpClient.execute(httpGet);

			if (HttpResponseVerifier.isNotOk(response)) {
				LOG.debug("Credentials: {}.", credentials);
				throw new GarminConnectAuthorizationException(String.format(CANNOT_RETRIEVE_ACCOUNT_SERVICE_ERROR_MESSAGE, response.getStatusLine().getStatusCode(), HttpStatus.SC_OK));
			}

			checkIfUsernameMatch(credentials, response);
		} catch (IOException e) {
			LOG.error("Unable to execute request to validate logged user, uri = {}.", garminConnectRESTResolver.getJsonUserService(ACCOUNT_RESOURCE));
			throw new GarminConnectAuthorizationException(UNABLE_TO_EXECUTE_REQUEST_ERROR_MESSAGE, e);
		}
	}

	private void checkIfUsernameMatch(GarminConnectCredentials credentials, HttpResponse response) {
		String responseContent = HttpResponseConverter.getAsString(response);

		Optional<String> username = getUsername(responseContent);

		if (username.isPresent()) {
			if (username.get().equals(credentials.getUsername())) {
				authorized = true;
				return;
			} else {
				throw new GarminConnectAuthorizationException(String.format(USERNAME_IS_NOT_PRESENT_ERROR_MESSAGE, username.get(), credentials.getUsername()));
			}
		}

		throw new GarminConnectAuthorizationException(String.format(USERNAME_NOT_FOUND_ERROR_MESSAGE, credentials.getUsername()));
	}

	private Optional<String> getUsername(String responseContent) {
		JsonReader jsonReader = new JsonReader(new StringReader(responseContent));
		jsonReader.setLenient(true);

		JsonParser jsonParser = new JsonParser();

		JsonElement json = jsonParser.parse(jsonReader);

		LOG.debug("Json account: {}.", json);

		if (json.isJsonObject()) {
			JsonObject jsonRoot = json.getAsJsonObject();

			if (jsonRoot.getAsJsonObject().has(JSON_ACCOUNT_KEY)) {
				JsonObject jsonAccount = jsonRoot.get(JSON_ACCOUNT_KEY).getAsJsonObject();

				if (jsonAccount.has(JSON_USERNAME_KEY)) {
					return Optional.ofNullable(jsonAccount.get(JSON_USERNAME_KEY).getAsString());
				}
			}
		}

		return Optional.empty();
	}

	boolean isAuthorized() {
		return authorized;
	}

	boolean isNotAuthorized() {
		return !authorized;
	}
}
