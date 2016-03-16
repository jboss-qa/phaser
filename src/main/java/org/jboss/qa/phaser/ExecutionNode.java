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
import org.jboss.qa.phaser.processors.MethodExecutor;
import org.jboss.qa.phaser.registry.CreateAnnotationProcessor;
import org.jboss.qa.phaser.registry.InjectAnnotationProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExecutionNode {

	@NonNull
	private PhaseDefinition phaseDefinition;
	@NonNull
	private PhaseDefinitionProcessor processor;
	@Getter
	private List<ExecutionNode> childNodes = new ArrayList<>();

	public void addChildNode(ExecutionNode node) {
		childNodes.add(node);
	}

	public void addChildNodes(Collection<ExecutionNode> nodes) {
		childNodes.addAll(nodes);
	}

	public ExecutionError execute(boolean finalize, org.jboss.qa.phaser.registry.InstanceRegistry register) {

		// finalizing state, skip non run always methods
		if (finalize && !phaseDefinition.isRunAlways()) {
			return null;
		}

		try {
			// Invoke phase definition processor
			processor.execute();

			// If phase definition has method, invoke it
			final Method method = phaseDefinition.getMethod();
			if (method != null) {
				final Class<?>[] paramClasses = method.getParameterTypes();
				final MethodExecutor.MethodExecutorBuilder builder = MethodExecutor.builder()
						.processor(new CreateAnnotationProcessor(register))
						.processor(new InjectAnnotationProcessor(register))
						.defaultProcessor(new InjectAnnotationProcessor(register));
				final List<Context> ctxs = register.get(Context.class);
				if (!ctxs.isEmpty()) {
					builder.processor(new PropertyAnnotationProcessor(ctxs.get(0)));
				}
				builder.build().invokeMethod(method, phaseDefinition.getJob());
			}
			return null; // ok, no exception
		} catch (InvocationTargetException e) {
			return generateExecutionError(e.getCause());
		} catch (Throwable e) {
			return generateExecutionError(e);
		}
	}

	private ExecutionError generateExecutionError(Throwable t) {
		if (phaseDefinition.getExceptionHandling() != null) {
			return processor.handleException(phaseDefinition.getExceptionHandling(), t);
		}
		return processor.handleException(t);
	}
}
