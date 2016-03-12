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

import org.apache.commons.lang3.StringUtils;

import org.jboss.qa.phaser.Create;
import org.jboss.qa.phaser.processors.ParameterProcessor;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateAnnotationProcessor implements ParameterProcessor<Create> {

	private InstanceRegistry register;

	@Override
	public List<Object> processParameter(Class clazz, Create annotation) {
		if (clazz.isAssignableFrom(InstanceRegistry.class)) {
			throw new IllegalStateException("Can not use @Create annotation for InstanceRegistry");
		}
		try {
			final Object o = clazz.newInstance();
			if (StringUtils.isNotEmpty(annotation.id())) {
				register.insert(annotation.id(), o);
			} else {
				register.insert(o);
			}
			return Collections.singletonList(o);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<Create> getAnnotationClass() {
		return Create.class;
	}
}
