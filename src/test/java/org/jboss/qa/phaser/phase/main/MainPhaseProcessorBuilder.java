package org.jboss.qa.phaser.phase.main;

import org.jboss.qa.phaser.PhaseDefinitionProcessorBuilder;

import java.lang.reflect.Method;

public class MainPhaseProcessorBuilder extends PhaseDefinitionProcessorBuilder<Main> {

	public MainPhaseProcessor buildProcessor(Main annotation, Method method) {
		return new MainPhaseProcessor(annotation);
	}
}
