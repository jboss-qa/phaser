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

import org.jboss.qa.phaser.Inject;
import org.jboss.qa.phaser.processors.FieldProcessor;
import org.jboss.qa.phaser.processors.ParameterProcessor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

@Slf4j
@AllArgsConstructor
public class InjectAnnotationProcessor implements FieldProcessor<Inject>, ParameterProcessor<Inject> {

	private InstanceRegistry register;

	@Override
	public Object processField(final Class clazz, final Inject annotation) {
		final Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new InvocationHandler() {

			@Override
			public Object invoke(Object o, Method method, Object[] args) throws Throwable {
				if (clazz.isAssignableFrom(org.jboss.qa.phaser.registry.InstanceRegistry.class)) {
					return method.invoke(register, args);
				}

				if (StringUtils.isNotEmpty(annotation.id())) {
					return method.invoke(register.get(annotation.id(), clazz), args);
				}

				final List<?> instances = register.get(clazz);
				if (instances.size() == 1) {
					return method.invoke(instances.get(0), args);
				} else if (instances.size() > 1) {
					log.warn("Can not inject {}: more instances existing", clazz);
				}

				return method.invoke(null, args);
			}
		});
		return enhancer.create();
	}

	@Override
	public Object processField(Class clazz, Inject annotation, Object value) {
		return processField(clazz, annotation);
	}

	@Override
	public Class<Inject> getAnnotationClass() {
		return Inject.class;
	}

	@Override
	public List<Object> processParameter(Class clazz, Inject annotation) {
		if (clazz.isAssignableFrom(InstanceRegistry.class)) {
			return Collections.singletonList((Object) register);
		}
		if (annotation != null && !annotation.id().isEmpty()) {
			return Collections.singletonList(register.get(annotation.id(), clazz));
		}
		return register.get(clazz);
	}
}
