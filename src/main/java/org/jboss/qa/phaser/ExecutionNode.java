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

	public void execute(Object instance) throws Exception {
		// Invoke phase definition processor
		processor.execute();

		// If phase definition has method, invoke it
		// TODO(vchalupa): solve argument injection
		if (phaseDefinition.getMethod() != null) {
			phaseDefinition.getMethod().invoke(instance);
		}
	}
}
