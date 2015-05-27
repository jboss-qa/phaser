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

public abstract class Phase<B extends PhaseDefinitionProcessorBuilder<A>, A extends Annotation> {

	@Getter private B phaseDefinitionBuilder;

	@Getter private Class<A> annotationClass;

	public Phase() throws Exception {
		phaseDefinitionBuilder = (B) ReflectionUtils.getGenericClass(getClass(), 0).newInstance();
		annotationClass = ReflectionUtils.getGenericClass(getClass(), 1);
	}

	public List<PhaseDefinition<A>> findAllOrderedDefinitions(Class<?> jobClass) throws Exception {
		final List<PhaseDefinition<A>> phaseDefinitions = new ArrayList<>();
		A annotation = jobClass.getAnnotation(annotationClass);
		if (annotation != null) {
			phaseDefinitions.add(createPhaseDefinition(annotation, null));
		}
		for (Method m : jobClass.getMethods()) {
			annotation = m.getAnnotation(annotationClass);
			if (annotation != null) {
				phaseDefinitions.add(createPhaseDefinition(annotation, m));
			}
		}
		Collections.sort(phaseDefinitions);
		return phaseDefinitions;
	}

	public PhaseDefinition<A> createPhaseDefinition(A annotation, Method method) {
		final String id = ReflectionUtils.invokeAnnotationMethod(annotation, Id.class);
		final String parentId = ReflectionUtils.invokeAnnotationMethod(annotation, ParentId.class);
		final Number order = ReflectionUtils.invokeAnnotationMethod(annotation, Order.class);

		return new PhaseDefinition<>(
				id == null || id.isEmpty() ? null : id,
				parentId == null || parentId.isEmpty() ? null : parentId,
				order,
				this,
				annotation,
				method);
	}
}
