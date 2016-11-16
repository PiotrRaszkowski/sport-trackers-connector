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

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpResponseConverterTest {

	private static final String ENTITY_CONTENT = "ENTITY CONTENT";

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpResponse httpResponse;

	@Mock
	private HttpEntity httpEntity;

	@Before
	public void setUp() throws IOException {
		when(httpEntity.getContent()).thenReturn(IOUtils.toInputStream(ENTITY_CONTENT, "utf-8"));
		when(httpEntity.getContentType()).thenReturn(null);

		when(httpResponse.getEntity()).thenReturn(httpEntity);
	}

	@Test
	public void getAsString() {
		String result = HttpResponseConverter.getAsString(httpResponse);

		assertEquals(ENTITY_CONTENT, result);
	}

	@Test
	public void getAsStringWhenNullEntityThenEmpty() {
		doReturn(null).when(httpResponse).getEntity();

		String result = HttpResponseConverter.getAsString(httpResponse);

		assertEquals("", result);
	}
}