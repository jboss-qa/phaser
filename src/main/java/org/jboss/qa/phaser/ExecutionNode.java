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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExecutionNode {

	@NonNull private PhaseDefinition phaseDefinition;
	@NonNull private PhaseDefinitionProcessor processor;
	@Getter private List<ExecutionNode> childNodes = new ArrayList<>();

	public void addChildNode(ExecutionNode node) {
		childNodes.add(node);
	}

	public void addChildNodes(Collection<ExecutionNode> nodes) {
		childNodes.addAll(nodes);
	}

	public ExecutionError execute(boolean finalize) {

		// finalizing state, skip non run always methods
		if (finalize && !phaseDefinition.isRunAlways()) {
			return null;
		}

		try {
			// Invoke phase definition processor
			processor.execute();

			// If phase definition has method, invoke it
			if (phaseDefinition.getMethod() != null) {

				final Class<?>[] paramClasses = phaseDefinition.getMethod().getParameterTypes();
				final Annotation[][] paramAnnotations = phaseDefinition.getMethod().getParameterAnnotations();

				if (paramClasses.length == 0) { // no parameters to inject
					phaseDefinition.getMethod().invoke(phaseDefinition.getJob());
				} else {
					final List<List<Object>> paramInstances = new ArrayList<>();
					for (int i = 0; i < paramClasses.length; i++) {
						boolean created = false;
						for (int j = 0; j < paramAnnotations[i].length; j++) {
							if (paramAnnotations[i][j] instanceof Create) { // Create instance for @Create params
								final Create create = (Create) paramAnnotations[i][j];
								final Object o = paramClasses[i].newInstance();
								if (StringUtils.isNotEmpty(create.id())) {
									InstanceRegistry.insert(create.id(), o);
								} else {
									InstanceRegistry.insert(o);
								}
								// Add created instance as unique instance of parameter
								final List<Object> ip = new ArrayList<>();
								ip.add(o);
								paramInstances.add(ip);
								created = true;
							}
						}
						if (!created) { // Find all existing instances
							paramInstances.add(InstanceRegistry.get(paramClasses[i]));
						}
					}

					for (List<Object> paramList : createCartesianProduct(paramInstances)) {
						phaseDefinition.getMethod().invoke(phaseDefinition.getJob(), paramList.toArray());
					}
				}
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

	// TODO(vchalupa): Move to utils or use some util library
	private static List<List<Object>> createCartesianProduct(List<List<Object>> lists) {
		final List<List<Object>> resultLists = new ArrayList<>();
		if (lists.size() == 0) {
			resultLists.add(new ArrayList<>());
			return resultLists;
		} else {
			final List<Object> firstList = lists.get(0);
			final List<List<Object>> remainingLists = createCartesianProduct(lists.subList(1, lists.size()));
			for (Object condition : firstList) {
				for (List<Object> remainingList : remainingLists) {
					final ArrayList<Object> resultList = new ArrayList<>();
					resultList.add(condition);
					resultList.addAll(remainingList);
					resultLists.add(resultList);
				}
			}
		}
		return resultLists;
	}
}
