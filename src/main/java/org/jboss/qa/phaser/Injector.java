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

import org.jboss.qa.phaser.context.Context;
import org.jboss.qa.phaser.context.PropertyAnnotationProcessor;
import org.jboss.qa.phaser.processors.CdiExecutor;
import org.jboss.qa.phaser.registry.InjectAnnotationProcessor;
import org.jboss.qa.phaser.registry.InstanceRegistry;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Injector {

	private Injector() {
	}

	public static void injectFields(InstanceRegistry registry, List<Object> objs) throws Exception {
		for (final Object obj : objs) {
			injectFields(registry, obj);
		}
	}

	public static void injectFields(InstanceRegistry registry, Object obj) throws Exception {
		final CdiExecutor.CdiExecutorBuilder cdiExecutorBuilder = CdiExecutor.builder();

		final List<Context> ctxs = registry.get(Context.class);
		if (!ctxs.isEmpty()) {
			cdiExecutorBuilder.processor(new PropertyAnnotationProcessor(ctxs.get(0)));
		} else {
			log.warn("Property injection is not activated. You can activate it by adding context into instance registry");
		}
		final CdiExecutor cdiExecutor = cdiExecutorBuilder.processor(new InjectAnnotationProcessor(registry)).build();
		cdiExecutor.inject(obj);
	}
}
