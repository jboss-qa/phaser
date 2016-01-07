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
import org.jboss.qa.phaser.registry.SimpleInstanceRegistry;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Phaser {

	private PhaseTree phaseTree;
	private List<Object> jobs;

	public Phaser(PhaseTree phaseTree, Object job) {
		this.phaseTree = phaseTree;
		this.jobs = new ArrayList<>(1);
		this.jobs.add(job);
	}

	public void run() throws Exception {
		run(new SimpleInstanceRegistry());
	}

	public void run(InstanceRegistry register) throws Exception {
		org.jboss.qa.phaser.InstanceRegistry.setRegistry(register);
		final List<ExecutionNode> executions = phaseTree.validate().buildPhaseDefinitions(jobs).buildExecutionTree();
		new Executor(jobs, executions, register).execute();
	}
}
