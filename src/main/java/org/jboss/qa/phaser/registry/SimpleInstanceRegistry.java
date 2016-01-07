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
package org.jboss.qa.phaser.registry;

import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleInstanceRegistry implements InstanceRegistry {

	private Map<String, Object> namedInstances = new HashMap<>();
	private Map<Class<?>, List<Object>> typedInstances = new HashMap<>();

	public void insert(Object o) {
		insert(o, o.getClass());
		insert(o, ClassUtils.getAllInterfaces(o.getClass()));
		insert(o, ClassUtils.getAllSuperclasses(o.getClass()));
	}

	private void insert(Object o, List<Class<?>> classes) {
		for (Class<?> c : classes) {
			insert(o, c);
		}
	}

	private void insert(Object o, Class<?> c) {
		List<Object> instances = typedInstances.get(c);
		if (instances == null) {
			instances = new ArrayList<>();
			typedInstances.put(c, instances);
		}
		instances.add(o);
	}

	public void insert(String id, Object o) {
		namedInstances.put(id, o);
		insert(o);
	}

	public <T> T get(String id, Class<T> clazz) {
		final Object o = namedInstances.get(id);
		if (o != null && clazz.isAssignableFrom(o.getClass())) {
			return (T) o;
		}
		return null;
	}

	public <T> List<T> get(Class<T> clazz) {
		final List<T> instances = (List<T>) typedInstances.get(clazz);
		return instances == null ? Collections.<T>emptyList() : instances;
	}

	@Override
	public String toString() {
		return "SimpleInstanceRegistry{"
				+ "namedInstances=" + namedInstances
				+ ", typedInstances=" + typedInstances
				+ '}';
	}
}
