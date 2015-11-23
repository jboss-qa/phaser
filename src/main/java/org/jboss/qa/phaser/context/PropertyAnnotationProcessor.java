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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyAnnotationProcessor {

	private Context context;

	public PropertyAnnotationProcessor(Context context) {
		this.context = context;
	}

	public void process(Object job) throws IllegalAccessException {

		Class<?> current = job.getClass();
		while (current.getSuperclass() != null) {
			for (final Field field : current.getDeclaredFields()) {
				final Property inject = field.getAnnotation(Property.class);

				if (inject != null) {
					final Class<?> type = field.getType();
					field.setAccessible(true);
					final Object value = context.get(inject.value(), type);
					if (value != null) {
						field.set(job, value);
					} else {
						context.set(inject.value(), field.get(job), type);
					}
				}
			}
			current = current.getSuperclass();
		}
	}

	public Object[] process(Method method, Object[] params) {
		final Class<?>[] paramClasses = method.getParameterTypes();
		final Annotation[][] paramAnnotations = method.getParameterAnnotations();
		if (params == null) {
			params = new Object[paramClasses.length];
		}
		if (params.length != paramClasses.length) {
			throw new IllegalStateException();
		}
		for (int i = 0; i < paramClasses.length; i++) {
			for (int j = 0; j < paramAnnotations[i].length; j++) {
				if (paramAnnotations[i][j] instanceof Property && params[i] == null) {
					final Property property = (Property) paramAnnotations[i][j];
					final Object o = context.get(property.value(), paramClasses[i]);
					params[i] = o;
				}
			}
		}
		return params;
	}
}
