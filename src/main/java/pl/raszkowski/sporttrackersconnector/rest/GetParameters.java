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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class GetParameters {

	private static final String AND_SEPARATOR = "&";
	private static final String EQUAL_SIGN = "=";

	private Map<String, String> parameters = new TreeMap<>();

	public static class CustomParameter {
		private String name;

		private String operator;

		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String get() {
			return name + operator + value;
		}
	}

	private List<CustomParameter> customParameters = new ArrayList<>();

	public void addParameter(String name, String value) {
		parameters.put(name, value);
	}

	public void addCustomParameter(String name, String operator, String value) {
		CustomParameter customParameter = new CustomParameter();
		customParameter.setName(name);
		customParameter.setOperator(operator);
		customParameter.setValue(value);

		customParameters.add(customParameter);
	}

	public void addCustomParameter(CustomParameter customParameter) {
		customParameters.add(customParameter);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public List<CustomParameter> getCustomParameters() {
		return customParameters;
	}

	void setCustomParameters(List<CustomParameter> customParameters) {
		this.customParameters = customParameters;
	}

	public boolean hasParameters() {
		return !parameters.isEmpty() || !customParameters.isEmpty();
	}

	public String getCustomQuery() {
		List<String> parameters = new ArrayList<>();

		this.parameters.entrySet().forEach(entry -> parameters.add(entry.getKey() + EQUAL_SIGN + entry.getValue()));
		this.customParameters.forEach(customParameter -> parameters.add(customParameter.get()));

		if (parameters.isEmpty()) {
			return "";
		}

		return StringUtils.join(parameters, AND_SEPARATOR);
	}
}
