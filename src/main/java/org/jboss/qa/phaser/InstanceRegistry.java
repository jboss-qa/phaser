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
package org.jboss.qa.phaser;

import java.util.List;

/**
 * Old style static instace registry.
 *
 * @deprecated use {@link org.jboss.qa.phaser.registry.InstanceRegistry} instead.
 */
@Deprecated
public final class InstanceRegistry {

	private static volatile org.jboss.qa.phaser.registry.InstanceRegistry registry = null;

	public static synchronized void insert(Object o) {
		registry.insert(o);
	}

	public static synchronized void insert(String id, Object o) {
		registry.insert(id, o);
	}

	public static synchronized Object get(String id, Class<?> clazz) {
		return registry.get(id, clazz);
	}

	public static synchronized List<Object> get(Class<?> clazz) {
		return (List<Object>) registry.get(clazz);
	}

	public static synchronized void setRegistry(org.jboss.qa.phaser.registry.InstanceRegistry r) {
		registry = r;
	}

	private InstanceRegistry() {
	}
}
