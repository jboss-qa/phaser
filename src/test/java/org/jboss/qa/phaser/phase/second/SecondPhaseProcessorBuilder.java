package org.jboss.qa.phaser.phase.second;

import org.jboss.qa.phaser.PhaseDefinitionProcessorBuilder;

import java.lang.reflect.Method;

public class SecondPhaseProcessorBuilder extends PhaseDefinitionProcessorBuilder<Second> {

	public SecondPhaseProcessor buildProcessor(Second annotation,Method method) {
		return new SecondPhaseProcessor(annotation);
	}
}
