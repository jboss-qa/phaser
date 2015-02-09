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
package org.jboss.qa.phaser.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ReflectionUtils {

	public static <T> Class<T> getGenericClass(final Class<?> parametrizedClass, int pos) {
		return (Class<T>) ((ParameterizedType) parametrizedClass.getGenericSuperclass()).getActualTypeArguments()[pos];
	}

	public static <T> T invokeAnnotationMethod(Annotation annotation, Class<? extends Annotation> methodId) {
		for (Method am : annotation.annotationType().getMethods()) {
			if (am.isAnnotationPresent(methodId)) {
				try {
					return (T) am.invoke(annotation);
				} catch (IllegalAccessException | InvocationTargetException e) {
					log.warn("Method {} of annotation {} can not be invoked.", am.getName(), annotation.getClass().getName());
				}
			}
		}

		return null;
	}

	private ReflectionUtils() {
	}
}
