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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class PhaseValidator {

	private static Map<Class<?>, Class<?>> primitiveTypeMap = new HashMap<>();

	static {
		primitiveTypeMap.put(Integer.TYPE, Integer.class);
		primitiveTypeMap.put(Long.TYPE, Long.class);
		primitiveTypeMap.put(Double.TYPE, Double.class);
		primitiveTypeMap.put(Float.TYPE, Float.class);
		primitiveTypeMap.put(Byte.TYPE, Byte.class);
		primitiveTypeMap.put(Short.TYPE, Short.class);
	}

	private static void validateIdPresence(Phase phase) throws PhaseValidationException {
		for (Method m : phase.getAnnotationClass().getMethods()) {
			if (m.isAnnotationPresent(Id.class)) {
				return;
			}
		}
		throw new PhaseValidationException(Id.class.getName() + " annotation not present for any method of " + phase.getAnnotationClass().getCanonicalName());
	}

	private static void validateOrderType(Phase phase) throws PhaseValidationException {
		for (Method m : phase.getAnnotationClass().getMethods()) {
			if (m.isAnnotationPresent(Order.class)) {
				Class<?> orderType = m.getReturnType();
				if (orderType.isPrimitive()) {
					final Class<?> numberType = primitiveTypeMap.get(orderType);
					if (numberType != null) {
						orderType = numberType;
					}
				}

				if (!Number.class.isAssignableFrom(orderType)) {
					throw new PhaseValidationException(Order.class.getName() + " method does not return number in " + phase.getAnnotationClass().getCanonicalName());
				}
			}
		}
	}

	public static void validate(Phase phase) throws PhaseValidationException {
		validateIdPresence(phase);
		validateOrderType(phase);
	}

	private PhaseValidator() {
	}
}
