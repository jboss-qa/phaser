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

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

@Slf4j
public class Executor {

	private List<Object> jobs;
	private List<ExecutionNode> roots;

	public Executor(List<Object> jobs, List<ExecutionNode> roots) throws Exception {
		this.jobs = jobs;
		this.roots = roots;
		injectFields();
	}

	public void execute() throws Exception {
		final List<ErrorReport> throwAtEnd = new LinkedList<>();
		invokeJobMethods(BeforeJob.class);

		final Queue<ExecutionNode> nodeQueue = new LinkedList<>(roots);
		boolean finalizeState = false;
		while (!nodeQueue.isEmpty()) {
			final ExecutionNode node = nodeQueue.poll();

			final ExecutionError err = node.execute(finalizeState);

			if (err != null) {
				final ExceptionHandling eh = err.getExceptionHandling();
				final ErrorReport errorReport = new ErrorReport("Exception thrown by phase execution:", err.getThrowable());
				switch (eh.getReport()) {
					case THROW_AT_END:
						throwAtEnd.add(errorReport);
						break;
					case LOG:
						ErrorReporter.report(errorReport);
						break;
					default:
						log.debug("Exception by phase execution, continue.");
				}

				if (eh.getExecution() == ExceptionHandling.Execution.IMMEDIATELY_STOP) {
					break;
				} else if (eh.getExecution() == ExceptionHandling.Execution.FINALIZE) {
					finalizeState = true;
				}
			}
			nodeQueue.addAll(node.getChildNodes());
		}

		invokeJobMethods(AfterJob.class);
		ErrorReporter.finalErrorReport(throwAtEnd);
	}

	private void invokeJobMethods(Class<? extends Annotation> annotationClass) throws Exception {
		for (Object job : jobs) {
			for (Method m : job.getClass().getMethods()) {
				final Annotation annotation = m.getAnnotation(annotationClass);
				if (annotation != null) {
					m.invoke(job);
				}
			}
		}
	}

	private void injectFields() throws Exception {
		for (final Object job : jobs) {
			Class<?> current = job.getClass();
			while (current.getSuperclass() != null) {
				for (final Field field : current.getDeclaredFields()) {
					final Inject inject = field.getAnnotation(Inject.class);

					if (inject != null) {
						final Class<?> type = field.getType();

						final Enhancer enhancer = new Enhancer();
						enhancer.setSuperclass(type);
						enhancer.setCallback(new InvocationHandler() {

							@Override
							public Object invoke(Object o, Method method, Object[] args) throws Throwable {
								if (StringUtils.isNotEmpty(inject.id())) {
									return method.invoke(InstanceRegistry.get(inject.id(), type));
								}

								final List<Object> instances = InstanceRegistry.get(type);
								if (instances.size() == 1) {
									return method.invoke(instances.get(0), args);
								} else if (instances.size() > 1) {
									log.warn("Can not inject {} in {}: more instances existing", field.getName(), job.getClass().getCanonicalName());
								}

								return method.invoke(null, args);
							}
						});

						log.debug("Creating proxy for {}", field.getName());
						field.setAccessible(true);
						// TODO(vchalupa): check and properly log final classes and classes without default constructor
						field.set(job, enhancer.create());
					}
				}
				current = current.getSuperclass();
			}
		}
	}
}
