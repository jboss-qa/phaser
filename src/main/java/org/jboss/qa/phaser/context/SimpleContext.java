/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.jboss.qa.phaser.context;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleContext implements Context {

	private Map<String, Object> context = new HashMap<>();

	public void set(String key, Object o) {
		context.put(key, o);
	}

	public void set(String key, Object value, Class<?> type) {
		set(key, value);
	}

	public <T> T get(String key, Class<T> type) {
		return get(key, null, type);
	}

	public <T> T get(String key, T value, Class<T> type) {
		return context.containsKey(key) ? (T) context.get(key) : value;
	}
}
