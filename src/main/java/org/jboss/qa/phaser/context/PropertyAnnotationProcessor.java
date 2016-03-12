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

import org.jboss.qa.phaser.processors.FieldProcessor;
import org.jboss.qa.phaser.processors.ParameterProcessor;

import java.util.Collections;
import java.util.List;

public class PropertyAnnotationProcessor implements ParameterProcessor<Property>, FieldProcessor<Property> {

	private Context context;

	public PropertyAnnotationProcessor(Context context) {
		this.context = context;
	}

	@Override
	public Object processField(Class clazz, Property annotation) {
		return context.get(annotation.value(), clazz);
	}

	@Override
	public Object processField(Class clazz, Property annotation, Object value) {
		final Object ctxValue = processField(clazz, annotation);
		if (ctxValue == null) {
			context.set(annotation.value(), value, clazz);
			return value;
		}
		return ctxValue;
	}

	@Override
	public List<Object> processParameter(Class clazz, Property annotation) {
		return Collections.singletonList(context.get(annotation.value(), clazz));
	}

	@Override
	public Class<Property> getAnnotationClass() {
		return Property.class;
	}
}
