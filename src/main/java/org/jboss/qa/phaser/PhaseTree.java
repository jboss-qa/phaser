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
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PhaseTree {

	@Getter
	private List<PhaseTreeNode> roots;

	public PhaseTree validate() throws PhaseValidationException {
		for (PhaseTreeNode root : roots) {
			root.validate();
		}
		return this;
	}

	public PhaseTree buildPhaseDefinitions(List<Object> jobs) throws Exception {
		for (PhaseTreeNode root : roots) {
			root.buildPhaseDefinitions(jobs);
		}
		return this;
	}

	public List<ExecutionNode> buildExecutionTree(InstanceRegistry registry) throws Exception {
		final List<ExecutionNode> nds = new ArrayList<>();
		for (PhaseTreeNode root : roots) {
			nds.addAll(root.buildExecutionTree(null, registry));
		}
		return nds;
	}
}
