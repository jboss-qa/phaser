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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Executor {

	private List<ExecutionNode> roots;

	public void execute(Class<?> jobClazz) throws Exception {

		// TODO(vchalupa): field injection
		final Object instance = jobClazz.newInstance();

		// TODO(vchalupa): execute before job method

		final Queue<ExecutionNode> nodeQueue = new LinkedList<>(roots);
		while (!nodeQueue.isEmpty()) {
			final ExecutionNode node = nodeQueue.poll();
			node.execute(instance);
			nodeQueue.addAll(node.getChildNodes());
		}
	}
}
