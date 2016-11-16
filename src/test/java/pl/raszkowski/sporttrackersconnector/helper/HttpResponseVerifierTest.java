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
package pl.raszkowski.sporttrackersconnector.helper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.raszkowski.sporttrackersconnector.helper.HttpResponseVerifier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class HttpResponseVerifierTest {

	@Mock
	private HttpResponse httpResponse;

	@Mock
	private StatusLine statusLine;

	@Before
	public void setUp() {
		doReturn(statusLine).when(httpResponse).getStatusLine();
	}

	@Test
	public void isOkWhenOkThenTrue() {
		doReturn(HttpStatus.SC_OK).when(statusLine).getStatusCode();

		boolean result = HttpResponseVerifier.isOk(httpResponse);

		assertTrue(result);
	}

	@Test
	public void isOkWhenNotOkThenFalse() {
		doReturn(HttpStatus.SC_CONFLICT).when(statusLine).getStatusCode();

		boolean result = HttpResponseVerifier.isOk(httpResponse);

		assertFalse(result);
	}

	@Test
	public void isNotOkWhenOkThenFalse() {
		doReturn(HttpStatus.SC_OK).when(statusLine).getStatusCode();

		boolean result = HttpResponseVerifier.isNotOk(httpResponse);

		assertFalse(result);
	}

	@Test
	public void isNotOkWhenNotOkThenTrue() {
		doReturn(HttpStatus.SC_BAD_GATEWAY).when(statusLine).getStatusCode();

		boolean result = HttpResponseVerifier.isNotOk(httpResponse);

		assertTrue(result);
	}

	@Test
	public void isMovedTemporarilyWhenMovedTemporarilyThenTrue() {
		doReturn(HttpStatus.SC_MOVED_TEMPORARILY).when(statusLine).getStatusCode();

		boolean result = HttpResponseVerifier.isMovedTemporarily(httpResponse);

		assertTrue(result);
	}

	@Test
	public void isMovedTemporarilyWhenOkThenFalse() {
		doReturn(HttpStatus.SC_OK).when(statusLine).getStatusCode();

		boolean result = HttpResponseVerifier.isMovedTemporarily(httpResponse);

		assertFalse(result);
	}

	@Test
	public void isNotMovedTemporarilyWhenMovedTemporarilyThenFalse() {
		doReturn(HttpStatus.SC_MOVED_TEMPORARILY).when(statusLine).getStatusCode();

		boolean result = HttpResponseVerifier.isNotMovedTemporarily(httpResponse);

		assertFalse(result);
	}

	@Test
	public void isNotMovedTemporarilyWhenOkThenTrue() {
		doReturn(HttpStatus.SC_OK).when(statusLine).getStatusCode();

		boolean result = HttpResponseVerifier.isNotMovedTemporarily(httpResponse);

		assertTrue(result);
	}
}