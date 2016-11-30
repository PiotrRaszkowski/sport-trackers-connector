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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivitiesSearchFields {

	private int start;

	private int limit;

	private String sortField;

	private SortOrder sortOrder;

	public enum SortOrder {
		DESC("desc"),
		ASC("asc"),
		;

		private final String value;

		private SortOrder(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private List<Condition> conditions = new ArrayList<>();

	public static class Condition {
		private String field;

		private Operator operator;

		private String value;

		public Condition(String field, Operator operator, String value) {
			this.field = field;
			this.operator = operator;
			this.value = value;
		}

		public Condition(String field, Operator operator, LocalDateTime value) {
			this(field, operator, OffsetDateTime.of(value, ZoneOffset.UTC));
		}

		public Condition(String field, Operator operator, OffsetDateTime value) {
			this.field = field;
			this.operator = operator;
			this.value = value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public Operator getOperator() {
			return operator;
		}

		public void setOperator(Operator operator) {
			this.operator = operator;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public enum Operator {
		EQUAL("="),
		GREATER_THAN(">"),
		GREATER_THAN_OR_EQUAL(">="),
		LESS_THAN("&lt;"),
		LESS_THAN_OR_EQUAL("&lt;="),
		NOT_EQUAL("!="),
		CONTAINS("in"),
		IS("is"),
		;

		private final String value;

		private Operator(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public void addCondition(Condition condition) {
		this.conditions.add(condition);
	}

	public void addConditions(Condition ... conditions) {
		for (Condition condition : conditions) {
			this.conditions.add(condition);
		}
	}
}
