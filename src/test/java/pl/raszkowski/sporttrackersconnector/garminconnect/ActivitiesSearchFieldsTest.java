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

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ActivitiesSearchFieldsTest {

	@InjectMocks
	private ActivitiesSearchFields activitiesSearchFields;

	@Test
	public void addCondition() {
		activitiesSearchFields.addCondition(
				new ActivitiesSearchFields.Condition("field", ActivitiesSearchFields.Operator.CONTAINS, "value")
		);

		List<ActivitiesSearchFields.Condition> conditions = activitiesSearchFields.getConditions();

		assertEquals(1, conditions.size());
	}

	@Test
	public void addConditions() {
		activitiesSearchFields.addConditions(
				new ActivitiesSearchFields.Condition("field1", ActivitiesSearchFields.Operator.CONTAINS, "value1"),
				new ActivitiesSearchFields.Condition("field2", ActivitiesSearchFields.Operator.CONTAINS, "value2")
		);

		List<ActivitiesSearchFields.Condition> conditions = activitiesSearchFields.getConditions();

		assertEquals(2, conditions.size());
	}
}