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
package org.jboss.qa.phaser.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class MethodExecutor {

	private ParameterProcessor defaultProcessor;

	@Singular
	private List<ParameterProcessor> processors;

	public void invokeMethod(Method method, Object target) throws InvocationTargetException, IllegalAccessException {
		try {
			final Class<?>[] paramClasses = method.getParameterTypes();
			if (paramClasses.length == 0) { // no parameters to inject
				if (log.isDebugEnabled()) {
					log.debug("Execute method {}#{}", target.getClass().getCanonicalName(), method.getName());
				}
				method.invoke(target);
				return;
			}

			final Annotation[][] paramAnnotations = method.getParameterAnnotations();
			final ArrayList<List<Object>> params = new ArrayList<>(paramClasses.length);
			params.ensureCapacity(paramClasses.length);

			for (int i = 0; i < paramClasses.length; i++) {
				List<Object> param = Collections.emptyList();
				for (int j = 0; j < paramAnnotations[i].length; j++) {
					final Annotation annotation = paramAnnotations[i][j];
					for (ParameterProcessor processor : processors) {
						if (processor.getAnnotationClass().isAssignableFrom(annotation.getClass())) {
							param = processor.processParameter(paramClasses[i], annotation);
						}
					}
				}
				if (paramAnnotations[i].length == 0) {
					if (defaultProcessor != null) {
						param = defaultProcessor.processParameter(paramClasses[i], null);
					}
				}
				params.add(param);
			}

			for (List<Object> methodParams : createCartesianProduct(params)) {
				if (log.isDebugEnabled()) {
					log.debug("Execute method {}#{} with params: {}", new Object[] {
							target.getClass().getCanonicalName(), method.getName(), methodParams.toArray()
					});
				}
				method.invoke(target, methodParams.toArray());
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("Unable invoke method " + method.getName() + " in class " + target.getClass().getCanonicalName(), e);
		}
	}

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
