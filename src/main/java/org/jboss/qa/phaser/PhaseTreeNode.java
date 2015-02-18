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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lombok.Getter;

public class PhaseTreeNode {

	@Getter private PhaseTreeNode parent;
	@Getter private List<PhaseTreeNode> childNodes = new ArrayList<>();
	@Getter private Phase phase;
	private Set<PhaseDefinition> phaseDefinitions = new TreeSet<>();

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

	public void buildPhaseDefinitions(Class<?> jobClass) throws Exception {
		phaseDefinitions = phase.findAllOrderedDefinitions(jobClass);
		for (PhaseTreeNode node : childNodes) {
			node.buildPhaseDefinitions(jobClass);
		}
	}

	public List<ExecutionNode> buildExecutionTree(PhaseDefinition<?> parent) {
		final List<ExecutionNode> executionNodes = new ArrayList<>();
		for (PhaseDefinition pd : phaseDefinitions) {
			if (parent == null || pd.getParentId() == null || pd.getParentId().equals(parent.getId())) {
				final ExecutionNode executionNode = new ExecutionNode(pd, phase.getPhaseDefinitionBuilder().buildProcessor(pd.getAnnotation(), pd.getMethod()));
				for (PhaseTreeNode node : childNodes) {
					executionNode.addChildNodes(node.buildExecutionTree(pd));
				}
				executionNodes.add(executionNode);
			}
		}
		return executionNodes;
	}
}
