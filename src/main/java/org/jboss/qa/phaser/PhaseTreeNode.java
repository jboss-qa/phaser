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

import org.jboss.qa.phaser.registry.InstanceRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

public class PhaseTreeNode {

	@Getter private PhaseTreeNode parent;
	@Getter private List<PhaseTreeNode> childNodes = new ArrayList<>();
	@Getter private Phase phase;
	private List<PhaseDefinition> phaseDefinitions;

	public PhaseTreeNode(Phase phase) {
		this.phase = phase;
	}

	public void addChild(PhaseTreeNode childNode) {
		childNode.parent = this;
		childNodes.add(childNode);
	}

	public void validate() throws PhaseValidationException {
		PhaseValidator.validate(phase);
		for (PhaseTreeNode node : childNodes) {
			node.validate();
		}
	}

	public void buildPhaseDefinitions(List<Object> jobs) throws Exception {
		phaseDefinitions = new LinkedList<>();
		for (Object job : jobs) {
			phaseDefinitions.addAll(phase.findAllDefinitions(job));
		}
		// sort phase definitions from all job instances
		Collections.sort(phaseDefinitions);

		for (PhaseTreeNode node : childNodes) {
			node.buildPhaseDefinitions(jobs);
		}
	}

	public List<ExecutionNode> buildExecutionTree(PhaseDefinition<?> parent, InstanceRegistry registry) throws Exception {
		final List<ExecutionNode> executionNodes = new LinkedList<>();

		if (phaseDefinitions.isEmpty()) {
			// no phase definition for current phase, skip and process child nodes
			for (PhaseTreeNode node : childNodes) {
				executionNodes.addAll(node.buildExecutionTree(null, registry)); // no parent ID
			}
		} else {
			for (PhaseDefinition pd : phaseDefinitions) {
				if (isAcceptable(parent, pd)) {
					final PhaseDefinitionProcessor p = phase.getPhaseDefinitionBuilder().buildProcessor(pd.getAnnotation(), pd.getMethod());
					Injector.injectFields(registry, p);
					final ExecutionNode executionNode = new ExecutionNode(pd, p);
					for (PhaseTreeNode node : childNodes) {
						executionNode.addChildNodes(node.buildExecutionTree(pd, registry));
					}
					executionNodes.add(executionNode);
				}
			}
		}
		return executionNodes;
	}

	private boolean isAcceptable(PhaseDefinition<?> parent, PhaseDefinition<?> candidate) {
		return candidate.getParentId() == null || parent != null && candidate.getParentId().equals(parent.getId());
	}
}
