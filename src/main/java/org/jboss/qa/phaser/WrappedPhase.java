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

import org.jboss.qa.phaser.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class WrappedPhase<B extends PhaseDefinitionProcessorBuilder<A>, A extends Annotation, W extends Annotation> extends Phase<B, A> {

	@Getter @Setter private Class<W> annotationWrapperClass;

	public WrappedPhase() throws Exception {
		annotationWrapperClass = ReflectionUtils.getGenericClass(getClass(), 2);
	}

	@Override
	public List<PhaseDefinition<A>> findAllOrderedDefinitions(Class<?> jobClass) throws Exception {
		final List<PhaseDefinition<A>> phaseDefinitions = new ArrayList<>();
		W wrappingAnnotation = jobClass.getAnnotation(annotationWrapperClass);
		if (wrappingAnnotation != null) {
			phaseDefinitions.addAll(findWrappedDefinitions(wrappingAnnotation, null));
		}
		for (Method m : jobClass.getMethods()) {
			wrappingAnnotation = m.getAnnotation(annotationWrapperClass);
			if (wrappingAnnotation != null) {
				phaseDefinitions.addAll(findWrappedDefinitions(wrappingAnnotation, m));
			}
		}

		Collections.sort(phaseDefinitions);
		return phaseDefinitions;
	}

	private List<PhaseDefinition<A>> findWrappedDefinitions(W wrappingAnnotation, Method method) throws Exception {
		final List<PhaseDefinition<A>> phaseDefinitions = new ArrayList<>();
		for (Method m : wrappingAnnotation.annotationType().getMethods()) {
			final Class<?> type = m.getReturnType();
			if (type.isArray() && type.getComponentType().isAssignableFrom(getAnnotationClass())) {
				for (A a: (A[]) m.invoke(wrappingAnnotation)) {
					phaseDefinitions.add(createPhaseDefinition(a, method));
				}
			}
		}
		return phaseDefinitions;
	}
}
