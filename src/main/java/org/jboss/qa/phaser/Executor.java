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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Executor {

	private Class<?> jobClass;
	private List<ExecutionNode> roots;
	private Object instance;

	public Executor(Class<?> jobClass, List<ExecutionNode> roots) throws Exception {
		this.jobClass = jobClass;
		this.roots = roots;
		// TODO(vchalupa): field injection
		instance = jobClass.newInstance();
	}

	public void execute() throws Exception {
		invokeJobMethods(BeforeJob.class);

		final Queue<ExecutionNode> nodeQueue = new LinkedList<>(roots);
		while (!nodeQueue.isEmpty()) {
			final ExecutionNode node = nodeQueue.poll();
			node.execute(instance);
			nodeQueue.addAll(node.getChildNodes());
		}

		invokeJobMethods(AfterJob.class);
	}

	private void invokeJobMethods(Class<? extends Annotation> annotaitonClass) throws Exception {
		for (Method m : jobClass.getMethods()) {
			Annotation annotation = m.getAnnotation(annotaitonClass);
			if (annotation != null) {
				m.invoke(instance);
			}
		}
	}
}
